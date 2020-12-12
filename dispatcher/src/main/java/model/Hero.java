package model;

import java.io.Serializable;

public class Hero implements Serializable {
    private int id;
    private int idUser;
    private String name;
    private int health;

    public Hero(int id, int idUser, String name, int health) {
        this.id = id;
        this.idUser = idUser;
        this.name = name;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
