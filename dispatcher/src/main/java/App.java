import classes.Player;
import services.ArenaService;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    static ConcurrentMap<Integer, Player> playerInfo = new ConcurrentHashMap<>();    // хранит инфу про аутент клиентов
    static ExecutorService pool = Executors.newCachedThreadPool();
    static BlockingQueue<Integer> clientsArenaQueue = new LinkedBlockingQueue<>();       // хранит id пользователей на арену

    public static void main(String[] args) {
        AtomicBoolean continueListen = new AtomicBoolean(true);
        // TODO: добавлять в очередь id при запросе клиента
        for(int i=0; i<1; i++){
            clientsArenaQueue.add(i);
        }


        while (continueListen.get()) {
            try {
                pool.execute(new ArenaService(clientsArenaQueue.take(), "dispatcher/src/main/resources/arenaServersIps.properties"));
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        pool.shutdown();

    }
}
