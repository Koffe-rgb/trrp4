package services;

import javafx.util.Pair;
import msg.ClientMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArenaService implements Runnable{
    private int id;
    private CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs;     //TODO: заменить на один ip сервера
    private static int idPl = 0;
    private Socket socket;
    private ExecutorService pool = Executors.newFixedThreadPool(2);
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public ArenaService(int id, CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs) {
        this.arenaServerIPs = arenaServerIPs;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Запускаем поток для очередного клиента");
        communicateWithServer();
    }


    private void communicateWithServer(){
        String ip = arenaServerIPs.get(0).getKey();
        int port = arenaServerIPs.get(0).getValue();

        System.out.println("[x] Сервер арены: "+ip+ " "+ port);

        // создаем сокет для подключения
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: сообщение клиенту, что не смогли достучаться до сервера арены
            Close();
            return;
        }

        // отправляем id серверу
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            socket.setSoTimeout(20 * 1000);     // ждем ответа минуту
            oos.writeInt(1);
            oos.flush();
            System.out.println("[x] id отправлен серверу "+ip+" "+port);

        } catch (IOException e) {
            System.out.println("[x] Ошибка подключения к серверу арены");
            e.printStackTrace();
            // TODO: сообщение клиенту, что не смогли достучаться до сервера арены
            // причины -> 1)сервер упал, 2)на сервере нет информации про клиента
            // решение -> кинуть на главный экран, пусть еще раз стучиться по диспетчерам
            Close();
            return;
        }

        // если нормально связались с сервером, начинаем дуэль
        pool.execute(new Reader());
//        pool.execute(new Sender());
    }

    /**
     * Метод закрытия соединения
     */
    private void Close(){
        System.out.println("[x] Закрываем дуэль");
        try {
            if(ois!=null) ois.close();
            if(oos!=null) oos.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdownNow();
    }

    /**
     * Класс отправки сообщений
     *
     */
    private class Sender implements Runnable{
        // TODO: нужна блокировка кнопок в не свой ход
        @Override
        public void run() {
            try {
                sendMsg();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        private void sendMsg() throws InterruptedException {
            System.out.println("[x] is socket closed? "+socket.isClosed());
            while(!socket.isClosed()){
                System.out.println("[x] Sleeping...");
                Thread.sleep(7*1000);
                System.out.println("[x] Waking up!");
                try {
                    System.out.println("Writing msg...");
                    oos.writeInt(77);
                    oos.flush();
                    System.out.println("Msg was sent");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Класс получения сообщений
     */
    private class Reader implements Runnable{
        @Override
        public void run() {
            readMsg();
            Close();
        }
        private void readMsg(){
            // Выход возможен при тайм-ауте на получение (через ошибку),
            // при отказе сокета
            // или при получении последнего хода
            while (true){
                try {
                    ClientMsg msg = (ClientMsg) ois.readObject();
                    System.out.println("[x] "+msg.getType()+" "+msg.getPhrase()+" "+msg.getLives()+" "+msg.getEnemyLives());
                    if (msg.getType()==0)
                        break;
                } catch (IOException | ClassNotFoundException e) {
                    if (e instanceof SocketTimeoutException){
                        // TODO: сообщение клиенту что с сервером проблемы
                        System.out.println("[e] Time-out");
                    }else {
                        e.printStackTrace();
                        // TODO: спросить о переподключении
                    }

                    break;
                }
            }
        }
    }

}
