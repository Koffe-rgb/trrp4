package arenaserver;

import classes.Phrases;
import classes.Player;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sun.xml.internal.ws.resources.ClientMessages;
import javafx.util.Pair;
import msg.ClientMsg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Duel implements Runnable {
    private Socket player1Socket = null;
    private Player player1 = null;
    private Player player2 = null;
    private int hodNum;
    private String[] dailyMonsters;
    private Random random;
    private List<String> chronicle;
    private Phrases phrases;
    private int winnerId = -1;
    private int loserId = -1;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private ExecutorService pool = Executors.newFixedThreadPool(2);
    private Map<Integer, Duel> clientIdsInDuel;
    private AtomicInteger glas = new AtomicInteger(-1);
    private final int BAD_GLAS = 0;
    private final int GOOD_GLAS = 1;


    public Duel(Socket client, Map<Integer, Duel> clientIdsInDuel, ObjectInputStream ois, ObjectOutputStream oos, Player player) {
        dailyMonsters = new String[]{"Aвитаминосец", "Aвтомогиль", "Aдепт Пивного Культа", "Aдминистратор Годвилля", "Aдский Вертихвост", "Aдский Вратарь", "Aктивированный Угорь", "Aленький Цветочник", "Aлименталист", "Aлкоголем", "Aлхимический Металлист", "Aль Монах", "Aльтер Эго", "Aльфа-кентавр", "Aнархиоптерикс", "Aнатомический Нонсенс", "Aнгел-Бранитель", "Aнгел-Хоронитель", "Aндед-Мороз", "Aнизотропный Голем", "Aнонимный Aнонимус", "Aнонимный Доброжелатель", "Aнтагонист", "Aнтигерой", "Aнтракторист", "Aнтропоморфный Дендромутант", "Aппручник", "Aргх-Aнгел", "Aривидервиш", "Aристокрот", "Aрхибаг", "Aрхивирус", "Aрхимедик", "Aсексуальный Маньяк", "Aссассинизатор", "Aстралопитек", "Aтомный Редактор", "Баал-Бес", "Байкер Из Склепа", "Банзаец", "Бардобрей", "Бармаглот", "Барон Суббота", "Барсук Кхорна", "Бахиллес", "Баш-Орк", "Безбашенный Всадник", "Безбашенный Кран", "Бездомный Домовой"};
        random = new Random();
        this.player1 = new Player(1, 300, "Cool Guy", "Hercules"); //player;
        this.player2 = new Player(-666, player1.getLives() + (random.nextInt(50) - 30), "Megamind", dailyMonsters[random.nextInt(dailyMonsters.length)]);
        player1Socket = client;
        chronicle = new LinkedList<>();
        this.clientIdsInDuel = clientIdsInDuel;
        this.oos = oos;
        this.ois = ois;
    }

    @Override
    public void run() {
        runDuel();

    }

    private void runDuel() {
        System.out.println("[x] Начинаем дуэль");
        // отправляем "приветы"
        System.out.println("[x] Отправляем приветы");
        String hiPhrase = "На поле битвы сошлись " + player1.getNickname() + " и " + player2.getNickname();
        chronicle.add(hiPhrase);
        ClientMsg clientMsg1 = new ClientMsg(hiPhrase, player1.getLives(), player2.getLives(), false);

        try {
            oos.writeObject(clientMsg1);
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

    private void Close() {
        System.out.println("[x] Закрываем дуэль");
        try {
            if (!player1Socket.isClosed()) {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
                player1Socket.close();
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        pool.shutdownNow();

        // удаляем ид клиента
        clientIdsInDuel.remove(player1.getId());
    }

    // игра заканчивается, когда у противника на очередном ходе заканчивается здоровье
    private boolean isGameFinished() {
        // если ход четный, проверяем здоровье второго (нечетного) игрока
        if (hodNum % 2 == 0)
            return player1.getLives() <= 0;
        else return player2.getLives() <= 0;
    }

    /**
     * Создание результата хода
     *
     * @param curPlayer
     * @param enemy
     * @return фраза хода
     */
    private String getHodResult(Player curPlayer, Player enemy) {
        int damage = random.nextInt(40) + 20;         //дамаг от 20 до 60
        int plus = random.nextInt(40) + 20;     // лечение или доп урон
//        String phrase = phrases.getUsualPhrase(curPlayer.getHero(), enemy.getHero());
        String phrase = "Ход номер " + hodNum;
        enemy.setLives(enemy.getLives() - damage);
        if (glas.get() == 0) {   //"плохо"
            enemy.setLives(enemy.getLives() - plus);
//            phrase = phrases.getBadPhrase(curPlayer.getHero(), enemy.getHero());
        } else if (glas.get() == 1) {   //"хорошо"
            curPlayer.setLives(curPlayer.getLives() + plus);
//            phrase = phrases.getGoodPhrase(curPlayer.getHero(), enemy.getHero());
        }
        if (enemy.getLives() < 0) enemy.setLives(0);
        glas.set(-1);
        return phrase;
    }


    private class Sender implements Runnable {
        @Override
        public void run() {

            sendHod();
            Close();
        }

        private void sendHod() {
            boolean isDuelRunning = true;
            Player curPlayer;
            Player enemy;

            // начинаем дуэль
            while (isDuelRunning) {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (hodNum % 2 == 0) {
                    curPlayer = player1;
                    enemy = player2;
                } else {
                    curPlayer = player2;
                    enemy = player1;
                }
                String phrase = getHodResult(curPlayer, enemy);
                System.out.println("[!] Результаты хода " + hodNum + ": " + player1.getLives() + " " + player2.getLives());
                isDuelRunning = !isGameFinished();
                // отправляем результаты хода
                System.out.println("[x] Заканчиваем дуэль? " + !isDuelRunning);
                int msgType = 1;
                if (isDuelRunning) {
                    msgType = 1;
                }
                // заканчиваем дуэль
                else {
                    msgType = 0;
                    phrase += " \n " + curPlayer.getHero() + " одержал оглушительную победу над " + enemy.getHero();
                    System.out.println(phrase);
                    winnerId = curPlayer.getId();
                    loserId = enemy.getId();
                }
                ClientMsg clientMsg = new ClientMsg(msgType, hodNum, phrase, hodNum % 2 != 0 ? curPlayer.getLives() : enemy.getLives(), hodNum % 2 != 0 ? enemy.getLives() : curPlayer.getLives());

                try {
                    oos.writeObject(clientMsg);
                    oos.flush();
                } catch (IOException e) {
                    System.out.println("[x] Клиент был отключен");
//                    e.printStackTrace();
                }

                chronicle.add(phrase);
                hodNum++;
            }
        }
    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            receiveMsg();
        }

        private void receiveMsg() {
            while (!player1Socket.isClosed()) {
                System.out.println("[x] Ждем сообщения...");
                try {
                    int n = ois.readInt();
                    // хорошо
                    if (n == 1) {
                        glas.set(GOOD_GLAS);
                    }
                    // плохо
                    else {
                        glas.set(BAD_GLAS);
                    }

                    System.out.println(n);
                } catch (IOException e) {
                    System.out.println("[x] Сокет был закрыт. Прерываем поток чтения");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Метод, вызываемый внешним потоком для реконнекта клиента
     *
     * @param socket
     */
    public synchronized void reconnect(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        System.out.println("[x] Reconnection");
        player1Socket = socket;
        this.oos = oos;
        this.ois = ois;
        System.out.println("[x] Reconnection complete");
    }

    /**
     * Отправляет результаты в бд
     */
    private void sendResultToDB() {
        ConnectionFactory factory = null;
        final String QUEUE_NAME = "queue_arena_results";
        final int CONNECTION_TIMEOUT = 60000; // seconds
        factory = new ConnectionFactory();
        factory.setHost("192.168.0.9");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
                try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                    o.writeObject(new int[]{winnerId, loserId});
                }
                channel.basicPublish("", QUEUE_NAME, null, b.toByteArray());
                System.out.println(" [x] Sent to queue");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
