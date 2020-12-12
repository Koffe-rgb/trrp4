package msg;

import model.Hero;
import model.User;

import java.io.Serializable;

public class DispatcherDbServerMsg implements Serializable {
    private User user;
    private Hero hero;
    private String tag;

    private Object response;

    public DispatcherDbServerMsg(String tag, Object response) {
        this.tag = tag;
        this.response = response;
    }

    public DispatcherDbServerMsg(User user, String tag) {
        this.user = user;
        this.tag = tag;
    }

    public DispatcherDbServerMsg(Hero hero, String tag) {
        this.hero = hero;
        this.tag = tag;
    }

    public DispatcherDbServerMsg(User user, Hero hero, String tag) {
        this.user = user;
        this.hero = hero;
        this.tag = tag;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
