package msg;

import classes.Player;

import java.io.Serializable;

/**
 * Формат сообщения с диспетчером
 */
public class DispatcherMsg implements Serializable {
    private Player player;      // инфа про игрока (если null-> возвращаем колво клиентов)
    private int respond;     // ответ сервера арены (колво игроков или -1)
    private String request;     // запрос диспетчера

    public DispatcherMsg() {
    }

    public DispatcherMsg(int respond) {
        this.respond = respond;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRespond() {
        return respond;
    }

    public void setRespond(int respond) {
        this.respond = respond;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
