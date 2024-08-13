package org.hidog.board.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hidog.board.controllers.RequestBoard;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.exceptions.BoardDataNotFoundException;
import org.hidog.board.exceptions.BoardNotFoundException;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.repositories.BoardRepository;
import org.hidog.file.services.FileUploadService;
import org.hidog.member.MemberUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional // 게시판 설정 // 엔티티 영속성상태를 계속 보장하기 위해 넣어줌
@RequiredArgsConstructor
public class BoardSaveService {

    //private final BoardAuthService boardAuthService;
    private final BoardRepository boardRepository;
    private final BoardDataRepository boardDataRepository;
    private final FileUploadService fileUploadService;
    private final MemberUtil memberUtil;
    private final HttpServletRequest request;

    public BoardData save(RequestBoard form) {
        // 반환값 BoardData : 게시글 작성 이후 이동하려면 게시글 정보가 필요함 (게시글번호 or 게시판아이디)

        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode.trim() : "write"; // requestBoard 에서 이미 write 기본값 했는데 왜 해? // 커맨드객체에서 값을 넘겨줄 때 빈값으로 넘겨줄 때도 있다...?

        Long seq = form.getSeq();
        String gid = form.getGid();
        BoardData data = null;

        /*
        // 수정 권한 체크
        if (mode.equals("update")) {
            boardAuthService.check(mode, seq);
        }
         */

        if (seq != null && mode.equals("update")) { // 글 수정
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);
        } else { // 글 작성
           String bid = form.getBid();
           Board board = boardRepository.findById(bid).orElseThrow(BoardNotFoundException::new);

           data= BoardData.builder()
                   .gid(gid)
                   .board(board)
                   .member(memberUtil.getMember())
                   .ip(request.getRemoteAddr())
                   .ua(request.getHeader("User-Agent"))
                   .build();
        }


        /* 글 작성, 글 수정 공통 S */

        data.setPoster(form.getPoster()); // 작성자
        data.setSubject(form.getSubject()); // 게시글 제목
        data.setContent(form.getContent()); // 게시글 내용
        data.setCategory(form.getCategory()); // 게시판 분류
        data.setEditorView(data.getBoard().isUseEditor());

        /* 글 작성, 글 수정 공통 E */

        boardDataRepository.saveAndFlush(data);

        // 파일 업로드 완료 처리
        //fileUploadService.processDone(data.getGid());

        return data;
    }
}
