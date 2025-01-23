package com.redbox.domain.admin.dto;

import com.redbox.domain.request.entity.Request;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AdminListResponse {

    private Long id; // 게시글 아이디
    private String title; // 제목
    private String author;
    private LocalDate date; // 작성일
    private String status; // 게시글 승인 상태

    public AdminListResponse(Request request) {
        this.id = request.getRequestId();
        this.title = request.getRequestTitle();
        this.author = request.getUserName();
        this.date = request.getRequestDate();
        this.status = request.getRequestStatus().getText();
    }
}
