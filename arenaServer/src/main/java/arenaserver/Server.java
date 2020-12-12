package arenaserver;

import classes.Player;
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
    private static int _count = 0;
    static ExecutorService pool = Executors.newFixedThreadPool(10);     // один на диспетчеров, остальные для дуэлей
    static final List<Client> clientsQueue = Collections.synchronizedList(new LinkedList<>());  // хранит очередь клиентов, отправивших свой id

    static final List<Client> duelsList = Collections.synchronizedList(new LinkedList<>());     // хранит список дуэлей
    static final Hashtable<Integer, Player> playerInfoMap = new Hashtable<>(100);      // мап, хранящий инфу про клиента, полученную от диспетчера
    static ExecutorService clientPool = Executors.newCachedThreadPool();           // пул, отвечающий за обработку конкретного клиента

    

    @Override
    public void run() {
        System.out.println("Запускаем сервер арены");
        // запускаем слушателя диспетчера
//        pool.execute(new DispatcherListener());
        // запускаем слушателя клиентов
        pool.execute(new ClientListener());
        // запускаем сервис проведения дуэли
//        pool.execute(new DuelStarter());

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
    Duel _duel;
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
                    _count++;
                    if(_count==1) {
                        _duel = new Duel(client);
                        pool.execute(_duel);
                    }
                    else _duel.reconnect(client);
//                    findPair();         // при добавлении нового клиента, проверяем нельзя ли найти ему пару для дуэли

                } catch (IOException e) {
                    //if (!server.isClosed()){server.close();}
                    System.out.println("Сервер закрыт - "+server.isClosed());

                    System.out.println(e.getMessage());
                    //System.exit(1);
                }
            }
        }
    }

























}
