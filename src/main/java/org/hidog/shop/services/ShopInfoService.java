package org.hidog.shop.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.board.services.BoardConfigInfoService;
import org.hidog.global.ListData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopInfoService {

    private final BoardDataRepository boardDataRepository;
    private final BoardConfigInfoService boardConfigInfoService;

    public ListData<BoardData> getList(Long seq){
        List<String[]> boardList = boardConfigInfoService.getBoardList("market");
        if(boardList != null && boardList.size() > 0){
            for(String[] bid : boardList){
                //List<BoardData> boardData = boardDataRepository.findByBoardBidAndMemberSeq((Long)bid[0], seq);
            }

        }


        return null;
    }
}
