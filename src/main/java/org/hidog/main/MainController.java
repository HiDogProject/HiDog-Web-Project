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

        /* 스와이프 & 메인 배너 */
        model.addAttribute("addCommonCss", List.of("swiper-bundle.min"));
        model.addAttribute("addScript", List.of("main/banner"));
        model.addAttribute("addCommonScript", List.of("swiper-bundle.min"));

        /* 드롭 다운 메뉴 바 */
        model.addAttribute("addCss", List.of("main/style"));
        model.addAttribute("addScript", List.of("main/dropdown"));

        return "front/main/index";
    }
}