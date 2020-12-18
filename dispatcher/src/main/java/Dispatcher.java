import classes.Player;
import greet.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import services.ArenaService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Dispatcher extends GodvilleServiceGrpc.GodvilleServiceImplBase {
    static ConcurrentMap<Integer, Player> playerInfo = new ConcurrentHashMap<>();
    static ExecutorService pool = Executors.newCachedThreadPool();


    private final Map<String, MutablePair<ObjectInputStream, ObjectOutputStream>> loginStreams = new ConcurrentHashMap<>();
    private final Map<Integer, MutablePair<ObjectInputStream, ObjectOutputStream>> idStreams = new ConcurrentHashMap<>();
    private final String dbServerHost;
    private final int dbServerPort;
    private ObjectInputStream fromDbServer;
    private ObjectOutputStream toDbServer;

    private final List<User> authUsers;

    private Server grpcServer;

    private final ExecutorService poolForWriting = Executors.newCachedThreadPool();
    private final ExecutorService poolForListening = Executors.newSingleThreadExecutor();

    public Dispatcher(String dbServerHost, int dbServerPort, int grpcServerPort) {

        this.authUsers = new CopyOnWriteArrayList<>();

        this.dbServerHost = dbServerHost;
        this.dbServerPort = dbServerPort;

        try {
            Socket dispatcherSocket = new Socket(dbServerHost, dbServerPort);
            this.toDbServer = new ObjectOutputStream(dispatcherSocket.getOutputStream());
            toDbServer.writeObject(new DispatcherDbServerMsg("dispatcher", null));
            this.fromDbServer = new ObjectInputStream(dispatcherSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        poolForListening.execute(new Listener(fromDbServer, authUsers));


        System.out.println("GRPC Server have started on localhost:" + grpcServerPort);
        try {
            grpcServer = ServerBuilder
                    .forPort(grpcServerPort)
                    .addService(this)
                    .build()
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println(("*** shutting down gRPC server since JVM is shutting down"));
            try {
                stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.out.println("*** server shut down");
        }));
    }



    private void stop() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            poolForListening.shutdown();
            poolForWriting.shutdown();
            pool.shutdown();
            loginStreams.forEach((s, p) -> {
                try {
                    p.left.close();
                    p.right.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    @Override
    public void login(LoginData request, StreamObserver<UserLoginOuput> responseObserver) {
        String login = request.getLogin();
        String password = request.getPassword();

        try {
            if (!loginStreams.containsKey(login)) {
                Socket socket = new Socket(dbServerHost, dbServerPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                loginStreams.put(login, new MutablePair<>(ois, oos));
            }

            MutablePair<ObjectInputStream, ObjectOutputStream> pair = loginStreams.get(login);

            User stub = new User(-1, login, password, "", "");
            Sender logUser = new Sender(new DispatcherDbServerMsg(stub, "login"), pair.left, pair.right);
            Future<Object> response = poolForWriting.submit(logUser);

            DispatcherDbServerMsg msg = (DispatcherDbServerMsg) response.get();
            // логин или пароль не подошли
            if (msg.getTag().equals("badLoginPassword")) {
                responseObserver.onNext(UserLoginOuput.newBuilder().setId(-1).build());
                responseObserver.onCompleted();
                return;
            }

            // логин и пароль подошли
            Object[] objects = (Object[]) msg.getResponse();
            User user = (User) objects[0];
            Hero hero = (Hero) objects[1];

            idStreams.put(user.getId(), new MutablePair<>(pair.left, pair.right));
            //loginStreams.remove(user.getLogin());

            UserLoginOuput userLoginOuput = UserLoginOuput.newBuilder()
                    .setId(user.getId())
                    .setNickname(user.getNickname())
                    .setHealthCount(hero.getHealth())
                    .setHeroName(hero.getName())
                    .build();
            responseObserver.onNext(userLoginOuput);
            responseObserver.onCompleted();

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(RegisterData request, StreamObserver<UserRegOutput> responseObserver) {
        LoginData loginData = request.getLoginData();
        String login = loginData.getLogin();
        User stub = new User(-1, login, loginData.getPassword(), "", request.getNickname());

        try {
            Socket socket = new Socket(dbServerHost, dbServerPort);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            loginStreams.put(login, new MutablePair<>(ois, oos));

            Sender regUser = new Sender(new DispatcherDbServerMsg(stub, "registration"), ois, oos);
            Future<Object> response = poolForWriting.submit(regUser);

            DispatcherDbServerMsg msg = (DispatcherDbServerMsg) response.get();
            // если не уникальный логин, то вернем с ид -1
            if (msg.getTag().equals("badLogin")) {
                responseObserver.onNext(UserRegOutput.newBuilder().setId(-1).build());
                responseObserver.onCompleted();
                return;
            }

            // иначе отправляем фул данные и вставляем героя
            User registered = (User) msg.getResponse();
            idStreams.put(registered.getId(), new MutablePair<>(ois, oos));

            UserRegOutput regOutput = UserRegOutput.newBuilder()
                    .setId(registered.getId())
                    .setLogin(registered.getLogin())
                    .setHash(registered.getHash())
                    .setSalt(registered.getSalt())
                    .setNickname(registered.getNickname())
                    .build();
            responseObserver.onNext(regOutput);
            responseObserver.onCompleted();

            Hero hero = new Hero(-1, registered.getId(), request.getHeroname(), 100);
            Sender sendHero = new Sender(new DispatcherDbServerMsg(hero, "addHero"), ois, oos);
            poolForWriting.submit(sendHero);
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(ClientId request, StreamObserver<Empty> responseObserver) {
        int id = (int) request.getId();
        User stub = new User(id, "", "", "", "");

        if (!idStreams.containsKey(id)) {
            try {
                Socket socket = new Socket(dbServerHost, dbServerPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                idStreams.put(id, new MutablePair<>(ois, oos));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MutablePair<ObjectInputStream, ObjectOutputStream> pair = idStreams.get(id);

        Sender outUser = new Sender(new DispatcherDbServerMsg(stub, "logout"), pair.left, pair.right);
        poolForWriting.submit(outUser);
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }



    /**
     * Закрытие всех потоков
     */
    private void Close(){
        pool.shutdownNow();
    }

    @Override
    public void startDuel(ClientId request, StreamObserver<ServerIp> responseObserver) {
        System.out.println("Dispatcher.startDuel");
        super.startDuel(request, responseObserver);

        // поиск арены для клиента
        int id = (int) request.getId();
        Future<String> addressF = pool.submit(new ArenaService(id, "dispatcher/src/main/resources/arenaServersIps.properties"));
        try {
            String address = addressF.get();
            responseObserver.onNext(ServerIp.newBuilder().setIp(address).build());
            responseObserver.onCompleted();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // TODO: вернуть результат address клиенту

    }

    @Override
    public void getStatistic(ClientId request, StreamObserver<greet.Statistic> responseObserver) {
        int id = (int) request.getId();
        User stub = new User(id, "", "", "", "");

        if (!idStreams.containsKey(id)) {
            try {
                Socket socket = new Socket(dbServerHost, dbServerPort);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                idStreams.put(id, new MutablePair<>(ois, oos));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MutablePair<ObjectInputStream, ObjectOutputStream> pair = idStreams.get(id);

        Sender getStat = new Sender(new DispatcherDbServerMsg(stub, "statistic"), pair.left, pair.right);
        Future<Object> reponse = poolForWriting.submit(getStat);

        try {
            DispatcherDbServerMsg msg = (DispatcherDbServerMsg) reponse.get();
            Statistic stat = (Statistic) msg.getResponse();

            if (stat == null) {
                responseObserver.onNext(greet.Statistic.newBuilder().setLoses(0).setWins(0).build());
                responseObserver.onCompleted();
                return;
            }
            greet.Statistic statistic = greet.Statistic.newBuilder()
                    .setWins(stat.getWins())
                    .setLoses(stat.getLoses())
                    .build();
            responseObserver.onNext(statistic);
            responseObserver.onCompleted();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void check(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private static class Sender implements Callable<Object> {
        private final DispatcherDbServerMsg messageToWrite;
        private final ObjectInputStream ois;
        private final ObjectOutputStream oos;

        public Sender(DispatcherDbServerMsg messageToWrite, ObjectInputStream ois, ObjectOutputStream oos) {
            this.messageToWrite = messageToWrite;
            this.ois = ois;
            this.oos = oos;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("[.] Start sending message to db server " + LocalDateTime.now());
            oos.writeObject(messageToWrite);
            oos.flush();
            System.out.println("[.] Message has been sent " + LocalDateTime.now());

            Object response = ois.readObject();
            System.out.println("[.] Got response " + LocalDateTime.now());
            return response;
        }
    }

    private static class Listener implements Runnable {
        private final ObjectInputStream ois;
        private final List<User> auth;

        public Listener(ObjectInputStream ois, List<User> auth) {
            this.ois = ois;
            this.auth = auth;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    DispatcherDbServerMsg msg = (DispatcherDbServerMsg) ois.readObject();

                    switch (msg.getTag()) {
                        case "auth":
                            User authUser = (User) msg.getResponse();
                            System.out.println("Listener.newUser " + authUser.getId());
                            auth.add(authUser);
                            break;
                        case "out":
                            User respUser = (User) msg.getResponse();
                            System.out.println("Listener.out " + respUser.getId());
                            auth.removeIf(u -> u.getId() == respUser.getId());
                            break;
                        case "allUsers":
                            System.out.println("Listener.allUsers");
                            ((List<User>) msg.getResponse()).forEach(user -> {
                                if (!auth.contains(user)) {
                                    auth.add(user);
                                    System.out.println(user.getId());
                                }
                            });
                            break;
                    }

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
