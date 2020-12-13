package socket;

import classes.Phrases;
import classes.Player;
import javafx.util.Pair;
import msg.DBMsg;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.*;

public class SocketDBService {
    private Properties properties = new Properties();
    private FileInputStream fis;
    private int serverDbNum;

    public SocketDBService() {

    }

    public boolean loadConfig(String propertiesFile) {
        try {
            fis = new FileInputStream(propertiesFile); //"src/main/resources/JdbcConfig.properties"
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Не удалось загрузить конфиг файл");
            e.printStackTrace();
            return false;
        }
        try {
            serverDbNum = Integer.parseInt(properties.getProperty("servers.num"));
        } catch (NumberFormatException e) {
            System.out.println("Не удалось прочитать колво серверов дб");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Phrases run() {
        boolean continueSearching = true;
        int serverDb = 0;
        Phrases phrases = null;

        while (continueSearching) {
            ObjectInputStream ois = null;
            String ip;
            int port;
            ip = properties.getProperty("ip."+serverDb);
            port = Integer.parseInt(properties.getProperty("port."+serverDb));
            try (Socket socket = new Socket(ip, port)) {
                socket.setSoTimeout(60 * 1000);     // ждем ответа минуту

                ois = new ObjectInputStream(socket.getInputStream());
                DBMsg msg = (DBMsg) ois.readObject();       // получаем фразы от дб
                phrases = new Phrases(msg.getUsualPhrases(), msg.getBadPhrases(), msg.getGoodPhrases());

                System.out.println();
                continueSearching = false;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[x] Ошибка подключения к бд серверу");
                e.printStackTrace();
            } finally {
                try {
                    if (ois != null) ois.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            serverDb++;
            serverDb = serverDb >= serverDbNum ? 0 : serverDb;        // если нам никто не ответил, идем по новой
        }
        return phrases;
    }
}
