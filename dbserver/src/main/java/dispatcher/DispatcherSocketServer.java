package dispatcher;

import repository.Dao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DispatcherSocketServer implements Runnable {
    private final Dao dbManager;
    private final ExecutorService threadPool;
    private final List<DispatcherServerHandler> handlerList;
    private int port;

    public DispatcherSocketServer(Dao dbManager, int port) {
        this.dbManager = dbManager;
        this.port = port;
        handlerList = new CopyOnWriteArrayList<>();
        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println(("[x] Shutting down Dispatcher Server Listener since JVM is shutting down"));
                try {
                    if (!serverSocket.isClosed()) serverSocket.close();
                    if (!threadPool.isShutdown()) threadPool.shutdown();
                } catch (IOException e) { e.printStackTrace(); }
                System.out.println("[x] Dispatcher Listener shut down");
            }));

            while (true) {
                System.out.println("[.] Awaiting client-dispatcher on port " + port + " : " + LocalDateTime.now());
                Socket clientSocket = serverSocket.accept();

                DispatcherServerHandler handler = new DispatcherServerHandler(clientSocket, handlerList, dbManager);
                handlerList.add(handler);

                threadPool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
