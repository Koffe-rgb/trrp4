package arenaserver;

import classes.Phrases;
import classes.Player;
import javafx.util.Pair;
import msg.ClientMsg;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Duel implements Runnable {
    private Socket player1Socket = null;
    private Player player1 = null;
    private Player player2 = null;
    private int hodNum;
    private Random random;
    private List<String> chronicle;
    private Phrases phrases;
    private int winnerId = -1;
    private int loserId = -1;
    private AtomicInteger duelsCount;

    public Duel(Client client, AtomicInteger duelsCount) {
        random = new Random();
        this.duelsCount = duelsCount;
//        player1 = cl1.player;
        this.player1 = new Player(1, 100, "Cool Guy", "Hercules");
        this.player2 = new Player(1, 100, "Darth Veider", "Luke");
        player1Socket = client.socket;
    }

    @Override
    public void run(){
        runDuel();
    }
    private void runDuel(){
        System.out.println("[x] Начинаем дуэль");
        ObjectInputStream ois1 = null;
        ObjectOutputStream oos1= null;
        try {
            ois1 = new ObjectInputStream(player1Socket.getInputStream());
            oos1 = new ObjectOutputStream(player1Socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[x] Не удалось получить стримы");
        }

        // отправляем "приветы"
        System.out.println("[x] Отправляем приветы");
            String hiPhrase = "На поле битвы сошлись "+player1.getNickname()+" и "+player2.getNickname();
            chronicle.add(hiPhrase);
            ClientMsg clientMsg1 = new ClientMsg(hiPhrase, player1.getLives(), player2.getLives(), false);

        try {
            oos1.writeObject(clientMsg1);
            oos1.flush();
            System.out.println("[x] Приветы отправленны");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[x] Не удалось отправить приветствие");
        }


            boolean isDuelRunning = true;
            Player curPlayer;
            Player enemy;
            Socket curSocket;

            ObjectInputStream curois;
            ObjectOutputStream curoos;
            ObjectOutputStream enemyoos;

            // начинаем дуэль
            while (isDuelRunning){
                curSocket=null;
                if(hodNum%2==0) {curPlayer = player1; enemy=player2; curSocket = player1Socket; curois = ois1; curoos=oos1; enemyoos=null;}
                else {curPlayer = player2; enemy = player1; enemyoos = oos1; curois=null; curoos=null;}

                if (curSocket!=null){
                    try {
                        curSocket.setSoTimeout(30*1000);    //ждем ответа от клиента (ms)
                    } catch (SocketException e) {
                        e.printStackTrace();
                        System.out.println("[x] Ошибка установки тайм-аута");
                    }
                }

                clientMsg1.clear();
                String phrase = "";
                int glas = -1;
                if (curSocket!=null) {
                    try {
                        // пытаемся получить воздействие
                        clientMsg1 = (ClientMsg) curois.readObject();
                        glas = clientMsg1.getGlas();

                    } catch (ClassNotFoundException | IOException e) {
                        System.out.println("Воздействия не было на " + hodNum + " ходу");
                        // e.printStackTrace();
                    } finally {
                        // получаем результат хода
                        phrase = getHodResult(glas, curPlayer, enemy);
                    }
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

                    try {
                        if (curoos!=null) {
                            curoos.writeObject(new ClientMsg(msgType, hodNum, phrase, curPlayer.getLives(), enemy.getLives()));
                            curoos.flush();
                        }
                        else{
                            enemyoos.writeObject(new ClientMsg(msgType, hodNum, phrase, enemy.getLives(), curPlayer.getLives()));
                            enemyoos.flush();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("[x] Клиент был отключен");
                }

                chronicle.add(phrase);
                hodNum++;
            }

            System.out.println("[x] Закрываем дуэль");
        try {
            if(ois1!=null) ois1.close();
            if(oos1!=null) oos1.close();
            player1Socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
            // отправляем результаты в БД
        sendResultToDB();
        duelsCount.decrementAndGet();

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

        return phrase;
    }

    /**
     * Отправляет результаты в бд
     */
    private void sendResultToDB(){
        //send(new int[2]{winnerId, loserId});
    }
    public synchronized void reconnect(Socket player){
        //if (!player1.isConnected())

    }
}
