package model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QHeroes is a Querydsl query type for QHeroes
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QHeroes extends com.querydsl.sql.RelationalPathBase<QHeroes> {

    private static final long serialVersionUID = -487635948;

    public static final QHeroes heroes = new QHeroes("heroes");

    public final NumberPath<Integer> health = createNumber("health", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> idUser = createNumber("idUser", Integer.class);

    public final StringPath name = createString("name");

    public final com.querydsl.sql.PrimaryKey<QHeroes> heroesPk = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QUsers> heroesUsersIdFk = createForeignKey(idUser, "id");

    public QHeroes(String variable) {
        super(QHeroes.class, forVariable(variable), "public", "heroes");
        addMetadata();
    }

    public QHeroes(String variable, String schema, String table) {
        super(QHeroes.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QHeroes(String variable, String schema) {
        super(QHeroes.class, forVariable(variable), schema, "heroes");
        addMetadata();
    }

    public QHeroes(Path<? extends QHeroes> path) {
        super(path.getType(), path.getMetadata(), "public", "heroes");
        addMetadata();
    }

    public QHeroes(PathMetadata metadata) {
        super(QHeroes.class, metadata, "public", "heroes");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(health, ColumnMetadata.named("health").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(idUser, ColumnMetadata.named("id_user").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(name, ColumnMetadata.named("name").withIndex(3).ofType(Types.VARCHAR).withSize(2147483647).notNull());
    }

}

