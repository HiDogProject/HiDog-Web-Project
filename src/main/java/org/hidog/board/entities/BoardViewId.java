package org.hidog.board.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BoardViewId {
    private Long seq; //게시글번호
    private Integer uid;
}
