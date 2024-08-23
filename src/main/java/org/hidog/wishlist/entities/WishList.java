package org.hidog.wishlist.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hidog.global.entities.BaseEntity;
import org.hidog.member.entities.Member;
import org.hidog.wishlist.constants.WishType;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
@IdClass(WishListId.class) // 기본키 3개
public class WishList extends BaseEntity {
    @Id
    private Long seq; // 등록번호

    @Id
    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private WishType wishType;

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // 하나의 멤버가 여러개의 게시글
    private Member member;
}
