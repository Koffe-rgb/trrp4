import classes.Player;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import services.ArenaService;

import java.util.concurrent.*;

public class App {
    static ConcurrentMap<Integer, Player> playerInfo = new ConcurrentHashMap<>();
    static ExecutorService pool = Executors.newCachedThreadPool();
    static BlockingQueue<Integer> clientsQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        for(int i=0; i<10; i++){
            clientsQueue.add(i);
        }

        for(int i=0; i<10; i++) {
            try {
                pool.execute(new ArenaService(clientsQueue.take(), "realDispatcher/src/main/resources/arenaServersIps.properties"));
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
