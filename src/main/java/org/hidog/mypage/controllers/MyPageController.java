package org.hidog.mypage.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.hidog.member.services.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 1) mypage/myhome 입력 시 마이 페이지 홈으로 이동
     * 2) 마이 페이지 홈 -> 버튼 O (회원 정보 확인 버튼, 프로필 버튼, 찜 목록 버튼, 게시글 버튼, 판매 내역 & 구매 내역 버튼)
     * 3) 회원 정보 확인 버튼 클릭 시 회원 정보 페이지 (/mypage/info)로 이동 -> 로그인한 사용자의 정보가 나오고 그 아래에 메인 페이지 버튼 / 회원 정보 수정 버튼
     * -> 회원 정보 수정 버튼 클릭 시 로그인할 때 사용한 비밀번호로 본인 인증 -> 성공 시 회원 정보 수정 페이지 (/mypage/changInfo)로 이동 | 실패 시 마이 페이지로 이동
     * 4) 원형 프로필 클릭 시 프로필 이미지 수정 팝업 생성 -> 이미지 저장 버튼 클릭 시 수정된 이미지로 변경 및 마이 페이지 홈에 가만히 있음
     * 5) 찜 목록 버튼 클릭 시 찜 목록 페이지 (/mypage/like)로 이동 -> 사진첩 처럼 이미지 목록으로 찜한 내역 목록화된 페이지 나옴
     * 6) 게시글 버튼 클릭 시 게시글 페이지 (/mypage/post)로 이동 -> 표 형태로 작성한 글 목록 나옴
     * 7) 판매 내역 & 구매 내역 버튼 클릭 시 판매 내역 & 구매 내역 페이지 (/mypage/sellAndBuy)로 이동 -> 5)와 동일하게 목록화된 페이지 나옴
     */
    // 마이 페이지 홈
    @GetMapping("/myhome")
    public String myHome(Model model) {
        commonProcess("myhome", model);
        return utils.tpl("mypage/myhome");
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

    @GetMapping("/changeInfo")
    public String changeInfoPage(Model model) {
        if (!memberUtil.isLogin()) {
            return "redirect:/mypage/myhome"; // 로그인하지 않은 경우 마이 페이지로 이동
        }

        Member member = memberUtil.getMember();
        model.addAttribute("member", member);
        commonProcess("changeInfo", model);

        return utils.tpl("mypage/changeInfo"); // 템플릿 경로
    }

    @PostMapping("/changeInfo")
    public String changeInfo(@RequestParam String userName, @RequestParam String password, @RequestParam String address, Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 회원 정보 수정할 수 있도록
        if (!memberUtil.isLogin()) {
            return "redirect:/mypage/myhome";
        }
        Member member = memberUtil.getMember();
        member.setUserName(userName);
        member.setPassword(password);
        member.setAddress(address);

        memberService.updateMember(member); // 회원 정보 수정 서비스 호출

        model.addAttribute("successMessage", "회원 정보가 수정되었습니다.");
        commonProcess("changeInfo", model);
        return utils.tpl("mypage/changeInfo");
    }

    @GetMapping("/profile")
    public String profile(Model model) { // 프로필 이미지 페이지 오픈
        commonProcess("profile", model);
        return utils.tpl("mypage/profile");
    }

    @PostMapping("/profile")
    public String updateProfileImage(MultipartFile profileImage) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 프로필 이미지 수정할 수 있도록
        return "redirect:" + utils.redirectUrl("mypage/myhome");
    }

    @GetMapping("/like")
    public String like(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인이 찜한 목록 확인할 수 있도록
        commonProcess("like", model);
        return utils.tpl("mypage/like");
    }

    @GetMapping("/post")
    public String post(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인이 쓴 게시글 확인할 수 있도록
        commonProcess("post", model);
        return utils.tpl("mypage/post");
    }

    @GetMapping("/sellAndBuy")
    public String sellAndBuy(Model model) { // CommonControllerAdvice 의 isLogin -> 로그인한 경우 본인의 판매 & 구매 내역 확인할 수 있도록
        commonProcess("sellAndBuy", model);
        return utils.tpl("mypage/sellAndBuy");
    }

/*  private final FileProperties properties;
    private final WishListService wishListService;
    private final PostService postService;
    private final MyPageService myPageService;
    private final MemberRepository memberRepository;
    private final SellRecordRepository sellRecordRepository;
    private final BuyRecordRepository buyRecordRepository;
    private final Utils utils;

    // 마이 페이지 기본 홈 (+ 프로필 이미지 저장 시 마이 페이지 홈으로 이동 경로)
    @GetMapping("/myhome")
    public String mypage(Model model, HttpSession session) {
        String profileImage = (String) session.getAttribute("profileImage");

        if (profileImage == null) {
            profileImage = "/images/default-profile.png"; // 기본 이미지 경로
        }
        model.addAttribute("profileImage", profileImage);

        commonProcess("myhome", model);

        return utils.tpl("mypage/myhome");
    }

    // 마이 페이지 -> 회원 정보 확인 버튼 클릭 시 회원 정보 페이지로 이동
    @GetMapping("/info")
    public String viewMemberInfo(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userEmail");

        if (email != null) {
            Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

            RequestProfile profile = new RequestProfile();
            profile.setUserName(member.getUserName());
            profile.setEmail(member.getEmail());
            profile.setAddress(member.getAddress());
            profile.setPassword("********");

            model.addAttribute("profile", profile);
            return utils.tpl("mypage/info");
        } else {
            return "redirect:" + utils.redirectUrl("/member/login");
        }
    }

    // 마이 페이지 -> 회원 정보 수정 버튼 클릭 시 본인 인증 후 회원 정보 수정 페이지로 이동
    @GetMapping("/changeInfo")
    public String changeInfo(Model model, HttpSession session) {
        // 세션에서 인증 상태 확인
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        model.addAttribute("authenticated", authenticated != null && authenticated);

        // 인증된 경우, 프로필 객체를 모델에 추가
        if (authenticated != null && authenticated) {
            String email = (String) session.getAttribute("userEmail");
            // 디버깅 로그 추가
            System.out.println("Session email in GET /changeInfo: " + email);
            if (email != null) {
                Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
                RequestProfile profile = new RequestProfile();
                profile.setUserName(member.getUserName());
                profile.setEmail(member.getEmail());
                profile.setAddress(member.getAddress());
                model.addAttribute("profile", profile);
            } else {
                model.addAttribute("error", "이메일을 찾을 수 없습니다.");
            }
        }
        return utils.tpl("mypage/changeInfo");
    }

    @PostMapping("/changeInfo")
    public String changeInfo(@RequestParam(value = "password", required = false) String password,
                             @Valid @ModelAttribute("profile") RequestProfile form,
                             Errors errors, HttpSession session, Model model) {

        // 세션에서 인증 상태 확인
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        String email = (String) session.getAttribute("userEmail");

        if (password != null) {
            if (email == null) {
                model.addAttribute("error", "사용자 정보가 세션에 없습니다.");
                return utils.tpl("mypage/changeInfo");
            }

            // 비밀번호 확인
            boolean isPasswordCorrect = myPageService.checkPassword(email, password);

            if (isPasswordCorrect) {
                session.setAttribute("authenticated", true);
                authenticated = true;
            } else {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
                return utils.tpl("mypage/changeInfo");
            }
        }

        if (authenticated != null && authenticated) {
            if (errors.hasErrors()) {
                return utils.tpl("mypage/changeInfo");
            }

            // 회원 정보 수정
            myPageService.updateMemberInfo(email, form);
            session.setAttribute("userInfoChanged", true);
            return "redirect:" + utils.redirectUrl("/mypage/myhome");
        }

        model.addAttribute("authenticated", false);
        return utils.tpl("mypage/changeInfo");
    }

    /*@PostMapping("/changeInfo")
    public String changeInfo(@RequestParam(value = "password", required = false) String password,
                             @Valid @ModelAttribute("profile") RequestProfile form,
                             Errors errors, HttpSession session, Model model) {

        // 세션에서 인증 상태 확인
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");

        // 디버깅 로그 추가
        String email = (String) session.getAttribute("userEmail");
        System.out.println("Session email in POST /changeInfo: " + email);

        if (password != null) {
            if (email == null) {
                model.addAttribute("error", "사용자 정보가 세션에 없습니다.");
                return utils.tpl("mypage/changeInfo");
            }

            // 비밀번호 확인
            boolean isPasswordCorrect = myPageService.checkPassword(email, password);

            if (isPasswordCorrect) {
                session.setAttribute("authenticated", true);
                authenticated = true;
            } else {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            }
        }

        if (authenticated != null && authenticated) {
            if (errors.hasErrors()) {
                return utils.tpl("mypage/changeInfo");
            }

            // 회원 정보 수정
            myPageService.save(form);
            session.setAttribute("userInfoChanged", true);
            return "redirect:" + utils.redirectUrl("/mypage/myhome");
        }

        model.addAttribute("authenticated", false);
        return utils.tpl("mypage/changeInfo");
    } */

    // 마이 페이지 -> 프로필 클릭 시 이미지 변경 창이 팝업으로 생성
/*    @GetMapping("/profile")
    public String changeProfileImage(Model model) {
        commonProcess("profile", model);
        return utils.tpl("mypage/profile");
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
            return utils.tpl("mypage/profile");
        }

        // 세션에 저장된 사용자 정보에 프로필 이미지 경로 저장
        session.setAttribute("profileImage", properties.getUrl() + fileName);

        return "redirect:" + utils.redirectUrl("/mypage/myhome");
    }

    // 마이 페이지 -> 찜한 목록 보기 버튼 클릭 시 찜 목록 페이지로 이동
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
        }

        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("pageName", mode);
    }
}