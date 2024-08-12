package org.hidog.board.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSaveBoardData is a Querydsl query type for SaveBoardData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSaveBoardData extends EntityPathBase<SaveBoardData> {

    private static final long serialVersionUID = 1003017963L;

    public static final QSaveBoardData saveBoardData = new QSaveBoardData("saveBoardData");

    public final org.hidog.global.entities.QBaseEntity _super = new org.hidog.global.entities.QBaseEntity(this);

    public final NumberPath<Long> bSeq = createNumber("bSeq", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final NumberPath<Long> mSeq = createNumber("mSeq", Long.class);

    public QSaveBoardData(String variable) {
        super(SaveBoardData.class, forVariable(variable));
    }

    public QSaveBoardData(Path<? extends SaveBoardData> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSaveBoardData(PathMetadata metadata) {
        super(SaveBoardData.class, metadata);
    }

}

