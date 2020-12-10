package msg;

import java.io.Serializable;
/**
 * Формат сообщения с клиентом
 */
public class ClientMsg implements Serializable {
    private int type;       // 0-результат арены, 1-конец хода, 2-запрос клиента на подключение, 3-глас клиента
    private int id;
    private int client;
    private String arenaResult;        // фраза о том, кто одержал победу
    private int glas;   // 1-хорошо, 0-плохо
    private String hod;     // фраза хода
    private int lives;      // колво жизней игрока (НЕ колво вычитаемой)

}
