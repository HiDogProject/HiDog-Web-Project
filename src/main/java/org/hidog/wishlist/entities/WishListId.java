package org.hidog.wishlist.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hidog.member.entities.Member;
import org.hidog.wishlist.constants.WishType;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class WishListId { // 기본키가 3개라 id클래스 생성함
    private Long seq;
    private WishType wishType;
    private Member member;
}
