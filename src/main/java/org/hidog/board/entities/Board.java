package org.hidog.board.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @Column(length=30)
    private String bid; // 게시판 아이디
}
