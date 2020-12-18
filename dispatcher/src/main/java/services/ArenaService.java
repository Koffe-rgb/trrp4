package services;

import classes.Player;
import jdk.nashorn.internal.parser.JSONParser;
import msg.DispatcherMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Класс, возвращающий адресс свободного сервера в формате ip:port или -1
 */
public class ArenaService implements Callable<String> {
    private int id;
    private static int idPl = 0;
    private Properties properties = new Properties();
    private FileInputStream fis;
    private int serverArenaNum;
    private String propertiesFile;
    private Player player;
    private List<MutablePair<String, Integer>> arenaServerIPs;

    public ArenaService(int id, String propertiesFile, Player player) {
        this.id = id;
        this.propertiesFile = propertiesFile;
        this.arenaServerIPs = new LinkedList<>();
        this.player = player;
    }

    @Override
    public String call() {
        idPl++;
        System.out.println("Запускаем поток для очередного клиента");
        return sendToServer();
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

    private String sendToServer(){
        int arenaFreePort = -1;
        String arenaFreeIp = "";
        int arenaServer = 0;
        List<Pair<Integer, Integer>> clientsNums = new LinkedList<>();
        if(!loadConfig()) return "-1";      // возвращаем ошибку клиенту

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

        String arenaAddress = "0.0.0.0:0000";
        if (clientsNums.size()>0) {
            // отправляем самому ненагруженному нечетному
            clientsNums.sort(Comparator.comparingInt(Pair::getValue));
            int num = clientsNums.get(0).getKey();

            // TODO: если никто не ответил, отправляем юзеру сообщение об этом
            if (clientsNums.get(0).getValue()==-1) {
                return "0.0.0.0:0000";
            };
            arenaFreeIp = arenaServerIPs.get(num).getKey();
            arenaFreePort = arenaServerIPs.get(num).getValue();
            arenaAddress = sendPlayerInfo(arenaFreeIp, arenaFreePort);
        }
        return arenaAddress;
    }
    private String sendPlayerInfo(String ip, int port){
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        String arenaAddress = "0.0.0.0:0000";

        try (Socket socket = new Socket(ip, port)) {
            socket.setSoTimeout(60 * 1000);     // ждем ответа минуту
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            oos.writeObject(new DispatcherMsg(player, -1,"Player Info"));    //TODO
            oos.flush();
            DispatcherMsg respond = (DispatcherMsg) ois.readObject();   // получаем ответ от арены
            arenaAddress = respond.getRequest();
            System.out.println("[x] Диспетчер "+ip+" "+port+" - "+ respond.getRequest()+" ");

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
        return arenaAddress;
    }
}
