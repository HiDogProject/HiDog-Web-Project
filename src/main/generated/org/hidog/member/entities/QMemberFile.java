package org.hidog.member.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMemberFile is a Querydsl query type for MemberFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberFile extends EntityPathBase<MemberFile> {

    private static final long serialVersionUID = 504138060L;

    public static final QMemberFile memberFile = new QMemberFile("memberFile");

    public final org.hidog.global.entities.QBaseEntity _super = new org.hidog.global.entities.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath email = createString("email");

    public final StringPath gid = createString("gid");

    public final StringPath mobile = createString("mobile");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath password = createString("password");

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    public final StringPath userName = createString("userName");

    public QMemberFile(String variable) {
        super(MemberFile.class, forVariable(variable));
    }

    public QMemberFile(Path<? extends MemberFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberFile(PathMetadata metadata) {
        super(MemberFile.class, metadata);
    }

}

