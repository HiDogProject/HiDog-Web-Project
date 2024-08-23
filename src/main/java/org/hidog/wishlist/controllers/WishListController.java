package org.hidog.wishlist.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.exceptions.RestExceptionProcessor;
import org.hidog.wishlist.constants.WishType;
import org.hidog.wishlist.servies.WishListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // ajax를 위한 레스트 컨트롤러 구성
@RequestMapping("/wish")
@RequiredArgsConstructor
public class WishListController implements RestExceptionProcessor {
    private final WishListService wishListService;

    /**
     * wishlist에 추가
     *
     * @param type
     * @param seq
     * @return
     */
    @GetMapping("/{type}/{seq}")
    public ResponseEntity<Void> add(@PathVariable("type") String type, @PathVariable("seq") Long seq) {

        wishListService.add(seq, WishType.valueOf(type));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
