package arenaserver;

import classes.Phrases;
import classes.Player;
import javafx.util.Pair;
import msg.ClientMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class Duel implements Callable {
    private Socket player1Socket = null;
    private Socket player2Socket = null;
    private Player player1 = null;
    private Player player2 = null;
    private int hodNum;
    private Random random;
    private List<String> chronicle;
    private Phrases phrases;
    private int winnerId = -1;
    private int loserId = -1;

    public Duel(Socket player1Socket, Socket player2Socket, Player player1, Player player2, Phrases phrases) {
        this.player1Socket = player1Socket;
        this.player2Socket = player2Socket;
        this.player1 = player1;
        this.player2 = player2;
        random = new Random();
        chronicle = new LinkedList<>();
        this.phrases = phrases;
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
            chronicle.add(hiPhrase);
            ClientMsg clientMsg1 = new ClientMsg(hiPhrase, player1.getLives(), player2.getLives(), false);
            ClientMsg clientMsg2 = new ClientMsg(hiPhrase, player2.getLives(), player1.getLives(), true);

            oos1.writeObject(clientMsg1);
            oos2.writeObject(clientMsg2);
            oos1.flush();
            oos2.flush();

            boolean isDuelRunning = true;
            Player curPlayer;
            Player enemy;
            Socket curSocket;

            ObjectInputStream curois;
            ObjectOutputStream curoos;
            ObjectOutputStream enemyoos;

            // начинаем дуэль
            while (isDuelRunning){
                if(hodNum%2==0) {curPlayer = player1; enemy=player2; curSocket = player1Socket; curois = ois1; curoos=oos1;enemyoos = oos2;}
                else {curPlayer = player2; curSocket = player2Socket; curois = ois2; enemy = player1; curoos=oos2; enemyoos = oos1;}

                curSocket.setSoTimeout(30*1000);    //ждем ответа от клиента (ms)
                clientMsg1.clear();
                String phrase = "";
                int glas = -1;
                try {
                    // пытаемся получить воздействие
                    clientMsg1 = (ClientMsg) curois.readObject();
                    glas = clientMsg1.getGlas();

                } catch (ClassNotFoundException e) {
                    System.out.println("Воздействия не было на " + hodNum + " ходу");
                    // e.printStackTrace();
                }
                finally {
                    // получаем результат хода
                    phrase = getHodResult(glas, curPlayer, enemy);
                }
                isDuelRunning = isGameFinished();       // проверяем не пора ли заканчивать
                // отправляем результаты хода
                int msgType = 1;
                if (isDuelRunning){
                    msgType = 1;
                }
                // заканчиваем дуэль
                else {
                    msgType = 0;
                    phrase+=". \n "+curPlayer.getHero()+" одержал оглушительную победу над "+enemy.getHero();
                    winnerId = curPlayer.getId();
                    loserId = enemy.getId();
                }
                curoos.writeObject(new ClientMsg(msgType, hodNum, phrase, curPlayer.getLives(), enemy.getLives()));
                enemyoos.writeObject(new ClientMsg(msgType, hodNum, phrase, enemy.getLives(), curPlayer.getLives()));
                curoos.flush();
                enemyoos.flush();

            }

            System.out.println("[x] Закрываем дуэль");
            ois1.close();
            ois2.close();
            oos1.close();
            oos2.close();
            player1Socket.close();
            player2Socket.close();
        } catch (IOException e) {
            System.out.println("[x] Проблемы со стримами");
            e.printStackTrace();
        }

        return new Pair<>(winnerId, loserId);
    }

    // игра заканчивается, когда у противника на очередном ходе заканчивается здоровье
    private boolean isGameFinished(){
        // если ход четный, проверяем здоровье второго (нечетного) игрока
        if (hodNum%2==0)
            return player2.getLives()<0;
        else return player1.getLives()<0;
    }

    /**
     * Создание результата хода
     * @param glas
     * @param curPlayer
     * @param enemy
     * @return фраза хода
     */
    private String getHodResult(int glas, Player curPlayer, Player enemy){
        int damage = random.nextInt(40)+20;         //дамаг от 20 до 60
        int plus = random.nextInt(40)+20;     // лечение или доп урон
        String phrase = phrases.getUsualPhrase(curPlayer.getHero(), enemy.getHero());
        enemy.setLives(enemy.getLives()-damage);
        if (glas==0){   //"плохо"
            enemy.setLives(enemy.getLives()-plus);
            phrase = phrases.getBadPhrase(curPlayer.getHero(), enemy.getHero());
        }
        else if (glas==1){   //"хорошо"
            curPlayer.setLives(curPlayer.getLives()+plus);
            phrase = phrases.getGoodPhrase(curPlayer.getHero(), enemy.getHero());
        }

        chronicle.add(phrase);
        return phrase;
    }

    public synchronized void reconnect(Socket player){
        //if (!player1.isConnected())

    }
}
