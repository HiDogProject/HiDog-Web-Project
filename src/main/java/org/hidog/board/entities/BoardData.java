package org.hidog.board.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hidog.global.entities.BaseEntity;
import org.hidog.member.entities.Member;

@Data
@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
public class BoardData extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    private String gid;

    @JoinColumn(name="bid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String guestPw; // 비화원 비밀번호(수정, 삭제)

    private String category; //게시글 분류

    private String poster; //작성자

    private String subject; //제목

    private String content; //내용

    private int viewCount; //조회수
    private boolean editorView; // 에디터를 사용해서 글 작성했는지 여부

}
