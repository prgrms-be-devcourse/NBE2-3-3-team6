package com.redbox.domain.request.dto;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.entity.RequestStatus;
import com.redbox.domain.user.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private RequestStatus requestStatus;
    private String status; // 상태
    private String content; // 내용
    private boolean isLiked; // 좋아요 여부

    private List<AttachFileResponse> attachFileResponses; // 첨부 파일 리스트

    public DetailResponse(Request request, Boolean isLiked) {
        this.id = request.getRequestId();
        this.userName = request.getUserName();
        this.date = request.getCreatedAt().toLocalDate();
        this.title = request.getRequestTitle();
        this.views = request.getRequestHits();
        this.startDate = request.getDonationStartDate();
        this.endDate = request.getDonationEndDate();
        this.targetAmount = request.getTargetAmount();
        this.currentAmount = request.getCurrentAmount();
        this.likes = request.getRequestLikes();
        this.requestStatus = request.getRequestStatus();
        this.status = request.getProgress().getText();
        this.content = request.getRequestContent();
        this.isLiked = isLiked;
        this.attachFileResponses = request.getAttachFiles()
                .stream().map(AttachFileResponse::new).toList();
    }
}