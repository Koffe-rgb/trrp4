public class App {
    static Dispatcher dispatcher;

    public static void main(String[] args) {
        dispatcher = new Dispatcher("localhost", 8000);
        dispatcher.requestHero(55);
    }
}
