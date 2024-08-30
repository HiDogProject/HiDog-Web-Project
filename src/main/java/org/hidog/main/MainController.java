package org.hidog.main;

import lombok.RequiredArgsConstructor;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.global.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final BoardConfigInfoService configInfoService;
    private final Utils utils;

    @GetMapping
    public String Main(Model model) {
        List<String[]> boardList = configInfoService.getBoardList();

        model.addAttribute("boardList", boardList);

        /* 공통 헤더 & 푸터 이미지 */
        model.addAttribute("imageUrl", utils.url("/common/img/Hidog.png")); // 로고 이미지
        model.addAttribute("imageUrl2", utils.url("/common/img/main_dog.png")); // 메인 이미지
        model.addAttribute("imageUrl3", utils.url("/common/img/main_dog3.png")); // 메인 이미지2
        model.addAttribute("callUrl", utils.url("/common/img/call.gif")); // 푸터 전화기 이미지

        /* 스와이프 & 메인 배너 */
        model.addAttribute("addCommonCss", List.of("swiper-bundle.min"));
        model.addAttribute("addScript", List.of("main/banner"));
        model.addAttribute("addCommonScript", List.of("swiper-bundle.min"));

        /* 아이콘 이미지 */
        model.addAttribute("imageUrl_D1", utils.url("/common/img/walk.gif")); // 산책하개 이미지
        model.addAttribute("imageUrl_D2", utils.url("/common/img/sotong.gif")); // 소통하개 이미지
        model.addAttribute("imageUrl_D3", utils.url("/common/img/market.gif")); // 화개장터 이미지
        model.addAttribute("imageUrl_D4", utils.url("/common/img/jarang.gif")); // 자랑하개 이미지
        model.addAttribute("imageUrl_D5", utils.url("/common/img/adopt.gif")); // 국가동물보호 이미지
        model.addAttribute("imageUrl_D6", utils.url("/common/img/weather.gif")); // 네이버 날씨 이미지
        model.addAttribute("imageUrl_D7", utils.url("/common/img/contest.gif")); // 이달개 이미지

        /* 드롭 다운 메뉴 바 */
        model.addAttribute("addCss", List.of("main/style"));
        model.addAttribute("addScript", List.of("main/dropdown"));

        /* 서브 배너 이미지 */
        model.addAttribute("subUrl1", utils.url("/common/img/Jb1.png"));
        model.addAttribute("subUrl2", utils.url("/common/img/Jb2.jpg"));

        /* 공지사항 이미지 */
        model.addAttribute("bulbUrl", utils.url("/common/img/bulb.gif"));

        return "front/main/index";
    }
}