package org.hidog.mypage.repositories;

import org.hidog.member.entities.Member;
import org.hidog.mypage.entities.SellRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellRecordRepository extends JpaRepository<SellRecord, Long> {
    List<SellRecord> findByMember(Member member);
}