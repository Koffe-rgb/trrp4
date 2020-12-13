package model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QStatistic is a Querydsl query type for QStatistic
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QStatistic extends com.querydsl.sql.RelationalPathBase<QStatistic> {

    private static final long serialVersionUID = -1673649180;

    public static final QStatistic statistic = new QStatistic("statistic");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> idUser = createNumber("idUser", Integer.class);

    public final NumberPath<Integer> loses = createNumber("loses", Integer.class);

    public final NumberPath<Integer> wins = createNumber("wins", Integer.class);

    public final com.querydsl.sql.PrimaryKey<QStatistic> statisticPk = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<QUsers> statisticUsersIdFk = createForeignKey(idUser, "id");

    public QStatistic(String variable) {
        super(QStatistic.class, forVariable(variable), "public", "statistic");
        addMetadata();
    }

    public QStatistic(String variable, String schema, String table) {
        super(QStatistic.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QStatistic(String variable, String schema) {
        super(QStatistic.class, forVariable(variable), schema, "statistic");
        addMetadata();
    }

    public QStatistic(Path<? extends QStatistic> path) {
        super(path.getType(), path.getMetadata(), "public", "statistic");
        addMetadata();
    }

    public QStatistic(PathMetadata metadata) {
        super(QStatistic.class, metadata, "public", "statistic");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(idUser, ColumnMetadata.named("id_user").withIndex(2).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(loses, ColumnMetadata.named("loses").withIndex(4).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(wins, ColumnMetadata.named("wins").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

