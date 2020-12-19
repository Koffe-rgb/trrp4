import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class App {
    private static final Properties properties = new Properties();
    private static FileInputStream fis;

    public static void main(String[] args) {
        if (!loadConfig("dispatcher/src/main/resources/dbServersIps_properties.properties")){
            System.err.println("Couldn't find config file");
            return;
        }
        connect();
    }

    private static void connect() {
        boolean cont = true;
        int n = 0;
        int localPort = 8025;

        while (cont) {
            String host = properties.getProperty("ip." + n);
            int port = Integer.parseInt(properties.getProperty("port." + n));

            try {
                Socket socket = new Socket(host, port);
                socket.setSoTimeout(10 * 1000);
                // удалось подключиться
                Dispatcher dispatcher = new Dispatcher(host, port, socket, localPort);
                socket.setSoTimeout(0);
                localPort++;
                cont = false;

            } catch (IOException e) {
                System.err.println("Couldn't  connect to db server. Continue searching");
            }
            n++;
        }
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean loadConfig(String propertiesFile) {
        try {
            fis = new FileInputStream(propertiesFile);
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("WARNING!");
            System.err.println("Couldn't load config file");
            return false;
        }
        try {
            int num = Integer.parseInt(properties.getProperty("servers.num"));
        } catch (NumberFormatException e) {
            System.err.println("WARNING!");
            System.err.println("Couldn't read number of db servers");
            return false;
        }
        return true;
    }
}
