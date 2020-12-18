import classes.Player;
import services.ArenaService;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App {
    public static void main(String[] args) {
        Dispatcher dispatcher = new Dispatcher("localhost", 8000, 8005);
        try {
            dispatcher.blockUntilShutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
