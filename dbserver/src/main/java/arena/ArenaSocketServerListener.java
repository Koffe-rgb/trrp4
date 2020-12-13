package arena;

import msg.DBMsg;
import repository.Dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ArenaSocketServerListener implements Runnable {
    private final Dao dbManager;
    private final ExecutorService threadPool;
    private int port;


    public ArenaSocketServerListener(Dao dbManager, int port) {
        this.dbManager = dbManager;
        this.threadPool = Executors.newFixedThreadPool(8);
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println(("[x] Shutting down Arena Listener since JVM is shutting down"));
                try {
                    if (!serverSocket.isClosed()) serverSocket.close();
                    if (!threadPool.isShutdown()) threadPool.shutdown();
                } catch (IOException e) { e.printStackTrace(); }
                System.out.println("[x] Arena Listener shut down");
            }));

            while (true) {
                System.out.println("[.] Awaiting client : " + LocalDateTime.now());
                Socket clientSocket = serverSocket.accept();
                PhraseRequestHandler handler = new PhraseRequestHandler(clientSocket, dbManager);
                threadPool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PhraseRequestHandler implements Runnable {
        private final Socket socket;
        private final Dao dbManager;

        public PhraseRequestHandler(Socket socket, Dao dbManager) {
            this.socket = socket;
            this.dbManager = dbManager;
        }

        @Override
        public void run() {
            System.out.println("[.] Client connected : " + LocalDateTime.now());

            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                DBMsg message = new DBMsg();
                message.setGoodPhrases(dbManager.selectPositivePhrases().toArray(new String[0]));
                message.setBadPhrases(dbManager.selectNegativePhrases().toArray(new String[0]));
                message.setUsualPhrases(dbManager.selectNeutralPhrases().toArray(new String[0]));

                System.out.println("[.] Phrases packed : " + LocalDateTime.now());

                oos.writeObject(message);
                oos.flush();
                System.out.println("[.] Sent to client : " + LocalDateTime.now());

            } catch (IOException e) {
                System.out.println("[x] Couldn't connect to client : " + LocalDateTime.now());
                e.printStackTrace();
            }
        }
    }
}
