package model;

public class Statistic {
    private final int id;
    private final int idUser;
    private final int wins;
    private final int loses;

    public Statistic(int id, int idUser, int wins, int loses) {
        this.id = id;
        this.idUser = idUser;
        this.wins = wins;
        this.loses = loses;
    }

    public int getId() {
        return id;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }
}
