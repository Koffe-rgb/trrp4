package arenaserver;

import classes.Player;
import javafx.util.Pair;
import msg.ClientMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Duel implements Callable {
    private Socket player1Socket = null;
    private Socket player2Socket = null;
    private Player player1 = null;
    private Player player2 = null;
    private int hodNum;


    public Duel(Socket player1Socket, Socket player2Socket, Player player1, Player player2) {
        this.player1Socket = player1Socket;
        this.player2Socket = player2Socket;
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public Object call() throws Exception {
        return null;
    }
    private Pair<Integer, Integer> runDuel(){
        try {
            ObjectInputStream ois1 = new ObjectInputStream(player1Socket.getInputStream());
            ObjectInputStream ois2 = new ObjectInputStream(player2Socket.getInputStream());
            ObjectOutputStream oos1 = new ObjectOutputStream(player1Socket.getOutputStream());
            ObjectOutputStream oos2 = new ObjectOutputStream(player2Socket.getOutputStream());

            // отправляем "приветы"
            String hiPhrase = "На поле битвы сошлись "+player1.getNickname()+" и "+player2.getNickname();
            ClientMsg clientMsg1 = new ClientMsg(hiPhrase, player1.getLives(), player2.getLives(), false);
            ClientMsg clientMsg2 = new ClientMsg(hiPhrase, player2.getLives(), player1.getLives(), true);
            oos1.writeObject(clientMsg1);
            oos2.writeObject(clientMsg2);
            oos1.flush();
            oos2.flush();

            boolean isDuelRunning = true;
            while (isDuelRunning){


            }




        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // игра заканчивается, когда у противника на очередном ходе заканчивается здоровье
    private boolean isGameFinished(){

    }

    public synchronized void reconnect(Socket player){
        //if (!player1.isConnected())

    }
}
