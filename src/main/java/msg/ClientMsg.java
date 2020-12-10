package msg;

import java.io.Serializable;
/**
 * Формат сообщения с клиентом
 */
public class ClientMsg implements Serializable {
    private int type;       // 0-результат дуэли, 1-конец хода, 2-запрос клиента на подключение, 3-глас клиента, 4-начало дуэли
    private int hodNum;     // номер хода (считает сервер)
    private int idClient;
    private String arenaResult;        // фраза о том, кто одержал победу
    private String hiPhrase;        // фраза о начале дуэли
    private int glas;   // 1-хорошо, 0-плохо
    private String hod;     // фраза хода
    private int lives;      // колво жизней игрока (НЕ колво вычитаемой)
    private int enemyLives;      // колво жизней противника (НЕ колво вычитаемой)
    private int time;           // таймер хода
    private boolean isEven;       // ход клиента чет или нечет

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
        this.hiPhrase = hiPhrase;
        this.lives = lives;
        this.enemyLives = enemyLives;
        this.isEven = isEven;
        time = 0;
    }

    public void clear(){
        type = -1;
        hodNum = -1;
        idClient = -1;
        arenaResult = "";
        glas = -1;
        hod = "";
        lives = -1;
        enemyLives = -1;
        hiPhrase = "";
        isEven = false;
        time = -1;
    }
}
