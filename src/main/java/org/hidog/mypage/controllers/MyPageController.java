package org.hidog.mypage.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hidog.file.entities.FileInfo;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.hidog.member.services.MemberService;
import org.hidog.mypage.services.MypageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final Utils utils;
    private final MemberUtil memberUtil;
    private final MemberService memberService;
    private final MypageService mypageService;

    @Value("${file.upload.url}")
    private String fileUploadUrl;

    /**
     * 1) mypage/myhome 입력 시 마이 페이지 홈으로 이동
     * 2) 마이 페이지 홈 -> 버튼 O (프로필 이미지, 회원 정보 확인 버튼, 찜 목록 버튼, 게시글 버튼, 판매 내역 & 구매 내역 버튼)
     * 3) 원형 프로필 클릭 시 프로필 이미지 수정 팝업 생성 -> 이미지 저장 버튼 클릭 시 수정된 이미지로 변경 및 마이 페이지 홈에 가만히 있음
     * 4) 회원 정보 확인 버튼 클릭 시 회원 정보 페이지 (/mypage/info)로 이동 -> 로그인한 사용자의 정보가 나오고 그 아래에 메인 페이지 버튼 / 회원 정보 수정 버튼
     * -> 회원 정보 수정 버튼 클릭 시 로그인할 때 사용한 비밀번호로 본인 인증 -> 성공 시 회원 정보 수정 페이지 (/mypage/changInfo)로 이동 | 실패 시 마이 페이지로 이동
     * 5) 찜 목록 버튼 클릭 시 찜 목록 페이지 (/mypage/like)로 이동 -> 사진첩 처럼 이미지 목록으로 찜한 내역 목록화된 페이지 나옴
     * 6) 게시글 버튼 클릭 시 게시글 페이지 (/mypage/post)로 이동 -> 표 형태로 작성한 글 목록 나옴
     * 7) 판매 내역 & 구매 내역 버튼 클릭 시 판매 내역 & 구매 내역 페이지 (/mypage/sellAndBuy)로 이동 -> 5)와 동일하게 목록화된 페이지 나옴
     */

    // 마이 페이지 홈 + 프로필 수정 원
    @GetMapping("/myhome")
    public String myHome(Model model) {
        commonProcess("myhome", model);
        model.addAttribute("profileImage", memberUtil.getProfileImageUrl());
        return utils.tpl("mypage/myhome");
    }

    // 프로필 수정 팝업 창
    @PostMapping("/myhome")
    public String updateProfileImage(@RequestParam("newProfileImage") MultipartFile newProfileImage, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getSession().getAttribute("userId");

            // 파일 업로드 및 FileInfo 반환
            FileInfo uploadedFile = mypageService.uploadProfileImage(newProfileImage, userId);

            // 새 이미지 URL 생성
            String newImageUrl = "/files/" + uploadedFile.getSeq() + uploadedFile.getExtension();

            // 프로필 이미지 URL 업데이트
            mypageService.updateProfileImage(userId, newImageUrl);

            // 새 프로필 이미지 URL 저장
            request.getSession().setAttribute("profileImage", newImageUrl);

            return "redirect:" + utils.redirectUrl("mypage/myhome");

        } catch (IOException e) {
            request.getSession().setAttribute("error", "이미지 변경 중에 오류가 발생했습니다.");
            return "redirect:" + utils.redirectUrl("mypage/myhome");
        } catch (Exception e) {
            request.getSession().setAttribute("error", e.getMessage());
            return "redirect:" + utils.redirectUrl("mypage/myhome");
        }
    }

    // 마이 페이지 -> 회원 정보 확인 페이지
    @GetMapping("/info")
    public String memberInfo(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 회원 정보 확인할 수 있도록
        commonProcess("info", model);

        if (memberUtil.isLogin()) { // 로그인한 경우 : 사용자 -> 회원 정보 확인 가능
            model.addAttribute("member", memberUtil.getMember());
        } else { // 로그인 하지 않은 경우
            model.addAttribute("errorMessage", "로그인 후 이용할 수 있습니다!");
        }

        return utils.tpl("mypage/info");
    }

    // 회원 정보 확인 페이지 -> 회원 정보 수정 페이지 이동
    @GetMapping("/changeInfo")
    public String changeInfoPage(Model model) {
        if (!memberUtil.isLogin()) {
            return "redirect:" + utils.redirectUrl("mypage/myhome"); // 미로그인인 경우 마이 페이지로 이동
        }

        Member member = memberUtil.getMember();
        model.addAttribute("member", member);
        commonProcess("changeInfo", model);

        return utils.tpl("mypage/changeInfo");
    }

    // 회원 정보 수정 페이지
    @PostMapping("/changeInfo")
    public String changeInfo(@RequestParam String userName, @RequestParam String password, @RequestParam String address, Model model) {
        if (!memberUtil.isLogin()) {
            return "redirect:" + utils.redirectUrl("mypage/myhome");
        }

        Member member = memberUtil.getMember();
        if (member == null) {
            model.addAttribute("errorMessage", "로그인 정보가 유효하지 않습니다.");
            return utils.tpl("mypage/changeInfo");
        }

        member.setUserName(userName);
        member.setPassword(password);
        member.setAddress(address);

        try {
            memberService.updateMember(member);
            model.addAttribute("successMessage", "회원 정보가 수정되었습니다.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 정보 수정 중에 오류가 발생했습니다.");
            e.printStackTrace();
        }

        commonProcess("changeInfo", model);
        return utils.tpl("mypage/changeInfo");
    }

    // 찜 목록 페이지
    @GetMapping("/like")
    public String like(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인이 찜한 목록 확인할 수 있도록
        commonProcess("like", model);
        return utils.tpl("mypage/like");
    }

    // 내가 쓴 게시글 페이지
    @GetMapping("/post")
    public String post(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인이 쓴 게시글 확인할 수 있도록
        commonProcess("post", model);
        return utils.tpl("mypage/post");
    }

    // 판매 내역 & 구매 내역 페이지
    @GetMapping("/sellAndBuy")
    public String sellAndBuy(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인의 판매 & 구매 내역 확인할 수 있도록
        commonProcess("sellAndBuy", model);
        return utils.tpl("mypage/sellAndBuy");
    }

/*  // 마이 페이지 -> 찜한 목록 보기 버튼 클릭 시 찜 목록 페이지로 이동
    @GetMapping("/like")
    public String viewLike(Model model, @RequestParam(value = "productId", required = false) Long productId) {
        Long userId = getCurrentUserId();

        // 삭제 버튼 클릭 시 찜 목록 삭제
        if (productId != null) {
            wishListService.removeProductFrimWishList(userId, productId);
        }

        // 찜 목록 불러옴
        List<WishList> wishlist = wishListService.getWishListForUser(userId);
        model.addAttribute("wishlist", wishlist);

        return utils.tpl("mypage/like");
    }

    // 현재 사용자 ID 가져옴
    private Long getCurrentUserId() {
        return 1L;
    }

    // 마이 페이지 -> 작성한 글 목록 보기 버튼 클릭 시 글 목록 페이지 이동
    @GetMapping("/post")
    public String viewMyPost(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            // 사용자 이름 -> 사용자 ID 조회
            Long userId = findUserIdByUsername(username);

            // 사용자 ID로 게시글 조회
            List<Post> posts = postService.getPostsByUserId(userId);

            model.addAttribute("posts", posts);
            return utils.tpl("mypage/post");
        }

        // 인증된 사용자가 없으면 로그인 페이지로 이동
        return utils.tpl("mypage/post"); // return "redirect:/login"; <- 로그인 완성되면 이거로 바꾸기!!
    }

    private Long findUserIdByUsername(String username) {
        return 1L;
    }

    // 마이 페이지 -> 판매 & 구매 내역 버튼 클릭 시 판매 & 구매 내역 페이지로 이동
    /*@GetMapping("/sellAndBuy")
    public String viewSellAndBuy(Model model) {
        commonProcess("sellAndBuy", model);
        return utils.tpl("mypage/sellAndBuy");
    } */

/*    @GetMapping("/sellAndBuy")
    public String viewSellAndBuy(Model model, HttpSession session) {
        // 이메일을 세션에서 가져오기
        String email = (String) session.getAttribute("userEmail");

        // 이메일로 사용자 정보 조회
        Member member = memberRepository.findByEmail(email)
                .orElse(null); // 로그인하지 않은 사용자는 null 반환

        // 로그인하지 않은 경우에도 빈 리스트를 반환합니다.
        List<SellRecord> sellRecords = (member != null) ? sellRecordRepository.findByMember(member) : List.of();
        List<BuyRecord> buyRecords = (member != null) ? buyRecordRepository.findByMember(member) : List.of();

        // 조회된 데이터 모델에 추가
        model.addAttribute("sellRecords", sellRecords);
        model.addAttribute("buyRecords", buyRecords);

        return utils.tpl("mypage/sellAndBuy");
    } */

    /**
     * 마이 페이지 공통
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode, "myhome"); // mode 변수가 null 인지 체크 -> mode 가 null 이면 "myhome" 문자열로 초기화 -> null 이 아니면 mode 는 원래의 값 유지

        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("mypage/style"); // 마이 페이지 공통
        switch (mode) {
            case "myhome": // 마이 페이지 홈
                addCss.add("mypage/myhome");
                break;
            case "info": // 회원 정보 확인 페이지
                addCss.add("mypage/info");
                break;
            case "changeInfo": // 회원 정보 수정 페이지
                addCss.add("mypage/changeInfo");
                addScript.add("mypage/changeInfo");
                break;
            case "profile": // 프로필 페이지
                addCss.add("mypage/profile");
                addScript.add("mypage/profile");
                break;
            case "like": // 찜 목록 페이지
                addCss.add("mypage/like");
                break;
            case "post": // 글 목록 페이지
                addCss.add("mypage/post");
                break;
            case "sellAndBuy": // 판매 & 구매 내역 페이지
                addCss.add("mypage/sellAndBuy");
                break;
            default:
                addCss.add("mypage/myhome");
                break;
        }

        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageName", mode);
    }
}