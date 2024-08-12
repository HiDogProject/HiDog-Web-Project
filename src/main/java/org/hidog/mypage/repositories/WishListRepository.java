package org.hidog.mypage.repositories;

import org.hidog.mypage.entities.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    // 찜 목록 조회
    List<WishList> findByUserId(Long userId);

    // 특정 상품을 찜 목록에서 삭제
    void deleteByUserIdAndProductId(Long userId, Long productId);
}