package org.hidog.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.hidog.global.CommonSearch;
import org.hidog.global.ListData;
import org.hidog.global.Utils;
import org.hidog.member.entities.Member;
import org.hidog.member.repositories.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("shop")
public class ShopController {

    private final BoardInfoService boardInfoService;
    private final Utils utils;
    private final MemberRepository memberRepository;

    //회원 상점
    @GetMapping("/{seq}")
    public String info(@PathVariable("seq") Long seq, @ModelAttribute CommonSearch search, Model model) {
        ListData< BoardData> data = boardInfoService.getMarketList(seq, "market", search);
        Member member = memberRepository.getReferenceById(seq);

        model.addAttribute("member", member);
        model.addAttribute("items", data.getItems());
        model.addAttribute("addCss", "board/market/list");



        return utils.tpl("shop/shop");
    }
}
