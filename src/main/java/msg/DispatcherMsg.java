package msg;

import classes.Player;

import java.io.Serializable;

/**
 * Формат сообщения с диспетчером
 */
public class DispatcherMsg implements Serializable {
    private Player player;      // инфа про игрока (если null-> возвращаем колво клиентов)
    private String respond;     // ответ сервера арены
    private String request;     // запрос диспетчера

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getRespond() {
        return respond;
    }

    public void setRespond(String respond) {
        this.respond = respond;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
