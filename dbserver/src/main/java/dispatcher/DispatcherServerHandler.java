package dispatcher;

import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
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
import java.util.List;
import java.util.UUID;

class DispatcherServerHandler implements Runnable {
    private final Socket socket;
    private final DispatcherSocketServer mainServer;
    private final Dao dbManager;
    private final List<User> authUsers;
    private ObjectOutputStream toDispatcher;
    private ObjectInputStream fromDispatcher;

    public DispatcherServerHandler(Socket socket, DispatcherSocketServer server, Dao dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
        this.mainServer = server;
        this.authUsers = mainServer.getAuthUsers();

        try {
            fromDispatcher = new ObjectInputStream(socket.getInputStream());
            toDispatcher = new ObjectOutputStream(socket.getOutputStream());
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

        // на старте надо отправить диспетчеру список аутентифицированных юзеров
        try {
            toDispatcher.writeObject(authUsers);
            toDispatcher.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!socket.isClosed()) {
            try {
                DispatcherDbServerMsg msg = (DispatcherDbServerMsg) fromDispatcher.readObject();

                System.out.println("[.] Just have gotten new message for " + msg.getTag() + " " + LocalDateTime.now());

                User user;

                // смотрим метку сообщения
                switch (msg.getTag()) {
                    case "statistic": getStatistic(msg); break;

                    case "registration": registerUser(msg); break;

                    case "login": logIn(msg); break;

                    case "logout": mainServer.sendToAllDispatchers(new DispatcherDbServerMsg("out", msg.getUser())); break;

                    case "out": authUsers.removeIf(u ->  u.getId() == msg.getUser().getId()); break;

                    case "selectUser":
                        user = dbManager.selectUser(msg.getUser().getId());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("selectUser", user));
                        break;

                    case "selectHero":
                        Hero hero = dbManager.selectHero(msg.getHero().getId());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("selectHero", hero));
                        break;

                    case "updateUser": dbManager.updateUser(msg.getUser()); break;
                    case "addHero": dbManager.insertHero(msg.getHero()); break;
                }

                toDispatcher.flush();
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

    private void logIn(DispatcherDbServerMsg msg) throws IOException {
        String login = msg.getUser().getLogin();
        String password = msg.getUser().getHash();

        String saltFromDb = dbManager.selectSalt(login);
        String hash1 = cryptographyThis(saltFromDb + password);

        User authUser = dbManager.selectUser(login, hash1);
        Hero usersHero = dbManager.selectHero(authUser.getId());

        // todo: почему подсказка: "всегда истина"?
        if (authUser != null) {
            authUsers.add(authUser);
            mainServer.sendToAllDispatchers(new DispatcherDbServerMsg( "auth", authUser));
            toDispatcher.writeObject(new DispatcherDbServerMsg("auth", new Object[]{ authUser, usersHero }));
        } else {
            toDispatcher.writeObject(new DispatcherDbServerMsg("badLoginPassword", null));
        }
    }

    private void registerUser(DispatcherDbServerMsg msg) throws IOException {
        User user;
        user = msg.getUser();
        boolean isUnique = dbManager.isLoginUnique(user.getLogin());
        if (!isUnique) {
            toDispatcher.writeObject(new DispatcherDbServerMsg("badLogin", null));
            return;
        }

        String generatedSalt = UUID.randomUUID().toString();
        String hash = cryptographyThis(generatedSalt + user.getHash());
        user.setHash(hash);
        user.setSalt(generatedSalt);

        dbManager.insertUser(user);
        toDispatcher.writeObject(new DispatcherDbServerMsg("rega", user));
    }

    private void getStatistic(DispatcherDbServerMsg msg) throws IOException {
        Statistic statistic = dbManager.selectStatistic(msg.getUser().getId());
        toDispatcher.writeObject(new DispatcherDbServerMsg("statistic", statistic));
    }
}