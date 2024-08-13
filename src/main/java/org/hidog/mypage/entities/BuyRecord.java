package org.hidog.mypage.entities;

import jakarta.persistence.*;
import org.hidog.member.entities.Member;

@Entity
public class BuyRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;
}