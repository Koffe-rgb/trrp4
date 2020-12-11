import arena.ArenaMqServerConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import repository.Dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class App {
    private static String queueName = "queue_arena_results";

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        threadPool.execute(new ArenaMqServerConsumer(new Dao()));


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("koffe");
        factory.setPassword("koffe");
        factory.setPort(5672);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {


            channel.queueDeclare(queueName, true, false, false, null);

            oos.writeObject(new int[]{ 10, 11 });

            channel.basicPublish("", queueName,
                    new AMQP.BasicProperties().builder().build(),
                    baos.toByteArray());

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }
}
