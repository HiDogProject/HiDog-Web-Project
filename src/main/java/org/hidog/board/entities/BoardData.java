package org.hidog.board.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hidog.file.entities.FileInfo;
import org.hidog.global.entities.BaseEntity;
import org.hidog.member.entities.Member;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor @AllArgsConstructor
@Table(indexes = {
        @Index(name="idx_boardData_basic", columnList = "notice DESC, listOrder DESC, listOrder2 ASC, createdAt DESC")
})
public class BoardData extends BaseEntity { // extends BaseEntity : 날짜와 시간
    @Id @GeneratedValue
    private Long seq; // 게시글 번호

    @ManyToOne(fetch = FetchType.LAZY) // 게시판입장에서 게시글은 여러개 // many가 관계의 주인, 외래키도 있는 곳
    @JoinColumn(name="bid") // 게시판 별 게시글 구분
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY) // 권한설정 // 한명의 회원이 여러개의 게시글 작성 -> 게시글 : many, 회원 : one
    @JoinColumn(name="memberSeq")
    private Member member;

    @Column(length=65, nullable = false)
    private String gid = UUID.randomUUID().toString(); // 게시글하나에 여러개의 파일을 묶는 groupid

    @Column(length=60)
    private String category; // 게시글 분류

    @Column(length=40, nullable = false)
    private String poster; // 작성자

    private String guestPw; // 비회원 비밀번호(수정, 삭제)

    private boolean notice;  // 공지글 여부 - true : 공지글

    @Column(nullable = false)
    private String subject; // 게시글 제목

    @Lob
    @Column(nullable = false)
    private String content; // 게시글 내용

    private int viewCount; // 조회수 // 우리 사이트는 조회수가 그렇게 많진 않을 테니 자료형 int로,,,

    private int commentCount; // 댓글 수

    private boolean editorView; // true : 에디터를 통해서 작성한 경우
    // 에디터 작성글은 기본적으로 html형태로 데이터가 들어감
    // 에디터 사용하지 않은 글은 <\n>로 들어감 -> <br> 태그로 바꾸어 주어야 함
    // 에디터를 사용하다가 사용하지않은경우가 있을 수 있음
    // 에디터를 사용하는 경우 : 그냥 출력
    // 에디터를 사용하지 않은경우 : \n -> <br> 로 바꿔서 출력

    //private Long parentSeq; // 부모 게시글 번호 - 답글인 경우

    private Long listOrder; // 1차 정렬 순서 - 내림차순

    @Column(length=60)
    private String listOrder2 = "R"; // 답글 2차 정렬 -> 오름차순

    //private int depth; // 답글 들여쓰기 정도

    @Column(length=20)
    private String ip; // IP 주소

    @Column(length=150)
    private String ua; // User-Agent : 브라우저 정보

    private Long num1; // 추가 필드 : 정수
    private Long num2; // 추가 필드 : 정수
    private Long num3; // 추가 필드 : 정수

    @Column(length=100)
    private String text1; // 추가 필드 : 한줄 텍스트

    @Column(length=100)
    private String text2; // 추가 필드 : 한줄 텍스트

    @Column(length=100)
    private String text3; // 추가 필드 : 한줄 텍스트

    @Lob
    private String longText1; // 추가 필드 : 여러줄 텍스트

    @Lob
    private String longText2; // 추가 필드 : 여러줄 텍스트

    @Lob
    private String longText3; // 추가 필드 : 여러줄 텍스트

    @Transient
    private List<FileInfo> editorFiles; // 에디터 첨부 파일

    //@Transient
    //private List<FileInfo> attachFiles; // 첨부 파일

    @Transient
    private boolean editable; // 수정 가능 여부

    @Transient
    private boolean deletable; // 삭제 가능 여부

    @Transient
    private boolean commentable; // 댓글 작성 가능 여부

    @Transient
    private boolean mine; // 게시글 소유자

    @Transient
    private boolean showEditButton; // 수정 버튼 노출 여부

    @Transient
    private boolean showDeleteButton; // 삭제 버튼 노출 여부

    @Transient
    @JsonIgnore
    private List<CommentData> comments; // 댓글 목록
}