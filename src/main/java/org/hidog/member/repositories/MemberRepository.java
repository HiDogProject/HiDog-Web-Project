package org.hidog.member.repositories;


import org.hidog.member.entities.Member;
import org.hidog.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByUserName(String userName);  // 닉네임으로 조회

    default boolean exists(String email) {
        QMember member = QMember.member;
        return exists(member.email.eq(email));
    }

    default boolean existsByUserName(String userName) {  // 닉네임으로 존재 여부 확인
        QMember member = QMember.member;
        return exists(member.userName.eq(userName));
    }
}

