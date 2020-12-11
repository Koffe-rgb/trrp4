import classes.Player;
import javafx.util.Pair;
import services.ArenaService;

import java.util.concurrent.*;

public class App {
    static ConcurrentMap<Integer, Player> playerInfo = new ConcurrentHashMap<>();
    static ExecutorService pool = Executors.newCachedThreadPool();
    static BlockingQueue<Integer> clientsQueue = new LinkedBlockingQueue<>();
    static CopyOnWriteArrayList<Pair<String, Integer>> arenaServerIPs = new CopyOnWriteArrayList();     // адрес и порт

    public static void main(String[] args) {
        for(int i=0; i<10; i++){
            clientsQueue.add(i);
        }
        arenaServerIPs.add(new Pair<>("localhost", 8002));

        for(int i=0; i<10; i++) {
            try {
                pool.execute(new ArenaService(clientsQueue.take(), arenaServerIPs));
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
}
