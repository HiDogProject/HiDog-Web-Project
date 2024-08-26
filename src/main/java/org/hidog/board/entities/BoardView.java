package org.hidog.board.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(BoardViewId.class)
public class BoardView {
    @Id
    private Long seq; // 게시글 번호

    @Id
    @Column(name="_uid")
    private Integer uid; // 비회원 - guestUid(), 회원 - 회원번호
}
