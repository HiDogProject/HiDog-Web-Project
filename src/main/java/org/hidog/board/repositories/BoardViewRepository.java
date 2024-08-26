package org.hidog.board.repositories;

import org.hidog.board.entities.Board;
import org.hidog.board.entities.BoardView;
import org.hidog.board.entities.BoardViewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface BoardViewRepository extends JpaRepository<BoardView, BoardViewId>, QuerydslPredicateExecutor<BoardView> {
}
