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
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    ExecutorService pool = Executors.newFixedThreadPool(2);

    public Duel(Socket client) {
        random = new Random();
//        player1 = cl1.player;
        this.player1 = new Player(1, 100, "Cool Guy", "Hercules");
        this.player2 = new Player(1, 100, "Darth Veider", "Luke");
        player1Socket = client;
        chronicle = new LinkedList<>();
    }

    @Override
    public void run(){
        handleWithSocket();
        runDuel();
    }
    /**
     * Обрабатывает первое сообщение от пользователя (получает id),
     * возвращает ответ клиенту, добавляет его в очередь
     */
    private void handleWithSocket(){
        int id = -1;
        try {
            player1Socket.setSoTimeout(60*1000);   // ждем id от клиента в течение минуты
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            oos = new ObjectOutputStream(player1Socket.getOutputStream());
            ois = new ObjectInputStream(player1Socket.getInputStream());
            id = ois.readInt();
            // проверяем, что у нас есть данные об этом клиенте
//                Player pl1 = playerInfoMap.get(id);
            Client cl;
//                if (pl1!=null) {      //Todo
            {
//                    cl = new Client(client, pl1);       // добавляем клиента в очередь, если данные найдены
                System.out.println("[x] клиент: "+player1Socket.getLocalAddress()+" был добавлен");
            }

        } catch (IOException  e) {
            e.printStackTrace();
        }

    }


    private void runDuel(){
        System.out.println("[x] Начинаем дуэль");
        // отправляем "приветы"
        System.out.println("[x] Отправляем приветы");
            String hiPhrase = "На поле битвы сошлись "+player1.getNickname()+" и "+player2.getNickname();
            chronicle.add(hiPhrase);
            ClientMsg clientMsg1 = new ClientMsg(hiPhrase, player1.getLives(), player2.getLives(), false);

        try {
//            oos1.writeObject(clientMsg1);
            oos.writeInt(1);
            oos.flush();
            System.out.println("[x] Приветы отправленны");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[x] Не удалось отправить приветствие");
        }

        pool.execute(new Reader());
        pool.execute(new Sender());

        // отправляем результаты в БД
        sendResultToDB();

    }
    private void Close(){
        System.out.println("[x] Закрываем дуэль");
        try {
            if(ois!=null) ois.close();
            if(oos!=null) oos.close();
            player1Socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdownNow();
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
//        String phrase = phrases.getUsualPhrase(curPlayer.getHero(), enemy.getHero());
        String phrase = "Ход номер "+hodNum;
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

    private class Sender implements Runnable{
        @Override
        public void run() {
            try {
                sendHod();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Close();
        }
        private void sendHod() throws InterruptedException {
            long time = System.currentTimeMillis()/1000;
            boolean isDuelRunning = true;
            Player curPlayer;
            Player enemy;
            Socket curSocket;

            ObjectInputStream curois;
            ObjectOutputStream curoos;
            ObjectOutputStream enemyoos;
            // начинаем дуэль
            while (isDuelRunning){
                Thread.sleep(5*1000);
                if(hodNum%2==0) {curPlayer = player1; enemy=player2; curoos=oos; enemyoos=null;}
                else {curPlayer = player2; enemy = player1; enemyoos = oos; curoos=null;}
                String phrase = "";
                isDuelRunning = hodNum<3;
                // отправляем результаты хода
                System.out.println("[x] Заканчиваем дуэль? " + !isDuelRunning);
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
//                            curoos.writeObject(new ClientMsg(msgType, hodNum, phrase, curPlayer.getLives(), enemy.getLives()));
                        if(!isDuelRunning)
                            curoos.writeInt(2);
                        else
                            curoos.writeInt(6);
                        curoos.flush();
                    }
                    else{
//                            enemyoos.writeObject(new ClientMsg(msgType, hodNum, phrase, enemy.getLives(), curPlayer.getLives()));
                        if(!isDuelRunning)
                            enemyoos.writeInt(2);
                        else
                            enemyoos.writeInt(3);

                        enemyoos.flush();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("[x] Клиент был отключен");
                }

                chronicle.add(phrase);
                hodNum++;
            }

        }

    }
    private class Reader implements Runnable{

        @Override
        public void run() {
            receiveMsg();
        }
        private void receiveMsg(){
            while (true){
                System.out.println("[x] Ждем сообщения...");
                try {
                    int n = ois.readInt();
                    System.out.println(n);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            //                if (curSocket!=null) {
//                    try {
//                        // пытаемся получить воздействие
//                        clientMsg1 = (ClientMsg) curois.readObject();
//                        glas = clientMsg1.getGlas();
//
//                    } catch (ClassNotFoundException | IOException e) {
//                        System.out.println("Воздействия не было на " + hodNum + " ходу");
//                        // e.printStackTrace();
//                    } finally {
//                        // получаем результат хода
//                        phrase = getHodResult(glas, curPlayer, enemy);
//                    }
//                }
//                isDuelRunning = isGameFinished();       // проверяем не пора ли заканчивать
        }
    }
}