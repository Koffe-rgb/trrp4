package model;

public class User {
    private int id;
    private String login;
    private String hash;
    private String salt;
    private String nickname;

    public User(int id, String login, String hash, String salt, String nickname) {
        this.id = id;
        this.login = login;
        this.hash = hash;
        this.salt = salt;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
