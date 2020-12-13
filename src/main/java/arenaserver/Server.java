package arenaserver;

import classes.Player;
import msg.ClientMsg;
import msg.DispatcherMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

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
    static ConcurrentLinkedQueue<Client> queue = new ConcurrentLinkedQueue<>();     // очередь клиентов
    static final List<Client> clientsQueue = Collections.synchronizedList(new LinkedList<>());  // хранит пару <id, socket> - очередь на дуэль
    static final List<Client> duelsList = Collections.synchronizedList(new LinkedList<>());     // хранит список дуэлей
    static final ConcurrentMap<Integer, Player> playerInfoMap = new ConcurrentHashMap<>();      // мап, хранящий инфу про клиента
    static ExecutorService clientPool = Executors.newCachedThreadPool();           // пул, отвечающий за обработку конкретного клиента
    static BlockingQueue<Pair<Client, Client>> pairs = new ArrayBlockingQueue<>(4);         // очередь на дуэли из 4-х пар


    @Override
    public void run() {
        // запускаем слушателя диспетчера
        pool.execute(new DispatcherListener());
        // запускаем слушателя клиентов
        pool.execute(new ClientListener());
        // запускаем сервис проведения дуэли
        pool.execute(new DuelStarter());

    }


    private class DuelStarter implements Runnable{
        AtomicInteger duelsCount = new AtomicInteger(0);
        ExecutorService duels = Executors.newFixedThreadPool(8);
        @Override
        public void run() {
            try {
                // если число дуэлей на данный момент меньше 8, то мы можем создать новую дуэль
                synchronized (duelsCount) {
                    if (duelsCount.get() <= 8) {
                        duelsCount.addAndGet(1);
                        duels.execute(new Duel(pairs.take(), duelsCount));     // отправляем пару на дуэль
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
                    client.setSoTimeout(10*1000);       // даем диспетчеру 20 секунд, чтобы тот сказал все, что хотел

                    try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){

                        DispatcherMsg msg = (DispatcherMsg) ois.readObject();
                        // если не получили инфу про игрока, значит нас просят вернуть колво людей на сервере
                        if (msg.getPlayer()==null){
                            oos.writeObject(new DispatcherMsg(queue.size()));
                        }
                        else{
                            playerInfoMap.putIfAbsent(msg.getPlayer().getId(), msg.getPlayer());        // добавляем данные про игрока
                            // clientsQueue.add(new Client(msg.getPlayer()));      // заводим данные на нового клиента и добавляем их в список
                            oos.writeObject(new DispatcherMsg(-1));
                        }
                        oos.flush();        // отправляем ответ диспетчеру

                    } catch (ClassNotFoundException| SocketTimeoutException e) {
                        e.printStackTrace();
                    } finally {
                        client.close();
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
            // ждем клиента
            while(!server.isClosed()){
                try {

                    System.out.println("Ожидание нового клиента");
                    Socket client = server.accept();          // принимаем клиента
                    System.out.println("Новый клиент был добавлен");
                    // предобщение -> получаем id клиента, добавляем его в очередь
                    pool.execute(new ClientHandler(client));
                    findPair();         // при добавлении нового клиента, проверяем нельзя ли найти ему пару для дуэли



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
        Client cl1 = null, cl2 = null;
        int cl1Index = -1, cl2Index = -1;
        synchronized (clientsQueue){
            // если в очереди есть клиенты
            if (clientsQueue.size()>0){
                boolean isFound = false;
                int i = 0;

                // пробуем найти двух готовых игроков
                while (!isFound && i<clientsQueue.size()){
                    if (clientsQueue.get(i).socket.isConnected()) {
                        if (cl1 == null) {
                            cl1 = clientsQueue.get(i);
                            cl1Index = i;
                        } else {
                            cl2 = clientsQueue.get(i);
                            isFound = true;
                            cl2Index = i;
                        }
                        i++;
                    }
                    else{
                        clientsQueue.remove(i);         // удаляем клиента из очереди, если он отключился
                        System.out.println("[х] Удаляем клиента номер "+i+" из очереди");
                    }
                }
                // добавляем пару в очередь на дуэль, если такая находится
                if(pairs.offer(new MutablePair<>(cl1, cl2))){
                    // удаляем из очереди клиентов
                    clientsQueue.remove(cl1);
                    clientsQueue.remove(cl2);
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
            //return handleWithSocket();
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
                ClientMsg msg = (ClientMsg) ois.readObject();
                id = msg.getIdClient();
                // проверяем, что у нас есть данные об этом клиенте
                Player pl1 = playerInfoMap.get(id);
                Client cl;
                if (pl1!=null) {
                    cl = new Client(client, pl1);       // добавляем клиента в очередь, если данные найдены
                    clientsQueue.add(cl);
                    oos.writeChars("Welcome to queue");     //отправляем клиенту сообщение, что мы поставили его в очередь
                    oos.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
