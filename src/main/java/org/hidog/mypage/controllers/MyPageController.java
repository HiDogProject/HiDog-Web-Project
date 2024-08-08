package org.hidog.mypage.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    // 마이 페이지 홈
    @GetMapping("/myhome")
    public String mypage(Model model) {
        commonProcess("myhome", model);
        return "front/mypage/myhome";
    }

    // 마이 페이지 -> 회원 정보 확인 버튼 클릭 시 회원 정보 페이지로 이동
    @GetMapping("/info")
    public String viewMemberInfo(Model model) {
        commonProcess("info", model);
        return "front/mypage/info";
    }

    // 마이 페이지 -> 회원 정보 수정 버튼 클릭 시 본인 인증 후 회원 정보 수정 페이지로 이동
    @GetMapping("/changeInfo")
    public String changeMemberInfo(Model model) {
        commonProcess("changeInfo", model);
        // 본인 인증 로직
        return "front/mypage/changeInfo";
    }

    // 회원 정보 수정 페이지에서 수정 내용 저장 및 정보 변경, 마이 페이지 홈으로 이동
    @PostMapping("/changeInfo")
    public String saveChangedMemberInfo(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("changeInfo", model);
        if (errors.hasErrors()) {
            return "front/mypage/changeInfo";
        }
        // 회원 정보 수정 코드
        return "redirect:/mypage/myhome";
    }

    // 마이 페이지 -> 프로필 클릭 시 이미지 변경 창이 팝업으로 생성
    @GetMapping("/profile")
    public String changeProfileImage(Model model) {
        commonProcess("profile", model);
        return "front/mypage/profile";
    }

    // 프로필 이미지 변경 후 변경 내용 반영
    @PostMapping("/profile")
    public String saveProfileImage(@Valid RequestProfile form, Errors errors, Model model) {
        commonProcess("profile", model);
        if (errors.hasErrors()) {
            return "front/mypage/profile";
        }
        // 프로필 이미지 저장 코드
        return "redirect:/mypage/myhome";
    }

    // 마이 페이지 -> 찜한 목록 보기 버튼 클릭 시 찜 목록 페이지로 이동
    @GetMapping("/like")
    public String viewLike(Model model) {
        commonProcess("like", model);
        return "front/mypage/like";
    }

    // 마이 페이지 -> 작성한 글 목록 보기 버튼 클릭 시 글 목록 페이지로 이동
    @GetMapping("/post")
    public String viewMyPost(Model model) {
        commonProcess("post", model);
        return "front/mypage/post";
    }

    // 마이 페이지 -> 판매 & 구매 내역 버튼 클릭 시 판매 & 구매 내역 페이지로 이동
    @GetMapping("/sellAndBuy")
    public String viewSellAndBuy(Model model) {
        commonProcess("sellAndBuy", model);
        return "front/mypage/sellAndBuy";
    }

    /**
     * 마이 페이지 공통
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode, "myhome"); // mode 변수가 null 인지 체크 -> mode 가 null 이면 "myhome" 문자열로 초기화 -> null 이 아니면 mode 는 원래의 값 유지

        List<String> addCss = new ArrayList<>(); // 새로운 빈 ArrayList 객체 생성해 addCss 변수에 할당 -> addCss 리스트 = 이 메서드에서 사용될 CSS 파일 경로 저장
        List<String> addCommonScript = new ArrayList<>(); // 새로운 빈 ArrayList 객체 생성해 addCommonScript 변수에 할당 -> addCommonScript 리스트 = 공통으로 사용될 스크립트 파일 경로 저장
        List<String> addScript = new ArrayList<>(); // 새로운 빈 ArrayList 객체 생성해 addScript 변수에 할당 -> addScript 리스트 = 특정 페이지에서만 사용될 스크립트 파일 경로 저장

        addCss.add("mypage/style"); // 마이 페이지 공통 스타일 적용
        if (mode.equals("myhome")) { // 마이 페이지 기본 페이지
            addCss.add("mypage/myhome");
        } else if (mode.equals("info")) { // 회원 정보 확인 페이지
            addCss.add("mypage/info");
        } else if (mode.equals("changeInfo")) { // 회원 정보 수정 페이지
            addCss.add("mypage/changeInfo");
            addScript.add("mypage/changeInfo");
        } else if (mode.equals("profile")) { // 프로필 수정 (페이지 X -> 이미지 클릭 시 팝업 창 생성)
            addCss.add("mypage/profile");
            addScript.add("mypage/profile");
        } else if (mode.equals("like")) { // 찜 목록 페이지
            addCss.add("mypage/like");
        } else if (mode.equals("post")) { // 내가 쓴 글 목록 페이지
            addCss.add("mypage/post");
        } else if (mode.equals("sellAndBuy")) { // 판매 내역 & 구매 내역 페이지
            addCss.add("mypage/sellAndBuy");
        }

        model.addAttribute("addCss", addCss); // addCss 리스트 -> "addCss"라는 이름으로 model 객체에 추가 | model 객체 = 컨트롤러와 뷰 사이의 데이터 전달하는 역할 | 뷰에서 "addCss"라는 이름 사용해 addCss 리스트 내용 접근
        model.addAttribute("addCommonScript", addCommonScript); // addCommonScript 리스트 -> "addCommonScript"라는 이름으로 model 객체에 추가 | 뷰에서 "addCommonScript"라는 이름 사용해 addCommonScript 리스트 내용 접근
        model.addAttribute("addScript", addScript); // addScript 리스트 -> "addScript"라는 이름으로 model 객체에 추가 | 뷰에서 "addScript"라는 이름 사용해 addScript 리스트 내용 접근
    }
}