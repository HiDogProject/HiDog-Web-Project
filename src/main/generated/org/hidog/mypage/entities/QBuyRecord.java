package org.hidog.mypage.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBuyRecord is a Querydsl query type for BuyRecord
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBuyRecord extends EntityPathBase<BuyRecord> {

    private static final long serialVersionUID = 1780256864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBuyRecord buyRecord = new QBuyRecord("buyRecord");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final org.hidog.member.entities.QMember member;

    public QBuyRecord(String variable) {
        this(BuyRecord.class, forVariable(variable), INITS);
    }

    public QBuyRecord(Path<? extends BuyRecord> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBuyRecord(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBuyRecord(PathMetadata metadata, PathInits inits) {
        this(BuyRecord.class, metadata, inits);
    }

    public QBuyRecord(Class<? extends BuyRecord> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new org.hidog.member.entities.QMember(forProperty("member")) : null;
    }

}

