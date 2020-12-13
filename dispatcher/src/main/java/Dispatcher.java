import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
import org.bouncycastle.util.encoders.Hex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher {
    private ObjectInputStream fromDbServer;
    private ObjectOutputStream toDbServer;

    private final ExecutorService poolForWriting = Executors.newSingleThreadExecutor();

    private final List<User> authUsers;

    public Dispatcher(String host, int port) {

        this.authUsers = new ArrayList<>();

        try {
            Socket socket = new Socket(host, port);
            toDbServer = new ObjectOutputStream(socket.getOutputStream());
            fromDbServer = new ObjectInputStream(socket.getInputStream());


            // принимаем список аутентифицированных юзеров
            authUsers.addAll(((List<User>) fromDbServer.readObject()));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        ExecutorService poolForListening = Executors.newSingleThreadExecutor();
        poolForListening.execute(new Listener());
    }

    public void requestStatistic(User user) {
        write(new DispatcherDbServerMsg(user, "statistic"));
    }

    public void requestUser(int id) {
        User user = new User(id, null, null, null, null);
        write(new DispatcherDbServerMsg(user, "selectUser"));
    }

    public void requestHero(int id) {
        Hero hero = new Hero(id, -1, null, -1);
        write(new DispatcherDbServerMsg(hero, "selectHero"));
    }

    public void logout(User user) {
        authUsers.remove(user);
        write(new DispatcherDbServerMsg(user, "logout"));
    }

    public void updateUser(User user) {
        write(new DispatcherDbServerMsg(user, "updateUser"));
    }

    public void deleteUser(User user) {
        write(new DispatcherDbServerMsg(user, "deleteUser"));
    }

    public void insertHero(Hero hero, int idUser) {
        User user = new User(idUser, null, null, null, null);
        write(new DispatcherDbServerMsg(user, hero, "insertHero"));
    }

    public void updateHero(Hero hero) {
        write(new DispatcherDbServerMsg(hero, "updateHero"));
    }

    public void deleteHero(Hero hero) {
        write(new DispatcherDbServerMsg(hero, "deleteHero"));
    }

    private void write(DispatcherDbServerMsg msg) {
        poolForWriting.execute(new Writer(msg));
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

    private class Writer implements Runnable {
        private final DispatcherDbServerMsg messageToWrite;

        public Writer(DispatcherDbServerMsg messageToWrite) {
            this.messageToWrite = messageToWrite;
        }

        @Override
        public void run() {
            try {
                // todo : написать перенаправление на другой сервер при падении бд сервера
                System.out.println("[.] Start sending message to db server " + LocalDateTime.now());
                toDbServer.writeObject(messageToWrite);
                toDbServer.flush();
                System.out.println("[.] Message has been sent " + LocalDateTime.now());

            } catch (IOException e) {
                System.out.println("[x] The DbServer was shut down probably " + LocalDateTime.now());
                e.printStackTrace();
            }
        }
    }

    private class Listener implements Runnable {
        public void run() {
            System.out.println("[.] Start listening db server " + LocalDateTime.now());

            while (true) {

                try {
                    DispatcherDbServerMsg msg = (DispatcherDbServerMsg) fromDbServer.readObject();

                    switch (msg.getTag()) {
                        case "statistic":
                            Statistic stat = (Statistic) msg.getResponse();
                            System.out.printf("%d, %d, %d\n", stat.getIdUser(), stat.getWins(), stat.getLoses());
                            // todo: обработка статистики
                            break;
                        case "unique":  // промежуточный этап регистрации
                            boolean isUniqueLogin = (Boolean) msg.getResponse();
                            if (isUniqueLogin) {
                                // todo: прокинуть сюда логин, пароль, никнейм / написать отдельным методом то, что ниже
//                                String login = "log";
//                                String password = "pass";
//                                String nickname = "nicky";
//
//                                String salt = UUID.randomUUID().toString();
//                                String hash = cryptographyThis(salt + password);
//
//                                User user = new User(-1, login, hash, salt, nickname);
//                                DispatcherMsg userToRegister = new DispatcherMsg(user, "registration");
//
//                                poolForWriting.execute(new Writer(userToRegister));
                            } else {
                                // todo: сказать, что логин занят
                                System.out.println("u r loser!!1");
                            }

                            break;
                        case "idUser":
                            int id = (Integer) msg.getResponse();
                            System.out.println(id);
                            // todo: обработка id зарегистрированного пользователя

                            break;
                        case "salt": { // промежуточный этап аутентификации
                            String salt = (String) msg.getResponse();
                            // todo: прокинуть сюда логин, пароль / написать отдельным методом то, что ниже
//                            String password = "pass";
//                            String login = "log";
//
//                            String hash = cryptographyThis(salt + password);
//
//                            User user = new User(-1, login, hash, "", "");
//                            DispatcherMsg loginHash = new DispatcherMsg(user, "login");
//
//                            poolForWriting.execute(new Writer(loginHash));

                            break;
                        }
                        case "auth": {   // получение аутентифицированного юзера от диспетчера
                            User user = (User) msg.getResponse();
                            boolean empty = authUsers.stream().noneMatch(u -> u.getId() == user.getId());
                            if (empty)
                                authUsers.add(user);
                            // todo: другая обработка аутентификации
                            break;
                        }
                        case "logout":
                            User userToDelete = (User) msg.getResponse();
                            System.out.println(userToDelete.getLogin());
                            authUsers.remove(userToDelete);
                            break;
                        case "badLoginPassword":
                            // todo : сказать что плохие логин / пароль
                            System.out.println("bad login or password");
                            break;
                        case "selectUser":
                            User selectedUser = (User) msg.getResponse();
                            // todo : обработка получения пользователя по id
                            System.out.println(selectedUser.getLogin());
                            break;
                        case "selectHero":
                            Hero selectedHero = (Hero) msg.getResponse();
                            // todo : обработка получения героя по id
                            System.out.println(selectedHero.getName());
                            break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
