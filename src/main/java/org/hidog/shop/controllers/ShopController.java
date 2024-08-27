package org.hidog.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.global.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("shop")
public class ShopController {

    private final BoardConfigInfoService boardConfigInfoService;
    private final Utils utils;

    //내 상점
    @GetMapping("/{seq}")
    public String info(@PathVariable("seq") Long seq, Model model) {
        List<String[]> boardList = boardConfigInfoService.getBoardList("market");
        List<String> bids = new ArrayList<>();
        for(String[] bid : boardList){
            bids.add(bid[0]);
        }
        return utils.tpl("shop/shop");
    }
}
