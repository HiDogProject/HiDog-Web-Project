package org.hidog.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.board.advices.BoardControllerAdvice;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.global.Utils;
import org.hidog.global.exceptions.ExceptionProcessor;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.hidog.member.services.MemberSaveService;
import org.hidog.mypage.validators.ProfileUpdateValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController implements ExceptionProcessor {

    private final ProfileUpdateValidator profileUpdateValidator;
    private final MemberSaveService memberSaveService;
    private final BoardConfigInfoService boardConfigInfoService;

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final BoardControllerAdvice board;

    // 마이 페이지 홈
    @GetMapping("/myhome")
    public String myHome() {

        return utils.tpl("mypage/myhome");
    }

    @GetMapping("/info")
    public String info(@ModelAttribute RequestProfile form, Model model) {

        commonProcess("info", model);

        Member member = memberUtil.getMember();
        form.setUserName(member.getUserName());
        form.setAddress(member.getAddress());
        form.setDetailAddress(member.getDetailAddress());
        form.setEmail(member.getEmail());

        return utils.tpl("mypage/info");
    }

    //내 상점
    @GetMapping("/shop/{seq}")
    public String info(@PathVariable("seq") Long seq, Model model) {
        commonProcess("", model);
        List<String[]> board = boardConfigInfoService.getBoardList();
        System.out.println(board + "!!!");


        return utils.tpl("myPage/shop");
    }

    // 회원 정보 수정
    @PostMapping("/info")
    public String infoSave(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("info", model);

        profileUpdateValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/info");
        }

        memberSaveService.save(form);

        return "redirect:" + utils.redirectUrl("/mypage/myhome");
    }

    private void commonProcess(String mode, Model model) {
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        if (mode.equals("info")) {
            addCommonScript.add("fileManager");
            addScript.add("mypage/info");
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
    }
}