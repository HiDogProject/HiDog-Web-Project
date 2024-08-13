package org.hidog.mypage.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellRecord is a Querydsl query type for SellRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellRecord extends EntityPathBase<SellRecord> {

    private static final long serialVersionUID = -961779334L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellRecord sellRecord = new QSellRecord("sellRecord");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath itemName = createString("itemName");

    public final org.hidog.member.entities.QMember member;

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public QSellRecord(String variable) {
        this(SellRecord.class, forVariable(variable), INITS);
    }

    public QSellRecord(Path<? extends SellRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellRecord(PathMetadata metadata, PathInits inits) {
        this(SellRecord.class, metadata, inits);
    }

    public QSellRecord(Class<? extends SellRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new org.hidog.member.entities.QMember(forProperty("member")) : null;
    }

}

