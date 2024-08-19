package org.hidog.market.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hidog.file.entities.FileInfo;

import java.util.List;
import java.util.UUID;

@Data
public class RequestProduct {

    private String mode = "write";

    private Long seq;

    @NotBlank
    private String bid;

    private String gid = UUID.randomUUID().toString();

    private String category;

    @NotBlank
    private String poster; //판매글 작성자

    @NotBlank
    private String subject; //판매글 제목

    @NotBlank
    private String content; //판매글 설명

    @NotBlank
    private Long num1; //가격
    @NotBlank
    private Long num2; //수량
    @NotBlank
    private String text1; //상품명


    private List<FileInfo> attachFiles; // 첨부 파일 목록

}
