package classes;

import java.io.Serializable;

public class Player implements Serializable {
    int id = -1;
    int lives = -1;
    String nickname = "";
    String hero = "";

    public Player() {
    }

    public Player(int id) {
        this.id = id;
    }

    public Player(int id, int lives, String nickname, String hero) {
        this.id = id;
        this.lives = lives;
        this.nickname = nickname;
        this.hero = hero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHero() {
        return hero;
    }

    public void setHero(String hero) {
        this.hero = hero;
    }
}
