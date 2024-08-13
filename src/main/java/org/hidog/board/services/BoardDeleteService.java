package org.hidog.board.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.repositories.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardDeleteService {

    private final BoardDataRepository boardDataRepository;
    private final BoardInfoService boardInfoService;

    /**
     * 게시글 삭제
     *
     * @param seq
     */
    public void delete(Long seq) {

        BoardData data = boardInfoService.get(seq); // 게시글 1개 조회

        String gid = data.getGid();

//        boardDataRepository.delete(seq);
    }

}
