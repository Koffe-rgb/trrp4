import arenaserver.Server;
import classes.Phrases;
import socket.SocketDBService;

public class App {

    public static void main(String[] args) {
//        Phrases phrases = null;
//        // загружаем фразы для дуэли
//        SocketDBService socketDBService = new SocketDBService();
//        // если не удалось загрузить конфиг, выходим
//        if(!socketDBService.loadConfig("src/main/resources/dbServersIps.properties")){
//            return;
//        }
//
//        phrases = socketDBService.run();


        Server arenaServer = new Server();
        arenaServer.run();
    }
}
