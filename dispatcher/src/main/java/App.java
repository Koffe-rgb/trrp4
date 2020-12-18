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
