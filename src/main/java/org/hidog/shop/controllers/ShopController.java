package org.hidog.shop.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.hidog.global.CommonSearch;
import org.hidog.global.ListData;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.hidog.member.repositories.MemberRepository;
import org.hidog.order.entities.OrderItem;
import org.hidog.order.services.OrderItemInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("shop")
public class ShopController {

    private final BoardInfoService boardInfoService;
    private final OrderItemInfoService orderItemInfoService;
    private final Utils utils;
    private final MemberUtil memberUtil;
    private final MemberRepository memberRepository;

    //회원 상점
    @GetMapping("/{seq}")
    public String info(@PathVariable("seq") Long seq, @ModelAttribute CommonSearch search, Model model) {
        ListData< BoardData> data = boardInfoService.getMarketList(seq, "market", search);
        Member member = memberRepository.getReferenceById(seq);

        model.addAttribute("member", member);
        model.addAttribute("items", data.getItems());
        model.addAttribute("addCss", List.of("board/market/list","shop/style","style"));

        return utils.tpl("shop/shop");
    }

    @GetMapping("/sell")
    public String listSell(@ModelAttribute CommonSearch search, Model model){
        Long mSeq = memberUtil.getMember().getSeq();
        List<OrderItem> orderItems = orderItemInfoService.getByMemberSeq(mSeq);
        model.addAttribute("items", orderItems);
        model.addAttribute("addCss", List.of("shop/sell"));
        return utils.tpl("shop/sell");
    }

    @GetMapping("/buy")
    public String listBuy(){
        return utils.tpl("shop/buy");
    }

}
