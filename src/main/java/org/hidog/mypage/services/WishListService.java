package org.hidog.mypage.services;

import org.hidog.mypage.entities.WishList;
import org.hidog.mypage.repositories.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListService {

    private final WishListRepository wishListRepository;

    // 생성자 주입 -> 안 적으면 오류 발생 ㅠㅠ
    public WishListService(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    // 찜 목록 조회
    public List<WishList> getWishListForUser(Long userId) {
        return wishListRepository.findByUserId(userId);
    }

    // 찜 목록 상품 삭제
    public void removeProductFrimWishList(Long userId, Long productId) {
        wishListRepository.deleteByUserIdAndProductId(userId, productId);
    }
}