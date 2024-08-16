package org.hidog.board.services;

import org.hidog.board.entities.Board;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class BoardConfigInfoServiceTest {

    @Autowired
    private BoardConfigInfoService boardConfigInfoService;

    @Test
    void boardConfigTest() {
        Optional<Board> board = boardConfigInfoService.get("소통게시판(수정)");
        System.out.println(board);
    }
}
