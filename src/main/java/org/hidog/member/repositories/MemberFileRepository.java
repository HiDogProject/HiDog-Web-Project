package org.hidog.member.repositories;

import org.hidog.member.entities.MemberFile;
import org.hidog.member.entities.QMemberFile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberFileRepository extends JpaRepository<MemberFile, Long>, QuerydslPredicateExecutor<MemberFile> {
    @EntityGraph(attributePaths = "authorities")//처음부터 조인(같이 로딩)
    Optional<MemberFile> findByEmail(String email);

    default boolean exists(String email) {
        QMemberFile memberFile = QMemberFile.memberFile;

        return exists(memberFile.email.eq(email));
    }
}
