package socket;

import classes.Player;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Класс отвечающий за обработку запроса клиента
 * Обработка очереди и проведение дуэли
 */
public class SocketClientService implements Runnable{
    private ServerSocket server;
    private Socket client;
    static ExecutorService pool = Executors.newCachedThreadPool();
    public static LinkedList<ClientHandler> clientList = new LinkedList<>();
    //public ConcurrentLinkedQueue<Future> queue = new ConcurrentLinkedQueue<>();
    private Queue<Client> queue = new LinkedList<>();
    private List<Player> clientInfo = new LinkedList<>();

    public SocketClientService(){
        try {
            server = new ServerSocket(8000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<Integer, Integer> checkQueue(){
        int size = queue.size();
        System.out.println("Воу, в очереди есть целых "+size+" клиентов");
        if (size>=2)
        {
            return new Pair<>(queue.poll(), queue.poll());
        }
    }

    @Override
    public void run() {
        while(!server.isClosed()){
            try {
//                System.out.println("Проверка очереди на запись в БД");
//                writeToDB();

                System.out.println("Ожидание нового клиента");
                client = server.accept();          // принимаем клента
                System.out.println("Новый клиент был добавлен");
                // предобщение -> получаем id клиента и проверяем, что у нас такой есть, направляем на дуэли или в очередь

                Future res = pool.submit(new ClientHandler(client));
                queue.add(res);

//                if (!client.isClosed()) client.close();

            } catch (IOException e) {
                //if (!server.isClosed()){server.close();}
                System.out.println("Сервер закрыт - "+server.isClosed());
                System.out.println(e.getMessage());
                //System.exit(1);
            }
        }
    }

    /**
     * Класс, отвечающий за обработку клиента сокета
     */
    private static class ClientHandler implements Callable<Pair<Player, Player>> {
        public Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        /**
         * Запускаемый метод
         * @return пара победитель-проигравший
         */
        @Override
        public Pair<Player, Player> call() {
            Pair<Player, Player> res = null;
            try {
                res = handleWithSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }


        private Pair<Player, Player> handleWithSocket() throws IOException {
            // получаем id клиента
            try (ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream())){
                int key = (int)ois.readObject();
                // идем в бд, чтобы получить инфу о клиенте


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                client.close();
            }
            return res;
        }




    }
    private class Client{
        Socket socket;
        Player player;

        public Client(Socket socket, Player player) {
            this.socket = socket;
            this.player = player;
        }
    }
}
