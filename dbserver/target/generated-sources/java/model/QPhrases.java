package model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QPhrases is a Querydsl query type for QPhrases
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QPhrases extends com.querydsl.sql.RelationalPathBase<QPhrases> {

    private static final long serialVersionUID = 658733166;

    public static final QPhrases phrases = new QPhrases("phrases");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath phrase = createString("phrase");

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public final com.querydsl.sql.PrimaryKey<QPhrases> phrasesPk = createPrimaryKey(id);

    public QPhrases(String variable) {
        super(QPhrases.class, forVariable(variable), "public", "phrases");
        addMetadata();
    }

    public QPhrases(String variable, String schema, String table) {
        super(QPhrases.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QPhrases(String variable, String schema) {
        super(QPhrases.class, forVariable(variable), schema, "phrases");
        addMetadata();
    }

    public QPhrases(Path<? extends QPhrases> path) {
        super(path.getType(), path.getMetadata(), "public", "phrases");
        addMetadata();
    }

    public QPhrases(PathMetadata metadata) {
        super(QPhrases.class, metadata, "public", "phrases");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
        addMetadata(phrase, ColumnMetadata.named("phrase").withIndex(2).ofType(Types.VARCHAR).withSize(2147483647).notNull());
        addMetadata(type, ColumnMetadata.named("type").withIndex(3).ofType(Types.INTEGER).withSize(10).notNull());
    }

}

