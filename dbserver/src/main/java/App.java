import arena.ArenaSocketServerListener;
import repository.Dao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) {
        final ExecutorService threadPool = Executors.newCachedThreadPool();
        threadPool.execute(new ArenaSocketServerListener(new Dao(), 8000));
    }
}
