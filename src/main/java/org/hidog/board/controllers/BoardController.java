package org.hidog.board.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.hidog.board.services.BoardSaveService;
import org.hidog.board.validators.BoardFormValidator;
import org.hidog.file.entities.FileInfo;
import org.hidog.file.services.FileInfoService;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Transactional
public class BoardController {

    private final BoardInfoService boardInfoService;
    private final BoardSaveService boardSaveService;
    private final MemberUtil memberUtil;
    private final BoardFormValidator boardFormValidator;
    private final FileInfoService fileInfoService;
    private final Utils utils;

    private Board board; // 게시판 설정
    private BoardData boardData; // 게시글


    /**
     * 게시판 목록
     * @param bid : 게시판 아이디
     * @param model
     * @return
     */
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, Model model) {
        commonProcess(bid, "list", model);



        return utils.tpl("board/list");
    }

    /**
     * 게시글 1개 보기
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "view", model);

        return utils.tpl("board/view");
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
        commonProcess(bid, "write", model);

        /*
        if (memberUtil.isLogin()) {
            Member member = memberUtil.getMember();
            form.setPoster(member.getUserName());
        }
         */

        return utils.tpl("board/write");
    }

    /**
     * 게시글 수정
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/update/{seq}")
    public String update(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "update", model);

        RequestBoard form = boardInfoService.getForm(boardData);
        System.out.println("form:" + form);
        model.addAttribute("requestBoard", form);


        return utils.tpl("board/write");
    }

    /**
     * 게시글 등록, 수정
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, Model model) {
        String bid = form.getBid();
        String mode = form.getMode();
        commonProcess(bid, mode, model);

        boardFormValidator.validate(form, errors);


        if (errors.hasErrors()) {
            String gid = form.getGid();

            List<FileInfo> editorFiles = fileInfoService.getList(gid, "editor");
            List<FileInfo> attachFiles = fileInfoService.getList(gid, "attach");

            form.setEditorFiles(editorFiles);
            form.setAttachFiles(attachFiles);

            return utils.tpl("board/" + mode);
        }


        // 게시글 저장 처리
        BoardData boardData = boardSaveService.save(form);


        return "redirect:" + utils.redirectUrl("/board/list/" + bid);
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

        //return "redirect://front/board/list/" + board.getBid();
        return "redirect:" + utils.redirectUrl("/board/list/" + board.getBid());
    }


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

        //addScript.add("board/common"); // 게시판 공통 스크립트


        /*
        // 게시판 설정 처리 S
        board = configInfoService.get(bid);

        // 접근 권한 체크
        //boardAuthService.accessCheck(mode, board);

        model.addAttribute("board", board);
        // 게시판 설정 처리 E
         */



        //String pageTitle = board.getBName(); // 게시판명이 기본 타이틀

        if (mode.equals("write") || mode.equals("update") || mode.equals("reply")) { // 쓰기 또는 수정
            /*
            if (board.isUseEditor()) { // 에디터 사용하는 경우
                addCommonScript.add("ckeditor5/ckeditor");
            }
             */


            // 이미지 또는 파일 첨부를 사용하는 경우

            //if (board.isUseUploadImage() || board.isUseUploadFile()) {
                addCommonScript.add("fileManager");
            //}



            //addScript.add("board/form");

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
