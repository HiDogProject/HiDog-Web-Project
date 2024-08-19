package org.hidog.member.repositories;

import org.hidog.member.entities.Member;
import org.hidog.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByUserName(String userName);  // 닉네임으로 조회

    default boolean existsByEmail(String email) {
        QMember member = QMember.member;
        return exists(member.email.eq(email));
    }
    
    boolean existsByUserName(String userName);

    @Modifying @Query("UPDATE Member m SET m.profileImageFilename = :newImageUrl WHERE m.seq = :memberId")
    void updateProfileImageUrl(Long memberId, String newImageUrl);
}