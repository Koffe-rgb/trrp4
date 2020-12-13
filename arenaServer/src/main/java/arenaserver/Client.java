package arenaserver;

import classes.Player;

import java.net.Socket;
import java.util.concurrent.Future;

public class Client {
    Socket socket;
    Player player;


    public Client(Socket socket, Player player) {
        this.socket = socket;
        this.player = player;
    }

    public Client(Player player) {
        this.player = player;
    }

}
