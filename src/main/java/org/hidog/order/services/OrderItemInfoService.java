package org.hidog.order.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.hidog.order.entities.OrderItem;
import org.hidog.order.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemInfoService {

    private final OrderItemRepository orderItemRepository;
    private final BoardDataRepository boardDataRepository;

    public List<OrderItem> get(Long bSeq){
        List<OrderItem> orderItem = orderItemRepository.findByBoardDataSeq(bSeq);
        return orderItem;
    }

    public List<OrderItem> getByMemberSeq(Long mSeq){
        List<BoardData> boardData = boardDataRepository.findByMemberSeq(mSeq);
        List<OrderItem> orderItem = orderItemRepository.findByBoardDataIn(boardData);
        return orderItem;
    }
}
