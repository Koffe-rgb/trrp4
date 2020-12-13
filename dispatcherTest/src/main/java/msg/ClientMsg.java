package msg;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Формат сообщения с клиентом
 */
public class ClientMsg implements Serializable {
    private int type;       // 0-результат дуэли, 1-конец хода, 2-запрос клиента на подключение, 3-глас клиента, 4-начало дуэли
    private int hodNum;                 // номер хода (считает сервер)
    private int idClient;
    private String phrase;        // фраза о том, кто одержал победу или фраза о начале дуэли или фраза хода
    private int glas;               // 1-хорошо, 0-плохо
    private int lives;              // колво жизней игрока (НЕ колво вычитаемой)
    private int enemyLives;         // колво жизней противника (НЕ колво вычитаемой)
    private int time;               // таймер хода
    private boolean isEven;       // ход клиента чет или нечет
    private List<String> chronicle = new LinkedList<>();       // хроника событий

    public ClientMsg() {
    }

    /**
     * Приветственное сообщение
     * @param hiPhrase приветственная фраза
     * @param lives колво жизней игрока
     * @param enemyLives колво жизней противника
     */
    public ClientMsg(String hiPhrase, int lives, int enemyLives, boolean isEven) {
        this.type = 4;
        hodNum = 0;
        this.phrase = hiPhrase;
        this.lives = lives;
        this.enemyLives = enemyLives;
        this.isEven = isEven;
        time = 0;
    }

    /**
     * Сообщение конца хода или конца дуэли
     * @param type
     * @param hodNum
     * @param phrase
     * @param lives
     * @param enemyLives
     */
    public ClientMsg(int type, int hodNum, String phrase, int lives, int enemyLives) {
        this.type = type;
        this.hodNum = hodNum;
        this.phrase = phrase;
        this.lives = lives;
        this.enemyLives = enemyLives;
        this.time = 0;
    }


    public void clear(){
        type = -1;
        hodNum = -1;
        idClient = -1;
        phrase = "";
        glas = -1;
        lives = -1;
        enemyLives = -1;
        isEven = false;
        time = -1;
    }



    //region Getter-Setter
    public List<String> getChronicle() {
        return chronicle;
    }

    public void setChronicle(List<String> chronicle) {
        this.chronicle = chronicle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHodNum() {
        return hodNum;
    }

    public void setHodNum(int hodNum) {
        this.hodNum = hodNum;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getGlas() {
        return glas;
    }

    public void setGlas(int glas) {
        this.glas = glas;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getEnemyLives() {
        return enemyLives;
    }

    public void setEnemyLives(int enemyLives) {
        this.enemyLives = enemyLives;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public boolean isEven() {
        return isEven;
    }

    public void setEven(boolean even) {
        isEven = even;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    //endregion
}
