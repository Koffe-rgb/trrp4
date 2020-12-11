package arena;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import repository.Dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class ArenaMqServerConsumer implements Runnable {
    private String host = "localhost";
    private int port = 5672;
    private String username = "koffe";
    private String password = "koffe";
    private String queueName = "queue_arena_results";

    private final Dao dbManager;

    public ArenaMqServerConsumer(Dao dbManager) {
        this.dbManager = dbManager;
    }

    public ArenaMqServerConsumer(String host, int port, String username, String password, String queueName, Dao dbManager) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.queueName = queueName;
        this.dbManager = dbManager;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, true, false, false, null);
            System.out.println("[x] Waiting for messages for MQ : " + LocalDateTime.now());
            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {

                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(delivery.getBody()))) {
                    int[] pair = (int[]) ois.readObject();
                    System.out.println("[x] Received new message : " + LocalDateTime.now());

                    sendDuelResultToDb(pair);
                    System.out.println("[x] Results updated : " + LocalDateTime.now());

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            };
            channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });

        } catch (TimeoutException | IOException timeoutException) {
            timeoutException.printStackTrace();
        }

    }

    private void sendDuelResultToDb(int[] pair) {
        dbManager.upsertStatistic(pair[0], pair[1]);
    }
}
