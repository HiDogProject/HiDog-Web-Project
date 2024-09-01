package org.hidog.order.repository;

import org.hidog.board.entities.BoardData;
import org.hidog.order.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, QuerydslPredicateExecutor<OrderItem> {

    List<OrderItem> findByBoardDataSeq(Long boardDataSeq);
    List<OrderItem> findByBoardDataIn(List<BoardData> boardData);
}
