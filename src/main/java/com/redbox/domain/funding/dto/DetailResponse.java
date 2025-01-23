package com.redbox.domain.funding.dto;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.entity.FundingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {
    private Long id; // 게시글 ID
    private String userName;
    private LocalDate date; // 등록일
    private String title; // 제목
    private int views; // 조회수
    private LocalDate startDate; // 기부 시작일
    private LocalDate endDate; // 기부 종료일
    private int targetAmount; // 목표 수량
    private int currentAmount; // 현재 모금된 수량

    private int likes; // 좋아요 수
    private FundingStatus fundingStatus;
    private String status; // 상태
    private String content; // 내용
    private boolean isLiked; // 좋아요 여부

    private List<AttachFileResponse> attachFileResponses; // 첨부 파일 리스트

    public DetailResponse(Funding funding, Boolean isLiked) {
        this.id = funding.getFundingId();
        this.userName = funding.getUserName();
        this.date = funding.getCreatedAt().toLocalDate();
        this.title = funding.getFundingTitle();
        this.views = funding.getFundingHits();
        this.startDate = funding.getDonationStartDate();
        this.endDate = funding.getDonationEndDate();
        this.targetAmount = funding.getTargetAmount();
        this.currentAmount = funding.getCurrentAmount();
        this.likes = funding.getFundingLikes();
        this.fundingStatus = funding.getFundingStatus();
        this.status = funding.getProgress().getText();
        this.content = funding.getFundingContent();
        this.isLiked = isLiked;
        this.attachFileResponses = funding.getAttachFiles()
                .stream().map(AttachFileResponse::new).toList();
    }
}