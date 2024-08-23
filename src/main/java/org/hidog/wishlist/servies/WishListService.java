package org.hidog.wishlist.servies;

import lombok.RequiredArgsConstructor;
import org.hidog.member.MemberUtil;
import org.hidog.wishlist.entities.WishList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final MemberUtil memberUtil;

    /**
     *
     * @param seq
     * @param type
     */
    public void add(Long seq, WishList type) {
        if (memberUtil.isLogin()) { // 이거말고 스프링시큐리티의 프리오더라이즈도 있다고 하심
            return;
        }

        WishList wishList = WishList.builder()
                .build();
    }

    /**
     *
     * @param seq
     * @param type
     */
    public void remove(Long seq, WishList type) {

    }

    /**
     * 타입별로 내가 등록한 게시글만 가져오기
     *
     * @param type
     * @return
     */
    public List<Long> getList(WishList type) {
        return null;
    }
}
