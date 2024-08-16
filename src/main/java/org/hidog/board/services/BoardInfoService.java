package org.hidog.board.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hidog.board.controllers.BoardDataSearch;
import org.hidog.board.controllers.RequestBoard;
import org.hidog.board.entities.BoardData;
import org.hidog.board.entities.QBoardData;
import org.hidog.board.exceptions.BoardDataNotFoundException;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.config.services.ConfigInfoService;
import org.hidog.global.ListData;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardInfoService {
    private final EntityManager em;
    private final BoardDataRepository boardDataRepository;
    private final ConfigInfoService configInfoService;

    private final HttpServletRequest request;

    private final MemberUtil memberUtil;

    /**
     * 게시글 1개 조회(엔티티)
     *
     * @param seq : 게시글 번호
     * @return
     */
    public BoardData get(Long seq) {
        BoardData boardData = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        addBoardData(boardData);

        return boardData;
    }

    /**
     * BoardData(엔티티) -> RequestBoard(커맨드객체)
     * @param data : 게시글 데이터(BoardData), 게시글 번호(Long)
     * @return
     */
    public RequestBoard getForm(Object data) {
        BoardData boardData = null;
        if (data instanceof BoardData) {
            boardData = (BoardData) data;
        } else {
            Long seq = (Long) data;
            boardData = get(seq);
        }

        RequestBoard form = new ModelMapper().map(boardData, RequestBoard.class);
        form.setMode("update");
        form.setBid(boardData.getBoard().getBid());

        return form;
    }

    /**
     * 게시글 추가 정보 처리
     *
     * @param boardData
     */
    public void addBoardData(BoardData boardData) {
        /* 수정, 삭제 권한 정보 처리 S */
        boolean editable = false, deletable = false, mine = false;
        Member _member = boardData.getMember(); // null - 비회원, X null -> 회원

        // 관리자 -> 삭제, 수정 모두 가능
        if (memberUtil.isAdmin()) {
            editable = true;
            deletable = true;
        }

        // 회원 -> 직접 작성한 게시글만 삭제, 수정 가능
        Member member = memberUtil.getMember();
        if (_member != null && memberUtil.isLogin() && _member.getEmail().equals(member.getEmail())) {
            editable = true;
            deletable = true;
            mine = true;
        }

        // 비회원 -> 비회원 비밀번호가 확인 된 경우 삭제, 수정 가능
        // 비회원 비밀번호 인증 여부 세션에 있는 guest_confirmed_게시글번호 true -> 인증
        HttpSession session = request.getSession();
        String key = "guest_confirmed_" + boardData.getSeq();
        Boolean guestConfirmed = (Boolean)session.getAttribute(key);
        if (_member == null && guestConfirmed != null && guestConfirmed) {
            editable = true;
            deletable = true;
            mine = true;
        }

        boardData.setEditable(editable);
        boardData.setDeletable(deletable);
        boardData.setMine(mine);

        // 수정 버튼 노출 여부
        // 관리자 - 노출, 회원 게시글 - 직접 작성한 게시글, 비회원
        boolean showEditButton = memberUtil.isAdmin() || mine || _member == null;
        boolean showDeleteButton = showEditButton;

        boardData.setShowEditButton(showEditButton);
        boardData.setShowDeleteButton(showDeleteButton);

        /* 수정, 삭제 권한 정보 처리 E */
    }

    /**
     * 게시글 목록 조회
     *
     */
    public ListData<BoardData> getList(BoardDataSearch search) {
          int page =  Math.max(search.getPage(), 1);
          int limit = search.getLimit();

          String sopt = search.getSort(); // 검색옵션
          String skey = search.getSkey(); // 검색 키워드

          String bid = search.getBid();
          List<String> bids = search.getBids(); // 게시판 여러개 조회

        /* 검색 처리 S*/
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();


        if (bid != null && StringUtils.hasText(bid.trim())) { // 게시판별 조회
            andBuilder.and(boardData.board.bid.eq(bid.trim()));

        } else if (bid != null && !bids.isEmpty()) { // 게시판 여러개 조회
            andBuilder.and(boardData.board.bid.in(bids));
        }

        /**
         * 조건 검색 처리
         *
         * sopt - ALL : 통합검색(제목 + 내용 + 글작성자(작성자, 회원명))
         *       SUBJECT : 제목검색
         *       CONTENT : 내용검색
         *       SUBJECT_CONTENT: 제목 + 내용 검색
         *       NAME : 이름(작성자, 회원명)
         */
        sopt = sopt != null && StringUtils.hasText(sopt.trim()) ? sopt.trim() : "ALL";
        if (skey != null && StringUtils.hasText(skey.trim())) {

        }


        /* 검색 처리 E */

      return null;
    }

    /**
     * 특정 게시판 목록을 조회
     *
     * @param bid : 게시판 ID
     * @param search
     * @return
     */
    public ListData<BoardData> getList(String bid, BoardDataSearch search) {
        search.setBid(bid);

        return getList(search);
    }
}
