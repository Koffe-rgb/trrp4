package repository;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.PostgreSQLTemplates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import model.*;


import java.util.List;

public class Dao {
    private final SQLQueryFactory queryFactory;

    public Dao() {
        HikariConfig config = new HikariConfig("./src/main/resources/datasource.properties" );
        HikariDataSource dataSource = new HikariDataSource(config);

        SQLTemplates dialect = new PostgreSQLTemplates();
        Configuration configuration = new Configuration(dialect);

        this.queryFactory = new SQLQueryFactory(configuration, dataSource);
    }

    public User selectUser(int id) {
        QUsers qUsers = QUsers.users;
        return queryFactory.select(Projections.constructor(User.class,
                qUsers.id, qUsers.login, qUsers.hash, qUsers.salt, qUsers.nickname))
                .from(qUsers)
                .where(qUsers.id.eq(id))
                .fetchFirst();
    }

    public User selectUser(String login, String hash) {
        QUsers qUsers = QUsers.users;
        return queryFactory.select(Projections.constructor(User.class,
                qUsers.id, qUsers.login, qUsers.hash, qUsers.salt, qUsers.nickname))
                .from(qUsers)
                .where(qUsers.login.eq(login).and(qUsers.hash.eq(hash)))
                .fetchFirst();
    }

    public String selectSalt(String login) {
        QUsers qUsers = QUsers.users;
        return queryFactory.select(qUsers.salt)
                .from(qUsers)
                .where(qUsers.login.eq(login))
                .fetchFirst();
    }

    public boolean isLoginUnique(String login) {
        QUsers qUsers = QUsers.users;
        User user = queryFactory.select(Projections.constructor(User.class,
                qUsers.id, qUsers.login, qUsers.hash, qUsers.salt, qUsers.nickname))
                .from(qUsers)
                .where(qUsers.login.eq(login))
                .fetchFirst();
        return user == null;
    }

    public void insertUser(User user) {
        QUsers qUsers = QUsers.users;
        Integer key = queryFactory.insert(qUsers)
                .columns(qUsers.login, qUsers.hash, qUsers.salt, qUsers.nickname)
                .values(user.getLogin(), user.getHash(), user.getSalt(), user.getNickname())
                .executeWithKey(Integer.class);
        user.setId(key);
    }

    public void updateUser(User user) {
        QUsers qUsers = QUsers.users;
        queryFactory.update(qUsers)
                .set(qUsers.login, user.getLogin())
                .set(qUsers.hash, user.getHash())
                .set(qUsers.salt, user.getSalt())
                .set(qUsers.nickname, user.getNickname())
                .where(qUsers.id.eq(user.getId()))
                .execute();
    }

    public void deleteUser(int id) {
        QUsers qUsers = QUsers.users;
        queryFactory.delete(qUsers)
                .where(qUsers.id.eq(id))
                .execute();
    }

    public void deleteUser(User user) {
        deleteUser(user.getId());
    }

    public Hero selectHero(int userId) {
        QHeroes qHeroes = QHeroes.heroes;
        return queryFactory.select(Projections.constructor(Hero.class,
                qHeroes.id, qHeroes.idUser, qHeroes.name, qHeroes.health))
                .from(qHeroes)
                .where(qHeroes.idUser.eq(userId))
                .fetchFirst();
    }

    public void insertHero(Hero hero) {
        QHeroes qHeroes = QHeroes.heroes;
        Integer key = queryFactory.insert(qHeroes)
                .columns(qHeroes.idUser, qHeroes.name, qHeroes.health)
                .values(hero.getIdUser(), hero.getName(), hero.getHealth())
                .executeWithKey(Integer.class);
        hero.setId(key);
    }

    public void updateHero(Hero hero) {
        QHeroes qHeroes = QHeroes.heroes;
        queryFactory.update(qHeroes)
                .set(qHeroes.health, hero.getHealth())
                .set(qHeroes.name, hero.getName())
                .where(qHeroes.id.eq(hero.getId()))
                .execute();
    }

    public void deleteHero(int id) {
        QHeroes qHeroes = QHeroes.heroes;
        queryFactory.delete(qHeroes)
                .where(qHeroes.id.eq(id))
                .execute();
    }

    public void deleteHero(Hero hero) {
        deleteHero(hero.getId());
    }

    public Statistic selectStatistic(int idUser) {
        QStatistic qStatistic = QStatistic.statistic;
        return queryFactory.select(Projections.constructor(Statistic.class,
                qStatistic.id, qStatistic.idUser, qStatistic.wins, qStatistic.loses))
                .from(qStatistic)
                .where(qStatistic.idUser.eq(idUser))
                .fetchFirst();
    }

    public void upsertStatistic(int idUser, int nWins, int nLoses) {
        System.out.println("upsertStatistic begin");
        QStatistic qStatistic = QStatistic.statistic;

        Statistic existed = selectStatistic(idUser);
        if (existed == null) {
            queryFactory.insert(qStatistic)
                    .columns(qStatistic.idUser, qStatistic.wins, qStatistic.loses)
                    .values(idUser, nWins, nLoses)
                    .execute();
        } else {
            queryFactory.update(qStatistic)
                    .set(qStatistic.wins, qStatistic.wins.add(nWins))
                    .set(qStatistic.loses, qStatistic.loses.add(nLoses))
                    .where(qStatistic.idUser.eq(idUser))
                    .execute();
        }
        System.out.println("upsertStatistic end");
    }

    public void upsertStatistic(int idWinner, int idLoser) {
        if (idWinner != -1) upsertStatistic(idWinner, 1, 0);
        if (idLoser != -1) upsertStatistic(idLoser, 0, 1);
    }

    private List<String> selectPhrases(int type) {
        QPhrases qPhrases = QPhrases.phrases;
        return queryFactory.select(qPhrases.phrase)
                .from(qPhrases)
                .where(qPhrases.type.eq(type))
                .fetch();
    }

    public List<String> selectPhrases() {
        QPhrases qPhrases = QPhrases.phrases;
        return queryFactory.select(qPhrases.phrase)
                .from(qPhrases)
                .fetch();
    }

    public List<String> selectNeutralPhrases() {
        return selectPhrases(0);
    }

    public List<String> selectPositivePhrases() {
        return selectPhrases(1);
    }

    public List<String> selectNegativePhrases() {
        return selectPhrases(-1);
    }
}
