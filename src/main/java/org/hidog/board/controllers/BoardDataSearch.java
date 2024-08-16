package org.hidog.board.controllers;

import lombok.Data;
import org.hidog.global.CommonSearch;

import java.util.List;

@Data
public class BoardDataSearch extends CommonSearch { // 검색을 위한 커맨드 객체
    /**
     * 검색 옵션
     *  ALL :
     * SUBJECT : 제목 검색
     * CONTENT : 내용 검색
     * subject_content : 제목 + 내용(OR) 검색
     * NAME : 이름(작성자, 회원명)
     *
     */

    private String bid; // 게시판 ID
    private List<String> bids; // 게시판 ID 여러개

    private String sort; // 정렬 조건
}
