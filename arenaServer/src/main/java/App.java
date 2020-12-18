import arenaserver.Duel;
import arenaserver.Server;
import classes.Phrases;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import socket.SocketDBService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

public class App {

    public static void main(String[] args) {
        Phrases phrases = null;
        // загружаем фразы для дуэли
        SocketDBService socketDBService = new SocketDBService();
        // если не удалось загрузить конфиг, выходим
        if(!socketDBService.loadConfig("arenaServer/src/main/resources/dbServersIps.properties")){
            System.out.println("[!] Не удалось загрузить конфиг");
            return;
        }
        phrases = socketDBService.run();
        if (phrases==null) {
            System.out.println("[!] Не удалось загрузить фразы");
            return;
        }

        Server arenaServer = new Server(phrases);
        arenaServer.run();
    }
}
