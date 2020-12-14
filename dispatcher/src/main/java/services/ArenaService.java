package services;

import classes.Player;
import msg.DispatcherMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArenaService implements Runnable{
    private int id;
    private static int idPl = 0;
    private Properties properties = new Properties();
    private FileInputStream fis;
    private int serverArenaNum;
    private String propertiesFile;
    private List<MutablePair<String, Integer>> arenaServerIPs;

    public ArenaService(int id, String propertiesFile) {
        this.id = id;
        this.propertiesFile = propertiesFile;
        this.arenaServerIPs = new LinkedList<>();
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
            arenaServerIPs.add(new MutablePair<>(ip, port));

            System.out.println("[x] Сервер арены: "+ip+ " "+ port);
            try (Socket socket = new Socket(ip, port);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                socket.setSoTimeout(60 * 1000);     // ждем ответа минуту
                oos.writeObject(new DispatcherMsg(new Player(), -1,"How many clients?"));
                oos.flush();
                DispatcherMsg respond = (DispatcherMsg) ois.readObject();   // получаем колво клиентов
                clientsNums.add(new MutablePair<>(arenaServer,respond.getRespond()));

                System.out.println("[x] У диспетчера "+ip+" "+port+" - "+ respond.getRespond()+" клиентов");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[!] Ошибка подключения к серверу арены");
                e.printStackTrace();
                clientsNums.add(new MutablePair<>(arenaServer,-1));    // если не получили ответа
            }

            arenaServer++;
        }

        if (clientsNums.size()>0) {
            // отправляем самому ненагруженному нечетному
            clientsNums.sort(Comparator.comparingInt(Pair::getValue));

            int num = clientsNums.get(0).getKey();

            // TODO: если никто не ответил, отправляем юзеру сообщение об этом
            if (clientsNums.get(0).getValue()==-1) {};
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
