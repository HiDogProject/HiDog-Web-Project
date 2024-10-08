package org.hidog.board.services.comment;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.CommentData;
import org.hidog.board.repositories.CommentDataRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentDeleteService {
    private final CommentDataRepository commentDataRepository;
    private final CommentInfoService commentInfoService;

    public Long delete(Long seq) {

        CommentData data = commentInfoService.get(seq);
        Long boardDataSeq = data.getBoardData().getSeq();

        commentDataRepository.delete(data);
        commentDataRepository.flush();

        commentInfoService.updateCommentCount(boardDataSeq);

        return boardDataSeq;
    }
}
