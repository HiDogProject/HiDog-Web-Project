package org.hidog.market.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private final Utils utils;

    /**
     * 마켓 페이지
     * @return
     */
    @GetMapping()
    public String index(){
        return utils.tpl("market/index");
    }

    /**
     * 상품등록 페이지
     */
    @GetMapping("/products/new")
    public String newProduct(@ModelAttribute RequestProduct form, Model model){
        return utils.tpl("market/products/new");
    }

    /**
     * 상품수정 페이지
     */
    @GetMapping("/products/edit")
    public String editProduct(){
        return utils.tpl("market/products/edit");
    }

    /**
     * 상품 저장,수정 처리
     * @return
     */
    @PostMapping("/products/save")
    public String saveProduct(){
        return utils.redirectUrl("market/index");
    }

    /**
     * 상품 삭제 처리
     * @return
     */
    @DeleteMapping("/products/delete")
    public String deleteProduct(){
        return utils.redirectUrl("market/index");
    }

}
