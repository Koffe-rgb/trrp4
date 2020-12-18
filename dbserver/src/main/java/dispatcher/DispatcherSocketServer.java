package dispatcher;

import model.User;
import msg.DispatcherDbServerMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import repository.Dao;

import java.io.IOException;
import java.io.ObjectInputStream;
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
    private final int port;

    private final List<MutablePair<ObjectInputStream, ObjectOutputStream>> dispatchers = new CopyOnWriteArrayList<>();

    public DispatcherSocketServer(Dao dbManager, int port) {
        this.dbManager = dbManager;
        this.port = port;
        authUsers = new CopyOnWriteArrayList<>();
        threadPool = Executors.newCachedThreadPool();
    }

    public List<User> getAuthUsers() {
        return authUsers;
    }

    public List<MutablePair<ObjectInputStream, ObjectOutputStream>> getDispatchers() {
        return dispatchers;
    }

    public void sendToAllDispatchers(DispatcherDbServerMsg msg) {
        User user = (User) msg.getResponse();
        if (msg.getTag().equals("out")) {
            authUsers.removeIf(u -> u.getId() == user.getId());
            System.out.println("DispatcherSocketServer.out " + user.getId());
        } else if (msg.getTag().equals("auth")) {
            System.out.println("DispatcherSocketServer.auth " + user.getId());
            authUsers.add(user);
        }
        for (MutablePair<ObjectInputStream, ObjectOutputStream> p : dispatchers) {
            ObjectOutputStream oos = p.right;
            try {
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
                System.out.println("[.] Awaiting client on port " + port + " : " + LocalDateTime.now());
                Socket clientSocket = serverSocket.accept();

                DispatcherServerHandler handler = new DispatcherServerHandler(clientSocket, DispatcherSocketServer.this, dbManager);

                threadPool.execute(handler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
