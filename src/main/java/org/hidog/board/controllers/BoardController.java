package org.hidog.board.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.exceptions.BoardNotFoundException;
import org.hidog.board.exceptions.GuestPasswordCheckException;
import org.hidog.board.services.*;
import org.hidog.board.validators.BoardValidator;
import org.hidog.file.constants.FileStatus;
import org.hidog.file.entities.FileInfo;
import org.hidog.file.services.FileInfoService;
import org.hidog.global.ListData;
import org.hidog.global.Utils;
import org.hidog.global.exceptions.ExceptionProcessor;
import org.hidog.global.exceptions.UnAuthorizedException;
import org.hidog.global.services.ApiConfigService;
import org.hidog.member.MemberUtil;
import org.hidog.wishlist.servies.WishListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@SessionAttributes({"boardData","board"}) // 수정이후에는 세션 비우기?
public class BoardController implements ExceptionProcessor {

    private final BoardConfigInfoService configInfoService;
    private final BoardInfoService boardInfoService;
    private final BoardDeleteService boardDeleteService;
    private final BoardSaveService boardSaveService;
    private final FileInfoService fileInfoService;
    private final WishListService wishListService;

    private final ApiConfigService apiConfigService;
    private final BoardViewCountService viewCountService;
    private final BoardAuthService authService;

    private final BoardValidator boardValidator;
    private final MemberUtil memberUtil;
    private final Utils utils;

    private Board board; // 게시판 설정
    private BoardData boardData; // 게시글

    @ModelAttribute("mainClass")
    public String mainClass(){
        return "board-main layout-width";
    }

    // 티맵 api 키 조회
    @ModelAttribute("tmapJavascriptKey")
    public String tmapJavascriptKey() {
        return apiConfigService.get("tmapJavascriptKey");
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

        form.setGuest(!memberUtil.isLogin());
        if (memberUtil.isLogin()) {
            form.setPoster(memberUtil.getMember().getUserName());
        }

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

        RequestBoard form = boardInfoService.getForm(boardData); // 쿼리를 2번하지 않고 바로 쓰기 위해서 seq말고 boardData 사용함
        model.addAttribute("requestBoard", form);

        return utils.tpl("board/update");
    }

    /**
     * 게시글 등록, 수정
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public String save(@Valid RequestBoard form, Errors errors, Model model, SessionStatus status, HttpSession session) {
        String mode = form.getMode();
        mode = mode != null && StringUtils.hasText(mode.trim()) ? mode.trim() : "write";
        commonProcess(form.getBid(), mode, model);

        boolean isGuest = (mode.equals("write") && !memberUtil.isLogin());
        if(mode.equals("update")) {
            BoardData data = (BoardData)model.getAttribute("boardData");
            isGuest = data.getMember() == null;
        }
        
        form.setGuest(isGuest);

        boardValidator.validate(form, errors);

        if (errors.hasErrors()) {
            // 업로드 된 파일 목록 - location : editor, attach
            String gid = form.getGid();
            List<FileInfo> editorImages = fileInfoService.getList(gid, "editor", FileStatus.ALL);
            List<FileInfo> attachFiles = fileInfoService.getList(gid, "attach", FileStatus.ALL);
            form.setEditorImages(editorImages);
            form.setAttachFiles(attachFiles);

            return utils.tpl("board/" + mode);
        }

        // 게시글 저장 처리
        BoardData boardData = boardSaveService.save(form);

        status.setComplete();
        session.removeAttribute("boardData");

        // 목록 또는 상세 보기 이동
        String url = board.getLocationAfterWriting().equals("list") ? "/board/list/" + board.getBid() : "/board/view/" + boardData.getSeq();

        return "redirect:" + utils.redirectUrl(url);
    }

    /**
     * 게시글 목록
     *
     * @param bid : 게시판 아이디
     * @param model
     * @return
     */
    @GetMapping("/list/{bid}")
    public String list(@PathVariable("bid") String bid, @ModelAttribute BoardDataSearch search, Model model) {
        commonProcess(bid, "list", model);

        ListData<BoardData> data = boardInfoService.getList(bid, search);

        model.addAttribute("items", data.getItems());
        model.addAttribute("pagination", data.getPagination());
        if (memberUtil.isLogin()) {
            model.addAttribute("memberSeq", memberUtil.getMember().getSeq().toString());
        }
        return utils.tpl("board/list");
    }

    /**
     * 게시글 1개 보기
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/view/{seq}")
    public String view(@PathVariable("seq") Long seq, @ModelAttribute BoardDataSearch search, Model model, HttpSession session) throws JsonProcessingException {
        commonProcess(seq, "view", model);

        if (board.isShowListBelowView()) { // 게시글 하단에 목록 보여주기
            ListData<BoardData> data = boardInfoService.getList(board.getBid(), search);
            model.addAttribute("items", data.getItems());
            model.addAttribute("pagination", data.getPagination());
        }


        // 댓글 커맨드 객체 처리 S
        RequestComment requestComment = new RequestComment();
        if (memberUtil.isLogin()) {
            requestComment.setCommenter(memberUtil.getMember().getUserName());
        }

        model.addAttribute("requestComment", requestComment);
        // 댓글 커맨드 객체 처리 E

        viewCountService.update(seq); // 조회수 증가

        model.addAttribute("wishCount", wishListService.getWishCount(seq)); //좋아요 갯수
        String skin = board.getSkin(); // 스킨

        if (skin.equals("walking")) {
            Map<String, Object> markerPoint = boardInfoService.getMarkerPoint(seq);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(markerPoint);
            System.out.println("markerPoint:" + jsonString);

            model.addAttribute("markerPoint", jsonString);
        }

        return utils.tpl("board/view");
    }

    // walking/map 에서 사용할 댓글
    @GetMapping("/comment/{seq}")
    public String popupComment(@PathVariable("seq") Long seq, @ModelAttribute BoardDataSearch search, Model model, HttpSession session){
        commonProcess(seq, "popup_comment", model);

        // 댓글 커맨드 객체 처리 S
        RequestComment requestComment = new RequestComment();
        if (memberUtil.isLogin()) {
            requestComment.setCommenter(memberUtil.getMember().getUserName());
        }

        model.addAttribute("requestComment", requestComment);
        // 댓글 커맨드 객체 처리 E


        return utils.tpl("board/popup_content");
    }

    /**
     * 게시글 삭제
     *
     * @param seq : 게시글 번호
     * @param model
     * @return
     */
    @GetMapping("/delete/{seq}")
    public String delete(@PathVariable("seq") Long seq, Model model) {
        commonProcess(seq, "delete", model);

        boardDeleteService.delete(seq);

        return "redirect:" + utils.redirectUrl("/board/list/" + board.getBid());
    }

    /**
     * 비회원 비밀번호 검증
     *
     * @param password
     * @param model
     * @return
     */
    @PostMapping("/password")
    public String confirmGuestPassword(@RequestParam("password") String password, Model model) {

        authService.validate(password, boardData);

        String script = "parent.location.reload();";
        model.addAttribute("script", script);

        return "common/_execute_script";
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

        // 게시판 공통 JS
        addCommonScript.add("wish");

        // 게시판 공통 CSS
        addCss.add("board/style");

        // 스킨별 공통 CSS
        addCss.add("board/" + skin + "/style");

        if(skin.equals("default")){
            addCss.add("board/default/" + mode);
        }

        if(skin.equals("market")){
           addCss.add("board/market/" + mode);
        }

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
        } else if (mode.equals("view") || mode.equals("popup_comment")) { // 게시글 보기의 경우
            addScript.add("board/" + skin + "/view");

            if (mode.equals("popup_comment")) {
                addCss.add("style");
                addCss.add("walking/style");
                addScript.add("walking/content");
            }
        }

        if (skin.equals("walking")) {
            addCommonScript.add("map");
            addScript.add("board/walking/mapView");
        }

        if (skin.equals("gallery")) {
            addCommonScript.add("fileManager");
            addScript.add("board/gallery/form");
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
        model.addAttribute("mode", mode);

        //권한 체크
        if(List.of("write","list").contains(mode)) {
            authService.check(mode, board.getBid());
        }
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
    private void commonProcess(Long seq, String mode, Model model) {

        boardData = boardInfoService.get(seq);

        model.addAttribute("boardData", boardData);
        model.addAttribute("board", boardData.getBoard());
        commonProcess(boardData.getBoard().getBid(), mode, model);

        authService.check(mode, seq); // 권한 체크
    }

    @Override
    @ExceptionHandler(Exception.class)
    public ModelAndView errorHandler(Exception e, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        if (e instanceof UnAuthorizedException unAuthorizedException) {
            String message = unAuthorizedException.getMessage();
            message = unAuthorizedException.isErrorCode() ? utils.getMessage(message) : message;
            String script = String.format("alert('%s');history.back();", message);

            mv.setStatus(unAuthorizedException.getStatus());
            mv.setViewName("common/_execute_script");
            mv.addObject("script", script);

            return mv;
        } else if ( e instanceof GuestPasswordCheckException passwordCheckException) {
            mv.setStatus(passwordCheckException.getStatus());
            mv.addObject("board", board);
            mv.addObject("boardData", boardData);
            mv.setViewName(utils.tpl("board/password"));
            return mv;
        }
        e.printStackTrace();
        return ExceptionProcessor.super.errorHandler(e, request);
    }

    protected void orderProcess(Long seq, HttpSession session) {
        if(board.getSkin().equals("market")){
            session.setAttribute("boardData", boardData);
        }
    }
}