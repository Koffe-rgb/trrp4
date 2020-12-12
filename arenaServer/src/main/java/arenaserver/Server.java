package arenaserver;

import classes.Player;
import javafx.util.Pair;
import msg.ClientMsg;
import msg.DispatcherMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    static ExecutorService pool = Executors.newFixedThreadPool(10);     // один на диспетчеров, остальные для дуэлей
    static final List<Client> clientsQueue = Collections.synchronizedList(new LinkedList<>());  // хранит очередь клиентов, отправивших свой id

    static final List<Client> duelsList = Collections.synchronizedList(new LinkedList<>());     // хранит список дуэлей
    static final Hashtable<Integer, Player> playerInfoMap = new Hashtable<>(100);      // мап, хранящий инфу про клиента, полученную от диспетчера
    static ExecutorService clientPool = Executors.newCachedThreadPool();           // пул, отвечающий за обработку конкретного клиента
    static BlockingQueue<Client> pairs = new ArrayBlockingQueue<>(4);         // очередь на дуэли из 4-х пар
    static final ExecutorService duels = Executors.newCachedThreadPool();
    Socket scl1 = null, scl2 = null;


    @Override
    public void run() {
        System.out.println("Запускаем сервер арены");
        // запускаем слушателя диспетчера
//        pool.execute(new DispatcherListener());
        // запускаем слушателя клиентов
        pool.execute(new ClientListener());
        // запускаем сервис проведения дуэли
        pool.execute(new DuelStarter());

    }


    private class DuelStarter implements Runnable{
        AtomicInteger duelsCount = new AtomicInteger(0);
        @Override
        public void run() {
            try {
                while (true)
                // если число дуэлей на данный момент меньше 8, то мы можем создать новую дуэль
                synchronized (duelsCount) {
                    if (duelsCount.get() <= 8) {
                        duelsCount.addAndGet(1);
                        System.out.println("[x] Размер очереди пар "+pairs.size());
                        Client newCl = pairs.take();
                        duels.execute(new Duel(newCl, duelsCount));     // отправляем пару на дуэль
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private class DispatcherListener implements Runnable{
        private ServerSocket server;
        private Socket client;

        @Override
        public void run() {
            System.out.println("Запускаем слушателя диспетчеров");
            try {
                server = new ServerSocket(8002);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(!server.isClosed()){
                try {
                    System.out.println("[x] Ожидание диспетчера");
                    client = server.accept();          // принимаем диспетчера
                    System.out.println("[x] Диспетчер был добавлен");
                    client.setSoTimeout(20*1000);       // даем диспетчеру 20 секунд, чтобы тот сказал все, что хотел

                    try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){

                        DispatcherMsg msg = (DispatcherMsg) ois.readObject();
                        // если не получили инфу про игрока, значит нас просят вернуть колво людей на сервере
                        if (msg.getPlayer().getId()==-1){
                            oos.writeObject(new DispatcherMsg(new Player(), clientsQueue.size(), ""));
                        }
                        else{
                            System.out.println("[x] Добавляем клиента "+msg.getPlayer().getId());
                            playerInfoMap.putIfAbsent(msg.getPlayer().getId(), msg.getPlayer());        // добавляем данные про игрока
                            System.out.println("[x] Колво данных об игроках = "+playerInfoMap.size());
                            clientsQueue.add(new Client(msg.getPlayer()));     //TODO - убрать
                            oos.writeObject(new DispatcherMsg(new Player(), -1, ""));
                        }
                        oos.flush();        // отправляем ответ диспетчеру

                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        System.out.println("[x] Class not found");
                        e.printStackTrace();
                    } finally {
//                        client.close();
                    }


                } catch (IOException e) {
                    System.out.println("Сервер закрыт - "+server.isClosed());
                    System.out.println(e.getMessage());
                    //System.exit(1);
                }
            }
        }


    }
    private class ClientListener implements Runnable{
        private ServerSocket server;
        @Override
        public void run() {
            System.out.println("Запускаем слушателя клиентов");
            try {
                server = new ServerSocket(8006);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // ждем клиента
            while(!server.isClosed()){
                try {
                    System.out.println("Ожидание нового клиента");
                    // предобщение -> получаем id клиента, добавляем его в очередь
                    Socket client = server.accept();
                    pool.execute(new ClientHandler(client));
//                    findPair();         // при добавлении нового клиента, проверяем нельзя ли найти ему пару для дуэли
                    if(client!=null) System.out.println("---> "+client.isClosed());
                    pairs.add(new Client(client, new Player()));

                } catch (IOException e) {
                    //if (!server.isClosed()){server.close();}
                    System.out.println("Сервер закрыт - "+server.isClosed());

                    System.out.println(e.getMessage());
                    //System.exit(1);
                }
            }
        }
    }

    // проверяет колво людей в очереди и добавляет пару игроков в очередь на дуэль
    private void findPair(){
        System.out.println("[x] Ищем пары");
        Client cl1 = null, cl2 = null;
        synchronized (clientsQueue){
            // если в очереди есть клиенты
            if (clientsQueue.size()>0) {
                boolean isFound = false;
                int i = 0;

                // пробуем найти двух готовых игроков
                while (!isFound && i < clientsQueue.size()) {
                    System.out.println("[x] closed, conn = " + clientsQueue.get(i).socket.isClosed() + clientsQueue.get(i).socket.isConnected());
                    if (clientsQueue.get(i).socket.isConnected()) {
                        if (cl1 == null) {
                            cl1 = clientsQueue.get(i);
                        } else {
                            cl2 = clientsQueue.get(i);
                            isFound = true;
                            System.out.println("[x] Найдена пара");
                        }
                        i++;
                    } else {
                        clientsQueue.remove(i);         // удаляем клиента из очереди, если он отключился
                        System.out.println("[х] Удаляем клиента номер " + i + " из очереди ");
                    }
                }
                // добавляем пару в очередь на дуэль, если такая находится
                System.out.println("cl size " + clientsQueue.size());
                if (cl1 != null) System.out.println("[x] cl1 id " + cl1.player.getId());
                if (cl2 != null) System.out.println("[x] cl2 id " + cl2.player.getId());

                if (isFound) {
                    cl1.socket = scl1;
                    cl2.socket = scl2;
                    System.out.println("Сервер закрыт - " + cl1.socket.isClosed());
                    System.out.println("Сервер закрыт - " + cl2.socket.isClosed());
//                    if (pairs.offer(new Pair<>(cl1, cl2))) {
//                        System.out.println("[x] Добавляем клиентов в очередь");
//                        // удаляем из очереди клиентов
//                        clientsQueue.remove(cl1);
//                        clientsQueue.remove(cl2);
//                    }
                }
            }

        }

    }

    /**
     * Отвечает за получение первого сообщения от пользователя
     * и добавляет пользователя в очередь
     */
    private class ClientHandler implements Runnable {
        private Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            handleWithSocket();
        }

        /**
         * Обрабатывает первое сообщение от пользователя (получает id),
         * возвращает ответ клиенту, добавляет его в очередь
         */
        private void handleWithSocket(){
            int id = -1;
            try {
                client.setSoTimeout(60*1000);   // ждем id от клиента в течение минуты
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try (ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream())){
//                ClientMsg msg = (ClientMsg) ois.readObject();
//                id = msg.getIdClient();
                id = ois.readInt();     // TODO получение сообщения
                // проверяем, что у нас есть данные об этом клиенте
//                Player pl1 = playerInfoMap.get(id);
                Client cl;
//                if (pl1!=null) {      //Todo
                {
//                    cl = new Client(client, pl1);       // добавляем клиента в очередь, если данные найдены
                    cl = new Client(client, new Player(69));       // добавляем клиента в очередь, если данные найдены
                    System.out.println("[x] клиент: "+client.getLocalAddress()+" был добавлен");
//                    oos.writeChars("Welcome to queue");     //отправляем клиенту сообщение, что мы поставили его в очередь
                    oos.writeBoolean(true);
                    oos.flush();
                    clientsQueue.add(cl);
                    System.out.println("[x] клиентов в очереди - "+clientsQueue.size());

//                    pairs.add(cl);
                    //findPair();
                }

            } catch (IOException  e) {
                e.printStackTrace();
            }

        }
    }

}
