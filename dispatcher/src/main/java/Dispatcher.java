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

    private final ExecutorService poolForWriting = Executors.newSingleThreadExecutor();

    private final List<User> authUsers;

    private int grpcServerPort;
    private Server grpcServer;

    public Dispatcher(String dbServerHost, int dbServerPort, int grpcServerPort) {

        this.authUsers = new ArrayList<>();



        try {
            Socket socket = new Socket(dbServerHost, dbServerPort);
            toDbServer = new ObjectOutputStream(socket.getOutputStream());
            fromDbServer = new ObjectInputStream(socket.getInputStream());

            System.out.println("GRPC Server have started on localhost:" + grpcServerPort);
            grpcServer = ServerBuilder
                    .forPort(grpcServerPort)
                    .addService(this)
                    .build()
                    .start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println(("*** shutting down gRPC server since JVM is shutting down"));
                try {
                    stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.out.println("*** server shut down");
            }));


            // принимаем список аутентифицированных юзеров
            authUsers.addAll(((List<User>) fromDbServer.readObject()));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void stop() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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
        User stub = new User(-1, login, password, "", "");
        Sender logUser = new Sender(new DispatcherDbServerMsg(stub, "login"));
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
        User stub = new User(id, "", "", "", "");
        Sender outUser = new Sender(new DispatcherDbServerMsg(stub, "logout"));
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // TODO: вернуть результат address клиенту
    }

    @Override
    public void getStatistic(ClientId request, StreamObserver<greet.Statistic> responseObserver) {
        int id = (int) request.getId();
        User stub = new User(id, "", "", "", "");
        Sender getStat = new Sender(new DispatcherDbServerMsg(stub, "statistic"));
        Future<Object> reponse = poolForWriting.submit(getStat);

        DispatcherDbServerMsg msg = null;
        try {
            msg = (DispatcherDbServerMsg) reponse.get();
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
            responseObserver.onCompleted();;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void check(Empty request, StreamObserver<Empty> responseObserver) {
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
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
