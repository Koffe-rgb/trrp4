package model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QUsers is a Querydsl query type for QUsers
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QUsers extends com.querydsl.sql.RelationalPathBase<QUsers> {

    private static final long serialVersionUID = -1527340388;

    public static final QUsers users = new QUsers("users");

    public final StringPath hash = createString("hash");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath login = createString("login");

    public final StringPath nickname = createString("nickname");

    public final StringPath salt = createString("salt");

    public final com.querydsl.sql.PrimaryKey<QUsers> usersPk = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QHeroes> _heroesUsersIdFk = createInvForeignKey(id, "id_user");

    public final com.querydsl.sql.ForeignKey<QStatistic> _statisticUsersIdFk = createInvForeignKey(id, "id_user");

    public QUsers(String variable) {
        super(QUsers.class, forVariable(variable), "public", "users");
        addMetadata();
    }

    public QUsers(String variable, String schema, String table) {
        super(QUsers.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QUsers(String variable, String schema) {
        super(QUsers.class, forVariable(variable), schema, "users");
        addMetadata();
    }

    public QUsers(Path<? extends QUsers> path) {
        super(path.getType(), path.getMetadata(), "public", "users");
        addMetadata();
    }

    public QUsers(PathMetadata metadata) {
        super(QUsers.class, metadata, "public", "users");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(hash, ColumnMetadata.named("hash").withIndex(3).ofType(Types.VARCHAR).withSize(2147483647).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(login, ColumnMetadata.named("login").withIndex(2).ofType(Types.VARCHAR).withSize(2147483647).notNull());
        addMetadata(nickname, ColumnMetadata.named("nickname").withIndex(5).ofType(Types.VARCHAR).withSize(2147483647).notNull());
        addMetadata(salt, ColumnMetadata.named("salt").withIndex(4).ofType(Types.VARCHAR).withSize(2147483647).notNull());
    }

}

