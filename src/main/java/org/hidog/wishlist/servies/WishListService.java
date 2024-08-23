package org.hidog.wishlist.servies;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.hidog.member.MemberUtil;
import org.hidog.wishlist.constants.WishType;
import org.hidog.wishlist.entities.QWishList;
import org.hidog.wishlist.entities.WishList;
import org.hidog.wishlist.entities.WishListId;
import org.hidog.wishlist.repositories.WishListRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.desc;

@Service
@RequiredArgsConstructor
public class WishListService {
    private final MemberUtil memberUtil;
    private final WishListRepository wishListRepository;

    /**
     *
     * @param seq
     * @param type
     */
    public void add(Long seq, WishType type) {
        if (memberUtil.isLogin()) { // 이거말고 스프링시큐리티의 프리오더라이즈도 있다고 하심
            return;
        }

        WishListId wishListId = new WishListId(seq, type, memberUtil.getMember());
        wishListRepository.deleteById(wishListId);
        wishListRepository.flush();
    }


    /**
     *
     * @param seq
     * @param type
     */
    public void remove(Long seq, WishType type) {
        if (!memberUtil.isLogin()) {
            return;
        }

        WishListId wishListId = new WishListId(seq, type, memberUtil.getMember());
        wishListRepository.deleteById(wishListId);
        wishListRepository.flush();
    }


    /**
     * 타입별로 내가 등록한 게시글만 가져오기
     *
     * @param type
     * @return
     */
    public List<Long> getList(WishType type) {
        if (!memberUtil.isLogin()) {
            return null;
        }
        BooleanBuilder builder = new BooleanBuilder();
        QWishList wishList = QWishList.wishList;
        builder.and(wishList.member.eq(memberUtil.getMember()))
                .and(wishList.wishType.eq(type));

        List<Long> items = ((List<WishList>)wishListRepository.findAll(builder, Sort.by(desc("createdAt")))).stream().map(WishList::getSeq).toList();


        return items;
    }
}
