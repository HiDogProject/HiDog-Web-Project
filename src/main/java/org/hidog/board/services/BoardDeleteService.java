package org.hidog.board.services;

import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.repositories.BoardRepository;
=======
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.file.services.FileDeleteService;
>>>>>>> master
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardDeleteService {
<<<<<<< HEAD

    private final BoardDataRepository boardDataRepository;
    private final BoardInfoService boardInfoService;
=======
    private final BoardDataRepository boardDataRepository;
    private final BoardInfoService boardInfoService;
    private final FileDeleteService fileDeleteService;

>>>>>>> master

    /**
     * 게시글 삭제
     *
     * @param seq
     */
    public void delete(Long seq) {
<<<<<<< HEAD

        BoardData data = boardInfoService.get(seq); // 게시글 1개 조회

        String gid = data.getGid();

//        boardDataRepository.delete(seq);
    }

=======
         BoardData data = boardInfoService.get(seq);

         String gid = data.getGid();

        boardDataRepository.delete(data);
        boardDataRepository.flush();

        // 업로드된 파일 삭제
        fileDeleteService.delete(gid);
    }
>>>>>>> master
}
