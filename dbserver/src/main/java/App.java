import dispatcher.DispatcherSocketServer;
import repository.Dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        DispatcherSocketServer server = new DispatcherSocketServer(new Dao(), 8000);
        executorService.execute(server);
    }
}
