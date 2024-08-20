package org.hidog.market.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.exceptions.BoardNotFoundException;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.board.services.BoardInfoService;
import org.hidog.global.Utils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private final BoardConfigInfoService configInfoService;
    private final Utils utils;
    private final BoardInfoService boardInfoService;

    private Board board;
    private BoardData boardData;

    /**
     * 마켓 페이지
     * @return
     */
    @GetMapping()
    public String index(){
        return utils.tpl("market/index");
    }

    /**
     * 상품등록 페이지
     */
    @GetMapping("/write/{bid}")
    public String write(@PathVariable("bid") String bid, @ModelAttribute RequestProduct form, Model model){
        commonProcess(bid, "write", model);
        return utils.tpl("market/product/write");
    }

    /**
     * 상품수정 페이지
     */
    @GetMapping("/edit")
    public String edit(){
        return utils.tpl("market/product/edit");
    }

    /**
     * 상품 저장,수정 처리
     * @return
     */
    @PostMapping("/save")
    public String save(){
        return utils.redirectUrl("market/index");
    }

    /**
     * 상품 삭제 처리
     * @return
     */
    @DeleteMapping("/delete")
    public String delete(){
        return utils.redirectUrl("market/index");
    }


    /**
     * 게시판 설정이 필요한 공통 처리(모든 처리)
     * 게시판의 공통 처리 - 글목록, 글쓰기 등 게시판 ID가 있는 경우
     *
     * @param bid : 게시판 ID
     * @param mode
     * @param model
     */
    protected void commonProcess(String bid, String mode, Model model) {
        board = configInfoService.get(bid).orElseThrow(BoardNotFoundException::new); // 게시판 설정

        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        String pageTitle = board.getBName(); // 게시판명 - title 태그 제목

        mode = mode == null || !StringUtils.hasText(mode.trim()) ? "write" : mode.trim();

        String skin = board.getSkin(); // 스킨

        // 게시판 공통 CSS
        addCss.add("board/style");

        // 스킨별 공통 CSS
        addCss.add("board/" + skin + "/style");

        if (mode.equals("write") || mode.equals("update")) {
            // 글쓰기, 수정
            // 파일 업로드, 에디터 - 공통
            // form.js
            // 파일 첨부, 에디터 이미지 첨부를 사용하는 경우
            if (board.isUseUploadFile() || board.isUseUploadImage()) {
                addCommonScript.add("fileManager");
            }

            // 에디터 사용의 경우
            if (board.isUseEditor()) {
                addCommonScript.add("ckeditor5/ckeditor");
            }

            addScript.add("board/" + skin + "/form");
        }

        // 게시글 제목으로 title을 표시 하는 경우
        if (List.of("view", "update", "delete").contains(mode)) {
            pageTitle = boardData.getSubject();
        }

        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("board", board); // 게시판 설정
        model.addAttribute("pageTitle", pageTitle);
    }

    /**
     * 게시판 공통 처리 : 게시글 보기, 게시글 수정 - 게시글 번호가 있는 경우
     *      - 게시글 조회 -> 게시판 설정
     *
     * 게시글 번호가 경로 변수로 들어오는 공통 처리
     *  게시판 설정 + 게시글 내용
     *
     * @param seq : 게시글 번호
     * @param mode
     * @param model
     */
    protected void commonProcess(Long seq, String mode, Model model) {
        // 게시글 조회(엔티티)
        boardData = boardInfoService.get(seq);

        model.addAttribute("boardData", boardData);

        commonProcess(boardData.getBoard().getBid(), mode, model);
    }

}
