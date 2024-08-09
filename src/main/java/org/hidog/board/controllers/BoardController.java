package org.hidog.board.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private Board board;

    /**
     * 게시판 목록
     * @param bid : 게시판 아이디
     * @param model
     * @return
     */
    @GetMapping("/list/{bid}")
    public String list() {


        return "front/board/list";
    }

    /**
     * 게시글 보기
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view() {

        return "front/board/view";
    }

    /**
     * 게시글 작성
     *
     * @param bid : 게시판 아이디
     * @param model
     * @return
     */
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid,
                        @ModelAttribute RequestBoard form, Model model) {

        return "front/board/write";
    }

    /**
     * 게시글 수정
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/update/{seq}")
    public String update() {

        return "front/board/update";
    }

    /**
     * 게시글 등록, 수정
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save() {

        return "front/board/list";
    }


    /**
     * 게시글 삭제
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/delete/{seq}")
    public String delete() {

        return "redirect:/front//board/list/" + board.getBid();
    }

    /**
     * 보드 관련 컨트롤러 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode, ""); // mode가 null이면 ""을 기본값

        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("board/style");  // 보드 공통 스타일
        /*
        if (mode.equals("join")) {
            addCommonScript.add("fileManager");
            addCss.add("member/join");
            addScript.add("member/join");

        } else if (mode.equals("login")) {
            addCss.add("member/login");
        }
         */

        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }
}
