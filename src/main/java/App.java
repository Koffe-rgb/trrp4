import socket.SocketDBService;

public class App {

    public static void main(String[] args) {
        // загружаем фразы для дуэли
        SocketDBService socketDBService = new SocketDBService();
        // если не удалось загрузить конфиг, выходим
        if(!socketDBService.loadConfig("src/main/resources/dbServersIps.properties")){
            return;
        }

        socketDBService.run();

    }
}
