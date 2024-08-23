package org.hidog.board.advices;

import lombok.RequiredArgsConstructor;
import org.hidog.wishlist.constants.WishType;
import org.hidog.wishlist.servies.WishListService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice("org.hidog.board") // 게시판 관련된 공통 데이터 유지
@RequiredArgsConstructor
public class BoardControllerAdvice {
    private final WishListService wishListService;

    @ModelAttribute("boardWishList")
    public List<Long> boardWishList() {
        return wishListService.getList(WishType.BOARD);
    }
}
