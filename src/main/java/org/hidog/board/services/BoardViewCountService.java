package org.hidog.board.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.entities.BoardView;
import org.hidog.board.entities.BoardViewId;
import org.hidog.board.entities.QBoardView;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.repositories.BoardViewRepository;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardViewCountService {

    private BoardViewRepository viewRepository;
    private BoardDataRepository dataRepository;
    private Utils utils;
    private MemberUtil memberUtil;

    public void update(Long seq){
        BoardData data = dataRepository.findById(seq).orElse(null);
        if(data == null){
            return;
        }

        int uid = memberUtil.isLogin() ? memberUtil.getMember().getSeq().intValue() : utils.guestUid();

        BoardView boardView = new BoardView(seq, uid);
        viewRepository.saveAndFlush(boardView);

        //전체 조회수
        QBoardView bv = QBoardView.boardView;
        long total = viewRepository.count(bv.seq.eq(seq));

        data.setViewCount((int) total);
        dataRepository.saveAndFlush(data);

    }
}
