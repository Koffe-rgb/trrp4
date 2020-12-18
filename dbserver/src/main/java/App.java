import arena.ArenaMqServerConsumer;
import arena.ArenaSocketServerListener;
import dispatcher.DispatcherSocketServer;
import repository.Dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        Dao dao = new Dao();

        DispatcherSocketServer server = new DispatcherSocketServer(dao, 8000);
        ArenaSocketServerListener listener = new ArenaSocketServerListener(dao, 8001);
        ArenaMqServerConsumer consumer = new ArenaMqServerConsumer(dao);

        executorService.execute(server);
        executorService.execute(listener);
        executorService.execute(consumer);
    }
}
