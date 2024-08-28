package org.hidog.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.board.advices.BoardControllerAdvice;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.board.services.BoardInfoService;
import org.hidog.global.CommonSearch;
import org.hidog.global.ListData;
import org.hidog.global.Utils;
import org.hidog.global.exceptions.ExceptionProcessor;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.hidog.member.services.MemberSaveService;
import org.hidog.mypage.validators.ProfileUpdateValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    private final BoardInfoService boardInfoService;

    // 마이 페이지 홈
    @GetMapping
    public String myHome(Model model) {

        commonProcess("", model); // 페이지 제목 설정을 위해 호출

        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지

        return utils.tpl("mypage/myhome");
    }

    @GetMapping("/info")
    public String info(@ModelAttribute RequestProfile form, Model model) {

        commonProcess("info", model);

        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지

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

        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지

        profileUpdateValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return utils.tpl("mypage/info");
        }

        memberSaveService.save(form);

        return "redirect:" + utils.redirectUrl("/mypage");
    }

    // 게시글 목록
    @GetMapping("/post")
    public String myPost(@ModelAttribute CommonSearch search, Model model) {

        commonProcess("post", model);

        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지

        ListData<BoardData> data = boardInfoService.getMyList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("mypage/post");
    }

    // 찜 목록
    @GetMapping("/wishlist")
    public String wishlist(@ModelAttribute CommonSearch search, Model model) {

        commonProcess("wishlist", model);

        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지

        ListData<BoardData> data = boardInfoService.getWishList(search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());

        return utils.tpl("mypage/wishlist");
    }


    private void commonProcess(String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "";

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
            addCss.add("mypage/info");
            pageTitle = "회원 정보 수정";
        } else if (mode.equals("post")) {
            addCss.add("mypage/post");
            pageTitle = "내 게시글";
        } else if (mode.equals("wishlist")) {
            addCss.add("mypage/wishlist");
            pageTitle = "찜 목록";
        } else if (mode.equals("shop")) {
            addCss.add("mypage/shop");
            pageTitle = "상점";
        }

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCss", addCss);
        model.addAttribute("pageTitle", pageTitle); // 페이지 제목
    }
}