package org.hidog.mypage.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.global.configs.FileProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final FileProperties properties;

    @GetMapping("/myhome")
    public String mypage(Model model, HttpSession session) {
        // 세션에서 프로필 이미지 경로 가져와 모델에 추가
        String profileImage = (String) session.getAttribute("profileImage");

        if (profileImage == null) {
            profileImage = "/images/default-profile.png"; // 기본 이미지 경로
        }
        model.addAttribute("profileImage", profileImage);

        commonProcess("myhome", model); // 공통 프로세스 처리

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
    public String changeInfo(Model model, HttpSession session) {
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        model.addAttribute("authenticated", authenticated != null && authenticated);

        // 인증된 경우, 프로필 객체를 모델에 추가
        if (authenticated != null && authenticated) {
            RequestProfile profile = new RequestProfile();
            // 실제 사용자 정보를 불러와 profile 에 설정
            profile.setUserName("인간"); // 예시
            profile.setEmail("user01@test.org"); // 예시
            profile.setPassword("aA12345!"); // 예기
            profile.setAddress("서울특별시 마포구 신촌로 176"); // 예시
            model.addAttribute("profile", profile);
        }

        return "front/mypage/changeInfo";
    }

    @PostMapping("/changeInfo")
    public String changeInfo(@RequestParam(value = "password", required = false) String password,
                             @Valid @ModelAttribute("profile") RequestProfile form,
                             Errors errors, HttpSession session, Model model) {

        Boolean authenticated = (Boolean) session.getAttribute("authenticated");

        if (password != null) {
            // 비밀번호 확인 로직
            String correctPassword = "userActualPassword"; // 실제 비밀번호 로직
            if (password.equals(correctPassword)) {
                session.setAttribute("authenticated", true);
                authenticated = true;
            } else {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            }
        }

        if (authenticated != null && authenticated) {
            if (errors.hasErrors()) {
                return "front/mypage/changeInfo";
            }
            // 회원 정보 수정 코드 (서비스 호출 등) 예: mypageService.updateUserProfile(form);
            return "redirect:/mypage/myhome";
        }

        model.addAttribute("authenticated", false);
        return "front/mypage/changeInfo";
    }

    // 마이 페이지 -> 프로필 클릭 시 이미지 변경 창이 팝업으로 생성
    @GetMapping("/profile")
    public String changeProfileImage(Model model) {
        commonProcess("profile", model);
        return "front/mypage/profile";
    }

    // 프로필 이미지 변경 후 변경 내용 반영
    @PostMapping("/profile")
    public String saveProfileImage(@RequestParam("profileImage") MultipartFile profileImage, HttpSession session, Model model) {
        // 이미지 저장 경로
        String uploadDirectory = properties.getPath();
        String fileName = profileImage.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);

        try {
            Files.createDirectories(filePath.getParent()); // 디렉토리가 없는 경우 -> 생성
            profileImage.transferTo(new File(filePath.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "이미지 저장 실패");
            return "front/mypage/profile";
        }

        // 세션에 저장된 사용자 정보에 프로필 이미지 경로 저장
        session.setAttribute("profileImage", properties.getUrl() + fileName);

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
        switch (mode) {
            case "myhome":
                addCss.add("mypage/myhome"); // 마이 페이지 홈
                break;
            case "info":
                addCss.add("mypage/info"); // 회원 정보 확인 페이지
                break;
            case "changeInfo":
                addCss.add("mypage/changeInfo");
                addScript.add("mypage/changeInfo"); // 회원 정보 수정 페이지
                break;
            case "profile":
                addCss.add("mypage/profile");
                addScript.add("mypage/profile"); // 프로필 페이지
                break;
            case "like":
                addCss.add("mypage/like"); // 찜 목록 페이지
                break;
            case "post":
                addCss.add("mypage/post"); // 글 목록 페이지
                break;
            case "sellAndBuy":
                addCss.add("mypage/sellAndBuy"); // 판매 & 구매 내역 페이지
                break;
        }

        model.addAttribute("addCss", addCss); // addCss 리스트 -> "addCss"라는 이름으로 model 객체에 추가 | model 객체 = 컨트롤러와 뷰 사이의 데이터 전달하는 역할 | 뷰에서 "addCss"라는 이름 사용해 addCss 리스트 내용 접근
        model.addAttribute("addCommonScript", addCommonScript); // addCommonScript 리스트 -> "addCommonScript"라는 이름으로 model 객체에 추가 | 뷰에서 "addCommonScript"라는 이름 사용해 addCommonScript 리스트 내용 접근
        model.addAttribute("addScript", addScript); // addScript 리스트 -> "addScript"라는 이름으로 model 객체에 추가 | 뷰에서 "addScript"라는 이름 사용해 addScript 리스트 내용 접근
        model.addAttribute("pageName", mode); // 현재 페이지 이름을 모델에 추가
    }
}