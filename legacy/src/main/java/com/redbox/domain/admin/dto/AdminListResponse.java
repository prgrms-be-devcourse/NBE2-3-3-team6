package com.redbox.domain.admin.dto;

import com.redbox.domain.funding.entity.Funding;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AdminListResponse {

    private Long id; // 게시글 아이디
    private String title; // 제목
    private String author;
    private LocalDate date; // 작성일
    private String status; // 게시글 승인 상태

    public AdminListResponse(Funding funding) {
        this.id = funding.getFundingId();
        this.title = funding.getFundingTitle();
        this.author = funding.getUserName();
        this.date = funding.getFundingDate();
        this.status = funding.getFundingStatus().getText();
    }
}
