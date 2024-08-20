package org.hidog.board.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.services.BoardInfoService;
import org.hidog.global.Utils;
import org.hidog.global.exceptions.ExceptionProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board/search")
@RequiredArgsConstructor
public class BoardSearchController implements ExceptionProcessor {

    private final Utils utils;
    private final BoardInfoService boardInfoService;

    @ModelAttribute("pageTitle")
    public String PageTitle() {
        return Utils.getMessage("게시글_통합검색", "commons");
    }

    @ModelAttribute("addCss")
    public String[] AddCss() {
        return new String[]{"/board/search/add.css"};
    }
}
