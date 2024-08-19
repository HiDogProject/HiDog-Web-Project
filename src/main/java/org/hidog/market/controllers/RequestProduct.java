package org.hidog.market.controllers;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hidog.file.entities.FileInfo;

import java.util.List;
import java.util.UUID;

@Data
public class RequestProduct {

    private String mode = "new";

    private Long seq;

    @NotBlank
    private String bid = "market";

    private String gid = UUID.randomUUID().toString();

    private String category;

    @NotBlank
    private String poster;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    // 추가필드 - 정수
    @NotBlank
    @Column(name = "price")
    private Long num1; //가격
    private Long num2;
    private Long num3;

    // 추가필드 - 한줄 텍스트
    @NotBlank
    @Column(name = "pState")
    private String text1; //상태

    @NotBlank
    @Column(name = "state")
    private String text2; //판매상태

    private String text3;

    // 추가필드 - 여러줄 텍스트
    private String longText1;
    private String longText2;
    private String longText3;

    private List<FileInfo> editorFiles; // 에디터 파일 목록
    private List<FileInfo> attachFiles; // 첨부 파일 목록

}
