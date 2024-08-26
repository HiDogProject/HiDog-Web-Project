package org.hidog.main;

import lombok.RequiredArgsConstructor;
import org.hidog.board.services.BoardConfigInfoService;
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

    @GetMapping
    public String Main(Model model) {

        List<String[]> boardList = configInfoService.getBoardList();

        model.addAttribute("addCss", List.of("main/style"));
        model.addAttribute("addScript", List.of( "main/dropdown"));

        return "front/main/index";
    }

//    @GetMapping("/main/index2")
//    public String boardList(Model model) {
//
//         List<String[]> boardList = configInfoService.getBoardList();
//
//        model.addAttribute("boardList", boardList);
//
//        model.addAttribute("addCss", List.of("main/style"));
//        model.addAttribute("addScript", List.of( "main/dropdown"));
//
//        model.addAttribute("addCommonCss", List.of("swiper-bundle.min"));
//        model.addAttribute("addScript", List.of( "main/banner"));
//        model.addAttribute("addCommonScript", List.of("swiper-bundle.min"));
//
//        return "front/main/index2";
//    }
}