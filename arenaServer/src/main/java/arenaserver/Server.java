package arenaserver;

import classes.Phrases;
import classes.Player;
import msg.DispatcherMsg;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class Server implements Runnable{
    private static ExecutorService pool = Executors.newFixedThreadPool(2);     // один на диспетчеров и один под клиентов
    private static final Hashtable<Integer, Player> playerInfoMap = new Hashtable<>(100);      // мап, хранящий инфу про клиента, полученную от диспетчера
    private static ExecutorService clientPool = Executors.newCachedThreadPool();           // пул, отвечающий за обработку конкретного клиента
    private static AtomicInteger clientsCurNumber = new AtomicInteger(0);
    private Phrases phrases;
    private String  IP = "192.168.0.9";
    private int  DISPATCHER_PORT = 8016;
    private int  CLIENT_PORT = 8017;

    public Server(Phrases phrases) {
        this.phrases = phrases;
    }

    @Override
    public void run() {
        System.out.println("Запускаем сервер арены");
        // запускаем слушателя диспетчера
        pool.execute(new DispatcherListener());
        // запускаем слушателя клиентов
        pool.execute(new ClientListener());

    }

    private class DispatcherListener implements Runnable{
        private ServerSocket server;
        private Socket client;

        @Override
        public void run() {
            System.out.println("Запускаем слушателя диспетчеров");
            try {
                server = new ServerSocket(DISPATCHER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(!server.isClosed()){
                try {
                    System.out.println("Ожидание диспетчера");
                    client = server.accept();          // принимаем диспетчера
                    System.out.println("[x] Диспетчер был добавлен");
                    client.setSoTimeout(20*1000);       // даем диспетчеру 20 секунд, чтобы тот сказал все, что хотел

                    try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                         ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){

                        DispatcherMsg msg = (DispatcherMsg) ois.readObject();
                        // если не получили инфу про игрока, значит нас просят вернуть колво людей на сервере
                        if (msg.getPlayer().getId()==-1){
                            oos.writeObject(new DispatcherMsg(new Player(), clientsCurNumber.get(), ""));
                        }
                        else{
                            System.out.println("[x] Добавляем клиента "+msg.getPlayer().getId());
                            playerInfoMap.putIfAbsent(msg.getPlayer().getId(), msg.getPlayer());        // добавляем данные про игрока
                            playerInfoMap.replace(msg.getPlayer().getId(), msg.getPlayer());
                            System.out.println(msg.getPlayer().getId());
                            System.out.println("[x] Колво данных об игроках = "+playerInfoMap.size());
                            oos.writeObject(new DispatcherMsg(new Player(), -1, IP+":"+CLIENT_PORT));
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
                server = new ServerSocket(CLIENT_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // ждем клиента
            while(!server.isClosed()){
                try {
                    System.out.println("Ожидание нового клиента");
                    Socket client = server.accept();
                    System.out.println("[x] Получен новый клиент");

                    BufferedReader ois = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    PrintWriter oos = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);


                    // предобщение -> получаем id клиента, добавляем его в очередь
                    Player pl = handleWithSocket(client, oos, ois);
                    System.out.println("----> client id:");
                    System.out.println(pl.getId());
                    if(pl.getId()==-1) {
                        if (!client.isClosed())oos.close();
                        if (!client.isClosed()) ois.close();
                        if (!client.isClosed()) client.close();
                    }
                    else {
                        System.out.println("Выделяем поток под дуэль");
                        clientsCurNumber.addAndGet(1);
                        Duel _duel = new Duel(client, clientsCurNumber, ois, oos, pl, phrases);
                        clientPool.execute(_duel);
                    }
                } catch (IOException e) {
                    System.out.println("Сервер закрыт - "+server.isClosed());

                    System.out.println(e.getMessage());
                }
            }

        }
        /**
         * Обрабатывает первое сообщение от пользователя (получает id),
         * возвращает ответ клиенту, добавляет его в очередь
         */
        private Player handleWithSocket(Socket player1Socket, PrintWriter oos, BufferedReader ois){
            System.out.println("[x] Текущее колво клиентов: "+clientsCurNumber.get());
            String idS = "";
            int id = -1;
            try {
                player1Socket.setSoTimeout(60*1000);   // ждем id от клиента в течение минуты
            } catch (SocketException e) {
                e.printStackTrace();
            }
            Player pl1 = new Player(-1);
            try {
                System.out.println("Получаем ид");
                idS = ois.readLine();

                System.out.println(idS);
                // проверяем, что у нас есть данные об этом клиенте
                id = Integer.parseInt(idS);
                pl1 = playerInfoMap.get(id);

                if (pl1!=null) {
                    System.out.println("[x] клиент: "+player1Socket.getLocalAddress()+" был добавлен");
                }
                else {
                    System.out.println("[x] клиент: "+player1Socket.getLocalAddress()+" не существует");
                    pl1 = new Player(-1);
                }
                System.out.println("Ид получен");
                player1Socket.setSoTimeout(100*60*1000);   // ждем id от клиента в течение минуты
            } catch (IOException  e) {
                System.out.println("Ошибка при получении ид");
                e.printStackTrace();
            }
            return pl1;
        }
    }
}
