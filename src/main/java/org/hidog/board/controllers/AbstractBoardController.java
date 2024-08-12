package org.hidog.board.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractBoardController {

    protected BoardInfoService boardInfoService;

    protected Board board; // 게시판 설정
    protected BoardData boardData; // 게시글

    /**
     * 게시판의 공통 처리 - 글목록, 글쓰기 등 게시판 ID가 있는 경우
     *
     * @param bid : 게시판 ID
     * @param mode
     * @param model
     */
    protected void commonProcess(String bid, String mode, Model model) {

        mode = StringUtils.hasText(mode) ? mode : "list";

        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        List<String> addCommonCss = new ArrayList<>();
        List<String> addCss = new ArrayList<>();

        addScript.add("board/common"); // 게시판 공통 스크립트

        /*
        // 게시판 설정 처리 S
        board = configInfoService.get(bid);

        // 접근 권한 체크
        boardAuthService.accessCheck(mode, board);

        // 스킨별 css, js 추가
        String skin = board.getSkin();
        addCss.add("board/skin_" + skin);
        addScript.add("board/skin_" + skin);

        model.addAttribute("board", board);
        // 게시판 설정 처리 E
         */


        //String pageTitle = board.getBName(); // 게시판명이 기본 타이틀

        if (mode.equals("write") || mode.equals("update") || mode.equals("reply")) { // 쓰기 또는 수정
            if (board.isUseEditor()) { // 에디터 사용하는 경우
                addCommonScript.add("ckeditor5/ckeditor");
            }

            // 이미지 또는 파일 첨부를 사용하는 경우
            if (board.isUseUploadImage() || board.isUseUploadFile()) {
                addCommonScript.add("fileManager");
            }

            addScript.add("board/form");

            /*
            pageTitle += " ";
            pageTitle += mode.equals("update") ?  Utils.getMessage("글수정", "commons") :  Utils.getMessage("글쓰기", "commons");
             */

        } else if (mode.equals("view")) {
            // pageTitle - 글 제목 - 게시판 명
            //pageTitle = String.format("%s | %s", boardData.getSubject(), board.getBName());
            addScript.add("board/view");
        }


        model.addAttribute("addCommonCss", addCommonCss);
        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        //model.addAttribute("pageTitle", pageTitle);
    }

    /**
     * 게시판 공통 처리 : 게시글 보기, 게시글 수정 - 게시글 번호가 있는 경우
     *      - 게시글 조회 -> 게시판 설정
     *
     * @param seq : 게시글 번호
     * @param mode
     * @param model
     */
    protected void commonProcess(Long seq, String mode, Model model) {
        // 글수정, 글삭제 권한 체크
        //boardAuthService.check(mode, seq);

        // 게시글 조회(엔티티)
        boardData = boardInfoService.get(seq);

        String bid = boardData.getBoard().getBid();
        commonProcess(bid, mode, model);

        model.addAttribute("boardData", boardData);
    }
}
