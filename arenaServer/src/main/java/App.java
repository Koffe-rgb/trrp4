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
//        sendResultToDB();
//        Phrases phrases = null;
//        // загружаем фразы для дуэли
//        SocketDBService socketDBService = new SocketDBService();
//        // если не удалось загрузить конфиг, выходим
//        if(!socketDBService.loadConfig("arenaServer/src/main/resources/dbServersIps.properties")){
//            return;
//        }
//
//        phrases = socketDBService.run();


        Server arenaServer = new Server();
        arenaServer.run();
    }

    private static void sendResultToDB() {
        ConnectionFactory factory = null;
        final String QUEUE_NAME = "queue_arena_results";
        final int CONNECTION_TIMEOUT = 60000; // seconds
        factory = new ConnectionFactory();
        factory.setHost("localhost");     //192.168.0.9
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(new int[]{11, 10});
                }
                channel.basicPublish("", QUEUE_NAME, null, b.toByteArray());
                System.out.println(" [x] Sent to queue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
