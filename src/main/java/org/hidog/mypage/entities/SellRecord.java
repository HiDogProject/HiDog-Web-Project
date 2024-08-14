package org.hidog.mypage.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hidog.member.entities.Member;

import java.time.LocalDate;

@Entity @Getter @Setter
public class SellRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "member_id") // 명시적으로 조인 칼럼 설정
    private Member member;

    private String itemName;
    private Double price;
    private LocalDate date;
}