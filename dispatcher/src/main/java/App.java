import model.Hero;
import model.User;

public class App {
    static Dispatcher dispatcher;

    public static void main(String[] args) throws InterruptedException {
        dispatcher = new Dispatcher("localhost", 8000);

        User user = new User(10, "log", "23" ,"23" ,"lag");
        Thread.sleep(10*1000);
        dispatcher.logout(user);
    }
}
