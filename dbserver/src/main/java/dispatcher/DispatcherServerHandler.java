package dispatcher;

import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bouncycastle.util.encoders.Hex;
import repository.Dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

class DispatcherServerHandler implements Runnable {
    private final Socket socket;
    private final DispatcherSocketServer mainServer;
    private final Dao dbManager;
    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;

    public DispatcherServerHandler(Socket socket, DispatcherSocketServer mainServer, Dao dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
        this.mainServer = mainServer;

        try {
            fromClient = new ObjectInputStream(socket.getInputStream());
            toClient = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String cryptographyThis(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            return new String(Hex.encode(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void run() {
        System.out.println("[.] Dispatcher connected : " + LocalDateTime.now());

        while (!socket.isClosed()) {
            try {
                DispatcherDbServerMsg msg = (DispatcherDbServerMsg) fromClient.readObject();

                System.out.println("[.] Just have gotten new message for " + msg.getTag() + " " + LocalDateTime.now());

                // смотрим метку сообщения
                switch (msg.getTag()) {
                    case "statistic": getStatistic(msg); break;
                    case "registration": registerUser(msg); break;
                    case "login": logIn(msg); break;
                    case "logout": mainServer.sendToAllDispatchers(new DispatcherDbServerMsg("out", msg.getUser())); break;
                    case "selectUser": selectUser(msg);break;
                    case "selectHero": selectHero(msg);break;
                    case "updateUser": dbManager.updateUser(msg.getUser()); break;
                    case "addHero": dbManager.insertHero(msg.getHero()); break;
                    case "dispatcher":
                        System.out.println("DispatcherServerHandler.newDispatcher");
                        mainServer.getDispatchers().add(new MutablePair<>(fromClient, toClient));
                        toClient.writeObject(new DispatcherDbServerMsg("allUsers", mainServer.getAuthUsers()));
                        break;
                }

                System.out.println("[.] Message to dispatcher was sent " + LocalDateTime.now());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    System.out.println("[x] Dispatcher closed connection " + LocalDateTime.now());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private void selectHero(DispatcherDbServerMsg msg) throws IOException {
        Hero hero = dbManager.selectHero(msg.getHero().getId());
        DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("selectHero", hero);
        send(msg1);
    }

    private void selectUser(DispatcherDbServerMsg msg) throws IOException {
        User user = dbManager.selectUser(msg.getUser().getId());
        DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("selectUser", user);
        send(msg1);
    }

    private void logIn(DispatcherDbServerMsg msg) throws IOException {
        String login = msg.getUser().getLogin();
        String password = msg.getUser().getHash();

        String saltFromDb = dbManager.selectSalt(login);
        String hash = cryptographyThis(saltFromDb + password);

        User authUser = dbManager.selectUser(login, hash);
        if (authUser == null){
            DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("badLoginPassword", null);
            send(msg1);
            return;
        }
        // добавим аутент юзера
        mainServer.sendToAllDispatchers(new DispatcherDbServerMsg("auth", authUser));

        Hero usersHero = dbManager.selectHero(authUser.getId());
        DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("auth", new Object[]{authUser, usersHero});
        send(msg1);
    }

    private void registerUser(DispatcherDbServerMsg msg) throws IOException {
        User user = msg.getUser();
        boolean isUnique = dbManager.isLoginUnique(user.getLogin());
        if (!isUnique) {
            DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("badLogin", null);
            send(msg1);
            return;
        }

        String generatedSalt = UUID.randomUUID().toString();
        String hash = cryptographyThis(generatedSalt + user.getHash());
        user.setHash(hash);
        user.setSalt(generatedSalt);
        dbManager.insertUser(user);

        // добавим аутент юзера
        mainServer.sendToAllDispatchers(new DispatcherDbServerMsg("auth", user));

        DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("rega", user);
        send(msg1);
    }

    private void getStatistic(DispatcherDbServerMsg msg) throws IOException {
        Statistic statistic = dbManager.selectStatistic(msg.getUser().getId());
        DispatcherDbServerMsg msg1 = new DispatcherDbServerMsg("statistic", statistic);
        send(msg1);
    }

    private synchronized void send(DispatcherDbServerMsg msg1) throws IOException {
        toClient.writeObject(msg1);
        toClient.flush();
    }
}