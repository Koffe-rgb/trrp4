package services;

import classes.Player;
import javafx.util.Pair;
import msg.DispatcherMsg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaService implements Runnable{
    private int id;
    private CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs;
    private static int idPl = 0;
    private Properties properties = new Properties();
    private FileInputStream fis;
    private int serverArenaNum;
    private String propertiesFile;

    public ArenaService(int id, CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs, String propertiesFile) {
        this.arenaServerIPs = arenaServerIPs;
        this.id = id;
        this.propertiesFile = propertiesFile;
    }

    @Override
    public void run() {
        idPl++;
        System.out.println("Запускаем поток для очередного клиента");
        sendToServer();
    }
    public boolean loadConfig() {
        try {
            fis = new FileInputStream(propertiesFile); //"src/main/resources/JdbcConfig.properties"
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Не удалось загрузить конфиг файл");
            e.printStackTrace();
            return false;
        }
        try {
            serverArenaNum = Integer.parseInt(properties.getProperty("servers.num"));
        } catch (NumberFormatException e) {
            System.out.println("Не удалось прочитать колво серверов дб");
            e.printStackTrace();
            return false;
        }
        return true;
    }



    private void sendToServer(){
        int arenaServer = 0;
        List<Pair<Integer, Integer>> clientsNums = new LinkedList<>();
        if(!loadConfig()) return;

        while (arenaServer < serverArenaNum) {
            String ip;
            int port;
            ip = properties.getProperty("ip."+arenaServer);
            port = Integer.parseInt(properties.getProperty("port."+arenaServer));

            System.out.println("[x] Сервер арены: "+ip+ " "+ port);
            try (Socket socket = new Socket(ip, port);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                socket.setSoTimeout(60 * 1000);     // ждем ответа минуту
                oos.writeObject(new DispatcherMsg(new Player(), -1,"How many clients?"));
                oos.flush();
                DispatcherMsg respond = (DispatcherMsg) ois.readObject();   // получаем колво клиентов
                clientsNums.add(new Pair<>(arenaServer,respond.getRespond()));

                System.out.println("[x] У диспетчера "+ip+" "+port+" - "+ respond.getRespond()+" клиентов");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[x] Ошибка подключения к серверу арены");
                e.printStackTrace();
                clientsNums.add(new Pair<>(arenaServer,-1));    // если не получили ответа
            }

            arenaServer++;
        }

        if (clientsNums.size()>0) {
            // отправляем самому ненагруженному нечетному
            clientsNums.sort(new Comparator<Pair<Integer, Integer>>() {
                @Override
                public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                    return Integer.compare(o1.getValue(), o2.getValue());
                }
            });

            int num = clientsNums.get(0).getKey();
            sendPlayerInfo(arenaServerIPs.get(num).getKey(), arenaServerIPs.get(num).getValue());
        }
    }
    private void sendPlayerInfo(String ip, int port){
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try (Socket socket = new Socket(ip, port)) {
            socket.setSoTimeout(60 * 1000);     // ждем ответа минуту
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject(new DispatcherMsg(new Player(idPl), -1,"Player Info"));    //TODO
            oos.flush();
            DispatcherMsg respond = (DispatcherMsg) ois.readObject();   // получаем ответ от арены

            System.out.println("[x] Диспетчер "+ip+" "+port+" - "+ respond.getRespond()+" ");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[x] Ошибка подключения к серверу арены");
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) ois.close();
                if (oos!=null) oos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
