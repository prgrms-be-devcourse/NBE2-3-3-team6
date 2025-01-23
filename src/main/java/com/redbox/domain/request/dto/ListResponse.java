package com.redbox.domain.request.dto;

import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.entity.RequestStatus;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ListResponse {

    private Long requestId; // 게시글 아이디

    private Long userId;
    private String userEmail;
    private String userName;

    private String requestTitle;
    private String requestContent;
    private int targetAmount;
    private int currentAmount;
    private double progressPercent;

    private RequestStatus requestStatus;
    private String progress;

    private LocalDate requestDate;
    private int requestHits;
    private int requestLikes;

    // Request 엔티티를 매개변수로 받는 생성자 추가
    public ListResponse(Request request) {
        this.requestId = request.getRequestId();
        this.userId = request.getUserId();
        this.userEmail = request.getCreatedBy();
        this.userName = request.getUserName();
        this.requestTitle = request.getRequestTitle();
        this.requestContent = request.getRequestContent();
        this.targetAmount = request.getTargetAmount();
        this.currentAmount = request.getCurrentAmount();
        this.progressPercent = ( (double) request.getCurrentAmount() / request.getTargetAmount()) * 100;
        this.progress = request.getProgress().getText();
        this.requestStatus = request.getRequestStatus();
        this.requestDate = request.getRequestDate();
        this.requestHits = request.getRequestHits();
        this.requestLikes = request.getRequestLikes();
    }

}
