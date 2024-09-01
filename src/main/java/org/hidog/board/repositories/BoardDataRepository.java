package org.hidog.board.repositories;

import org.hidog.board.entities.BoardData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;


public interface BoardDataRepository extends JpaRepository<BoardData, Long>, QuerydslPredicateExecutor<BoardData> {
    BoardData findByLongText1(String longText1);
    List<BoardData> findByLongText1IsNotNull();
    List<BoardData> findByMemberSeq(Long memberSeq);
}

