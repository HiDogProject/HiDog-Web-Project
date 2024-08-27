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

    private final Utils utils;
    private final MemberUtil memberUtil;

    // 마이 페이지 홈
    @GetMapping
    public String myHome(Model model) {

        commonProcess("", model); // 페이지 제목 설정을 위해 호출
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

//    @PostMapping("/post")
//    public String post(@ModelAttribute BoardDataSearch search, Model model) {
//
//        commonProcess("post", model);
//
//        search.setBid(memberUtil.getMember().getEmail());
//
//        ListData<BoardData> listData = boardInfoService.getList(search, DeleteStatus.UNDELETED);
//
//        model.addAttribute("items", listData.getItems());
//        model.addAttribute("pagination", listData.getPagination());
//
//        return utils.tpl("mypage/post");
//    }

    private void commonProcess(String mode, Model model) {

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        String pageTitle = ""; // 기본 페이지 제목

        if (mode.equals("")) {
            addCss.add("mypage/myhome");
            pageTitle = "마이 페이지";
        } else if (mode.equals("info")) {
            addCommonScript.add("fileManager");
            addScript.add("mypage/info");
            pageTitle = "회원 정보 수정";
        } else if (mode.equals("post")) {
            addCss.add("mypage/post");
            pageTitle = "내 게시글";
        } else if (model.equals("like")) {
            addCss.add("mypage/like");
            pageTitle = "찜 목록";
        } else if (model.equals("sellAndBuy")) {
            addCss.add("mypage/sellAndBuy");
            pageTitle = "상점";
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle); // 페이지 제목
    }
}