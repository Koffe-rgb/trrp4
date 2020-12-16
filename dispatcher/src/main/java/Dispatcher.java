import classes.Player;
import greet.*;
import io.grpc.stub.StreamObserver;
import model.Hero;
import model.Statistic;
import model.User;
import msg.DispatcherDbServerMsg;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import services.ArenaService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Dispatcher extends GodvilleServiceGrpc.GodvilleServiceImplBase {
    private ObjectInputStream fromDbServer;
    private ObjectOutputStream toDbServer;

    static ConcurrentMap<Integer, Player> playerInfo = new ConcurrentHashMap<>();
    static ExecutorService pool = Executors.newCachedThreadPool();
    static BlockingQueue<Integer> clientsQueue = new LinkedBlockingQueue<>();
    static CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs = new CopyOnWriteArrayList();     // адрес и порт

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
    }

    public static void main(String[] args) {
        for(int i=0; i<10; i++){
            clientsQueue.add(i);
        }
        arenaServerIPs.add(new MutablePair<>("localhost", 8002));

        for(int i=0; i<10; i++) {
            try {
                pool.execute(new ArenaService(clientsQueue.take(), arenaServerIPs, "realDispatcher/src/main/resources/arenaServersIps.properties"));
                Thread.sleep(5*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pool.shutdown();

        // до бесконечности
//        while (true) {
//
//            try {
//                pool.execute(new ArenaService(clientsQueue.take(), arenaServerIPs));
//                Thread.sleep(5*1000);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    @Override
    public void login(LoginData request, StreamObserver<UserLoginOuput> responseObserver) {
        String login = request.getLogin();
        String password = request.getPassword();
        User userToLogIn = new User(-1, login, password, "", "");
        Sender logUser = new Sender(new DispatcherDbServerMsg(userToLogIn, "login"));
        Future<Object> response = poolForWriting.submit(logUser);

        try {
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

            UserLoginOuput userLoginOuput = UserLoginOuput.newBuilder()
                    .setId(user.getId())
                    .setNickname(user.getNickname())
                    .setHealthCount(hero.getHealth())
                    .setHeroName(hero.getName())
                    .build();
            responseObserver.onNext(userLoginOuput);
            responseObserver.onCompleted();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(RegisterData request, StreamObserver<UserRegOutput> responseObserver) {
        LoginData loginData = request.getLoginData();
        User stub = new User(-1, loginData.getLogin(), loginData.getPassword(), "", request.getNickname());
        Sender regUser = new Sender(new DispatcherDbServerMsg(stub, "registration"));
        Future<Object> response = poolForWriting.submit(regUser);

        try {
            DispatcherDbServerMsg msg = (DispatcherDbServerMsg) response.get();
            // если не уникальный логин, то вернем с ид -1
            if (msg.getTag().equals("badLogin")) {
                responseObserver.onNext(UserRegOutput.newBuilder().setId(-1).build());
                responseObserver.onCompleted();
                return;
            }

            // иначе отправляем фул данные и вставляем героя
            User registered = (User) msg.getResponse();
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
            Sender sendHero = new Sender(new DispatcherDbServerMsg(hero, "addHero"));
            poolForWriting.submit(sendHero);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout(ClientId request, StreamObserver<Empty> responseObserver) {
        int id = (int) request.getId();
        User userToLogOut = new User(id, "", "", "", "");
        Sender outUser = new Sender(new DispatcherDbServerMsg(userToLogOut, "logout"));
        poolForWriting.submit(outUser);
        responseObserver.onCompleted();
    }

    @Override
    public void startDuel(ClientId request, StreamObserver<ServerIp> responseObserver) {
        super.startDuel(request, responseObserver);
    }

    @Override
    public void getStatistic(ClientId request, StreamObserver<greet.Statistic> responseObserver) {
        int id = (int) request.getId();
        User whosStatistic = new User(id, "", "", "", "");
        Sender getStat = new Sender(new DispatcherDbServerMsg(whosStatistic, "statistic"));
        Future<Object> reponse = poolForWriting.submit(getStat);

        DispatcherDbServerMsg msg = (DispatcherDbServerMsg) reponse;
        Statistic stat = (Statistic) msg.getResponse();

        greet.Statistic statistic = greet.Statistic.newBuilder()
                .setWins(stat.getWins())
                .setLoses(stat.getLoses())
                .build();
        responseObserver.onNext(statistic);
        responseObserver.onCompleted();
    }

    // todo
    @Override
    public void check(Empty request, StreamObserver<Empty> responseObserver) {
        super.check(request, responseObserver);
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

    private class Sender implements Callable<Object> {
        private final DispatcherDbServerMsg messageToWrite;

        public Sender(DispatcherDbServerMsg messageToWrite) {
            this.messageToWrite = messageToWrite;
        }

        @Override
        public Object call() throws Exception {
            System.out.println("[.] Start sending message to db server " + LocalDateTime.now());
            toDbServer.writeObject(messageToWrite);
            toDbServer.flush();
            System.out.println("[.] Message has been sent " + LocalDateTime.now());

            Object response = fromDbServer.readObject();
            System.out.println("[.] Got response " + LocalDateTime.now());
            return response;
        }
    }
}
