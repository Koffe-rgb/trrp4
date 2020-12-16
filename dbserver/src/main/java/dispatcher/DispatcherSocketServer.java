package dispatcher;

import model.User;
import msg.DispatcherDbServerMsg;
import repository.Dao;

import java.io.IOException;
import java.io.ObjectOutputStream;
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
    private final List<User> authUsers;
    private int port;

    private final List<Socket> dispatchers = new ArrayList<>();

    public DispatcherSocketServer(Dao dbManager, int port) {
        this.dbManager = dbManager;
        this.port = port;
        authUsers = new CopyOnWriteArrayList<>();
        threadPool = Executors.newCachedThreadPool();
    }

    public List<User> getAuthUsers() {
        return authUsers;
    }

    public void sendToAllDispatchers(DispatcherDbServerMsg msg) {
        if (msg.getTag().equals("out")) {
            authUsers.removeIf(user -> user.getId() == msg.getUser().getId());
        }
        for (Socket dispatcher : dispatchers) {
            try {
                ObjectOutputStream oos = new ObjectOutputStream(dispatcher.getOutputStream());
                oos.writeObject(msg);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

                DispatcherServerHandler handler = new DispatcherServerHandler(clientSocket, DispatcherSocketServer.this, dbManager);

                threadPool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
