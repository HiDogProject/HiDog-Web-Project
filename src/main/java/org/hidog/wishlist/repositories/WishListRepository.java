package org.hidog.wishlist.repositories;

import org.hidog.wishlist.entities.WishList;
import org.hidog.wishlist.entities.WishListId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface WishListRepository extends JpaRepository<WishList, WishListId>, QuerydslPredicateExecutor<WishList> {


    @Query("SELECT COUNT(w) FROM WishList w WHERE w.seq = :seq")
    long countBySeq(@Param("seq") Long seq);

}
