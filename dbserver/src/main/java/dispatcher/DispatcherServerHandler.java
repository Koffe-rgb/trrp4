package dispatcher;

import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
import repository.Dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class DispatcherServerHandler implements Runnable {
    private final Socket socket;
    private final Dao dbManager;
    private final List<DispatcherServerHandler> otherDispatchers;
    private final List<User> authUsers;
    private ObjectOutputStream toDispatcher;
    private ObjectInputStream fromDispatcher;

    public DispatcherServerHandler(Socket socket, List<DispatcherServerHandler> otherDispatchers, Dao dbManager) {
        this.socket = socket;
        this.dbManager = dbManager;
        this.otherDispatchers = otherDispatchers;

        authUsers = new ArrayList<>();

        try {
            fromDispatcher = new ObjectInputStream(socket.getInputStream());
            toDispatcher = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("[.] Dispatcher connected : " + LocalDateTime.now());

        // на старте надо отправить дспетчеру список аутентифицированных юзеров
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
                    case "statistic":
                        Statistic statistic = dbManager.selectStatistic(msg.getUser().getId());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("statistic", statistic));
                        break;
                    case "isUniqueLogin":
                        boolean loginUnique = dbManager.isLoginUnique(msg.getUser().getLogin());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("unique", loginUnique));
                        break;
                    case "registration":
                        user = msg.getUser();
                        dbManager.insertUser(user);
                        toDispatcher.writeObject(new DispatcherDbServerMsg("idUser", user.getId()));
                        break;
                    case "saltByLogin":
                        String salt = dbManager.selectSalt(msg.getUser().getSalt());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("salt", salt));
                        break;
                    case "login":
                        user = dbManager.selectUser(msg.getUser().getLogin(), msg.getUser().getHash());
                        if (user != null) {
                            authUsers.add(user);
                            sendToAllDispatchers(new DispatcherDbServerMsg( "auth", user));
                        } else {
                            toDispatcher.writeObject(new DispatcherDbServerMsg("badLoginPassword", null));
                        }
                        break;
                    case "selectUser":
                        user = dbManager.selectUser(msg.getUser().getId());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("selectUser", user));
                        break;
                    case "selectHero":
                        Hero hero = dbManager.selectHero(msg.getHero().getId());
                        toDispatcher.writeObject(new DispatcherDbServerMsg("selectHero", hero));
                        break;

                    case "logout":  // --
                        sendToAllDispatchers(new DispatcherDbServerMsg("logout", msg.getUser()));
                        authUsers.remove(msg.getUser());
                        break;

                    case "updateUser": dbManager.updateUser(msg.getUser()); break;
                    case "deleteUser": dbManager.deleteUser(msg.getUser()); break;
                    case "addHero": dbManager.insertHero(msg.getHero(), msg.getUser().getId()); break;
                    case "updateHero": dbManager.updateHero(msg.getHero()); break;
                    case "deleteHero": dbManager.deleteHero(msg.getHero()); break;
                }

                toDispatcher.flush();
                System.out.println("[.] Message to dispatcher was sent " + LocalDateTime.now());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToAllDispatchers(DispatcherDbServerMsg msg) {
        for (DispatcherServerHandler dispatcher : otherDispatchers) {
            try {
                dispatcher.toDispatcher.writeObject(msg);
                dispatcher.toDispatcher.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendToAllDispatchers(List<User> authUsers) {
        for (DispatcherServerHandler handler : otherDispatchers) {
            if (handler.equals(DispatcherServerHandler.this)) continue;
            try {
                handler.toDispatcher.writeObject(authUsers);
                handler.toDispatcher.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}