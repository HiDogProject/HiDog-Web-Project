package org.hidog.board.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hidog.board.advices.BoardControllerAdvice;
import org.hidog.board.controllers.BoardDataSearch;
import org.hidog.board.controllers.RequestBoard;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.entities.CommentData;
import org.hidog.board.entities.QBoardData;
import org.hidog.board.exceptions.BoardDataNotFoundException;
import org.hidog.board.exceptions.BoardNotFoundException;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.services.comment.CommentInfoService;
import org.hidog.file.entities.FileInfo;
import org.hidog.file.services.FileInfoService;
import org.hidog.global.CommonSearch;
import org.hidog.global.ListData;
import org.hidog.global.Pagination;
import org.hidog.global.Utils;
import org.hidog.global.constants.DeleteStatus;
import org.hidog.member.MemberUtil;
import org.hidog.member.constants.Authority;
import org.hidog.member.entities.Member;
import org.hidog.wishlist.constants.WishType;
import org.hidog.wishlist.servies.WishListService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardInfoService {

    private final JPAQueryFactory jpaQueryFactory;
    private final BoardDataRepository boardDataRepository;
    private final BoardConfigInfoService configInfoService;
    private final HttpServletRequest request;
    private final MemberUtil memberUtil;
    private final Utils utils;
    private final ModelMapper modelMapper;
    private final FileInfoService fileInfoService;
    private final WishListService wishListService;
    private final BoardControllerAdvice board;
    private final CommentInfoService commentInfoService;


    /**
     * 게시글 목록 조회
     *
     */
    public ListData<BoardData> getList(BoardDataSearch search, DeleteStatus status) {

        String bid = search.getBid();
        List<String> bids = search.getBids(); // 게시판 여러개 조회

        // 게시판 설정 조회
        Board board = bid != null && StringUtils.hasText(bid.trim()) ? configInfoService.get(bid.trim()).orElseThrow(BoardNotFoundException::new) : new Board();

        int page =  Math.max(search.getPage(), 1); // search.getPage(), 1 을 비교해서 더 큰값을 반환
        int limit = search.getLimit();
        limit = limit > 0 ? limit : board.getRowsPerPage();

        int offset = (page - 1) * limit;

        // 삭제가 되지 않은 게시글 목록이 기본 값
        status = Objects.requireNonNullElse(status, DeleteStatus.UNDELETED);

        String sopt = search.getSort(); // 검색 옵션
        String skey = search.getSkey(); // 검색 키워드

        List<String> categories = search.getCategory();
        Boolean notice = search.getNotice();


        /* 검색 처리 S*/
        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 삭제, 미삭제 게시글 조회 처리
        if (status != DeleteStatus.ALL) {
            if (status == DeleteStatus.UNDELETED) {
                andBuilder.and(boardData.deletedAt.isNull()); // 미삭제 게시글
            } else {
                andBuilder.and(boardData.deletedAt.isNotNull()); // 삭제된 게시글
            }
        }


        if (bid != null && StringUtils.hasText(bid.trim())) { // 게시판별 조회
            bids = List.of(bid);
        }

        if (bids != null && !bids.isEmpty()){ // 게시판 여러개 조회
            andBuilder.and(boardData.board.bid.in(bids));
        }


        //분류 검색 처리
        if (categories != null && !categories.isEmpty()){
            andBuilder.and(boardData.category.in(categories));
        }

        //공지글 검색
        if(notice != null){
            andBuilder.and(boardData.notice.eq(notice));
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
            skey = skey.trim();
            BooleanExpression condition = null;

            BooleanBuilder orBuilder = new BooleanBuilder();

            /* 이름 검색 S */
            BooleanBuilder nameCondition = new BooleanBuilder();
            nameCondition.or(boardData.poster.contains(skey));
            if (boardData.member != null) {
                nameCondition.or(boardData.member.userName.contains(skey));
            }
            /* 이름 검색 E */

            if (sopt.equals("ALL")) { // 통합 검색
                orBuilder.or(boardData.subject.concat(boardData.content)
                                .contains(skey))
                        .or(nameCondition);



            } else if (sopt.equals("SUBJECT")) { // 제목 검색
                condition = boardData.subject.contains(skey);
            } else if (sopt.equals("CONTENT")) { // 내용 검색
                condition = boardData.content.contains(skey);
            } else if (sopt.contains("SUBJECT_CONTENT")) { // 제목 + 내용 검색
                condition = boardData.subject.concat(boardData.content)
                        .contains(skey);
            } else if (sopt.equals("NAME")) {
                andBuilder.and(nameCondition);
            }

            if (condition != null) andBuilder.and(condition);
            andBuilder.and(orBuilder);
        }

        /* 검색 처리 E */

        /* 정렬 처리 S */
        String sort = search.getSort();

        PathBuilder<BoardData> pathBuilder = new PathBuilder<>(BoardData.class, "boardData");
        OrderSpecifier orderSpecifier = null;
        Order order = Order.DESC;
        if (sort != null && StringUtils.hasText(sort.trim())) {
            // 정렬항목_방향   예) viewCount_DESC -> 조회수가 많은 순으로 정렬
            String[] _sort = sort.split("_");
            if (_sort[1].toUpperCase().equals("ASC")) {
                order = Order.ASC;
            }

            orderSpecifier = new OrderSpecifier(order, pathBuilder.get(_sort[0]));
        }

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(boardData.notice.desc()); // 공지사항이 가장 먼저 나오기
        if (orderSpecifier != null) {
            orderSpecifiers.add(orderSpecifier);
        }
        orderSpecifiers.add(boardData.createdAt.desc());
        /* 정렬 처리 E */

        /* 목록 조회 처리 S */
        List<BoardData> items = jpaQueryFactory
                .selectFrom(boardData)
                .leftJoin(boardData.board)
                .fetchJoin()
                .leftJoin(boardData.member)
                .fetchJoin()
                .where(andBuilder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(offset)
                .limit(limit)
                .fetch();

        // 추가 정보 처리
        items.forEach(this::addInfo);

        /* 목록 조회 처리 E */

        // 전체 게시글 갯수
        long total = boardDataRepository.count(andBuilder);

        // 페이징 처리
        int ranges = utils.isMobile() ? board.getPageCountMobile() : board.getPageCountPc();
        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 게시판 별 목록
     * 특정 게시판의 게시글목록을 조회
     *
     * @param bid : 게시판 ID
     * @param search
     * @return
     */
    public ListData<BoardData> getList(String bid, BoardDataSearch search, DeleteStatus status) {
        search.setBid(bid);

        return getList(search, status);
    }

    public ListData<BoardData> getList(String bid, BoardDataSearch search) {
        return getList(bid, search, DeleteStatus.UNDELETED);
    }

    /**
     * 게시글 1개 조회(엔티티)
     *
     * @param seq : 게시글 번호
     * @return
     */
    public BoardData get(Long seq, DeleteStatus status) {

        BooleanBuilder andBuilder = new BooleanBuilder();
        QBoardData boardData = QBoardData.boardData;
        andBuilder.and(boardData.seq.eq(seq));

        // 삭제, 미삭제 게시글 조회 처리
        if (status != DeleteStatus.ALL) {
            if (status == DeleteStatus.UNDELETED) {
                andBuilder.and(boardData.deletedAt.isNull()); // 미삭된 게시글
            } else {
                andBuilder.and(boardData.deletedAt.isNotNull()); // 삭제된 게시글
            }
        }

        BoardData item = jpaQueryFactory.selectFrom(boardData)
                .leftJoin(boardData.board)
                .fetchJoin()
                .leftJoin(boardData.member)
                .fetchJoin()
                .where(andBuilder)
                .fetchFirst();

        if (item == null) {
            throw new BoardDataNotFoundException();
        }

        // 추가 데이터 처리
        addInfo(item);

        // 댓글 목록
        List<CommentData> comments = commentInfoService.getList(seq);
        item.setComments(comments);

        return item;
    }

    public BoardData get(Long seq) {
        return get(seq, DeleteStatus.UNDELETED);
    }

    /**
     * BoardData(엔티티) -> RequestBoard(커맨드객체)
     * 게시글 데이터(BoardData), 게시글 번호(Long)
     *
     * @return
     */
    public RequestBoard getForm(Long seq, DeleteStatus status) {
        BoardData item = get(seq, status);

        return getForm(item);
    }

    public RequestBoard getForm(BoardData item) {

        RequestBoard form = modelMapper.map(item, RequestBoard.class);
        form.setBid(item.getBoard().getBid());

        form.setGuest(item.getMember() == null);

        return form;
    }

    public RequestBoard getForm(Long seq) {
        return getForm(seq, DeleteStatus.UNDELETED);
    }

    /**
     * 특정회원의 특정 게시판 조회
     * @param seq : 회원 번호
     * @param skin : 게시판 스킨
     * @param search
     * @return
     */
    public ListData<BoardData> getMarketList(Long seq, String skin,CommonSearch search) {
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;


        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.member.seq.eq(seq));
        andBuilder.and(boardData.board.skin.eq(skin));

        List<BoardData> items = jpaQueryFactory.selectFrom(boardData)
                .where(andBuilder)
                .leftJoin(boardData.board)
                .fetchJoin()
                .offset(offset)
                .limit(limit)
                .orderBy(boardData.createdAt.desc())
                .fetch();

        long total = boardDataRepository.count(andBuilder);
        int ranges = utils.isMobile() ? 5 : 10;
        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }






    /**
     * 내가 작성한 게시글 목록
     *
     */
    public ListData<BoardData> getMyList(CommonSearch search) {
        if (!memberUtil.isLogin()) {
            return new ListData<>();
        }

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = (page - 1) * limit;


        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.member.seq.eq(memberUtil.getMember().getSeq()));

        List<BoardData> items = jpaQueryFactory.selectFrom(boardData)
                .where(andBuilder)
                .leftJoin(boardData.board)
                .fetchJoin()
                .offset(offset)
                .limit(limit)
                .orderBy(boardData.createdAt.desc())
                .fetch();

        long total = boardDataRepository.count(andBuilder);
        int ranges = utils.isMobile() ? 5 : 10;
        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }


    /**
     *  내가 찜한 게시글 목록 조회
     *
     * @param search
     * @return
     */
    public ListData<BoardData> getWishList(CommonSearch search) {

        // CommonSearch : 페이징
        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();
        limit = limit < 1 ? 10 : limit;
        int offset = limit * (page - 1); // jpa쿼리 팩토리 사용 위해 작성한 코드

        List<Long> seqs = wishListService.getList(WishType.BOARD);
        if(seqs == null || seqs.isEmpty()) {
            return new ListData<>();
        }

        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();
        andBuilder.and(boardData.seq.in(seqs)); // 검색조건 추가

        List<BoardData> items = jpaQueryFactory.selectFrom(boardData)
                .where(andBuilder)
                .leftJoin(boardData.board)
                .fetchJoin()
                .offset(offset)
                .limit(limit)
                .orderBy(boardData.createdAt.desc())
                .fetch();

        items.forEach(this::addInfo);

        long total = boardDataRepository.count(andBuilder);

        int ranges = utils.isMobile() ? 5 : 10;
        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 게시글 추가 정보 처리, 추가 데이터 처리
     *          - 업로드한 파일 목록
     *              에디터 이미지 목록, 첨부 파일 이미지 목록
     *          - 권한 : 글쓰기, 글수정, 글 삭제, 글 조회 가능 여부
     *          - 댓글
     * @param item
     */
    public void addInfo(BoardData item) {

        /* 업로드한 파일 목록 S */
       String gid = item.getGid();
       List<FileInfo> editorImages = fileInfoService.getList(gid, "editor");
       List<FileInfo> attachFiles = fileInfoService.getList(gid, "attach");

       item.setEditorImages(editorImages);
       item.setAttachFiles(attachFiles);
       /* 업로드한 파일 목록 E */

        /* 게시글 권한 정보 처리 S */
        boolean editable = false, commentable = false, mine = false;

        // 관리자는 모든 권한 가능
        if (memberUtil.isAdmin()) {
            editable = commentable = true;
        }

        // 회원 - 직접 작성한 게시글인 경우만 수정,삭제(editable)
        Member boardMember = item.getMember(); // 게시글을 작성한 회원
        Member loggedMember = memberUtil.getMember(); // 로그인한 회원
        if (boardMember != null && memberUtil.isLogin() && boardMember.getEmail().equals(loggedMember.getEmail())) {
            editable = true; // 수정, 삭제 가능
            mine = true; // 게시글 소유자
        }

        // 비회원 - 비회원 비밀번호를 검증한 경우 - 게시글 소유자, 수정, 삭제 가능
        // 비회원이 비밀번호를 검증한 경우 세션 키 : confirmed_board_data_게시글번호, 값 true
        HttpSession session = request.getSession();
        Boolean guestConfirmed = (Boolean)session.getAttribute("confirm_board_data_" + item.getSeq());
        if (boardMember == null && guestConfirmed != null && guestConfirmed) { // 비회원 비밀번호가 인증된 경우
            editable = true;
            mine = true;
        }

        // 댓글 작성 가능 여부 - 전체 : 모두 가능(비회원 + 회원 + 관리자), 회원 + 관리자 , 관리자
        Board board = item.getBoard();
        Authority authority = board.getCommentAccessType();
        if (authority == Authority.ALL || memberUtil.isAdmin()) {
            commentable = true;
        }

        if (authority == Authority.USER && memberUtil.isLogin()) {
            commentable = true;
        }

        item.setEditable(editable);
        item.setCommentable(commentable);
        item.setMine(mine);

        /* 게시글 권한 정보 처리 E */

        // 게시글 버튼 노출 권한 처리 S
        boolean showEdit = false, showList= false, showDelete = false;

        Authority editAuthority = board.getWriteAccessType(); // 글작성, 수정 권한
        Authority listAuthority = board.getListAccessType(); // 글목록 보기 권한


        if (editAuthority == Authority.ALL || boardMember == null ||
                (editAuthority == Authority.USER && memberUtil.isLogin())) { // 수정 삭제 권한이 ALL인 경우, 비회원인 경우, 회원만 가능한 경우 + 로그인한 경우 수정, 삭제 버튼 클릭시 비회원 검증 하므로 노출
            showEdit = showDelete = true;
        }

        if (listAuthority == Authority.ALL || (listAuthority == Authority.USER && memberUtil.isLogin())) {
            showList = true;
        }

        if (memberUtil.isAdmin()) { // 관리자는 모든 권한 가능
            showEdit = showDelete = showList = true;
        }

        item.setShowEdit(showEdit);
        item.setShowDelete(showDelete);
        item.setShowList(showList);
        // 게시글 버튼 노출 권한 처리 E
    }

    public Map<String, Object> getMarkerPoint(Long seq) {
        Optional<BoardData> boardData = boardDataRepository.findById(seq);
        String departurePoint = boardData.get().getLongText1();
        String viaPoints = boardData.get().getLongText2();

        Map<String, Object> data = new HashMap<>();

        data.put("departurePoint", departurePoint);
        data.put("viaPoints", viaPoints);

        return data;
    }
}
