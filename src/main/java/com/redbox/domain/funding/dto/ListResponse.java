package com.redbox.domain.funding.dto;

import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.entity.FundingStatus;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ListResponse {

    private Long fundingId; // 게시글 아이디

    private Long userId;
    private String userEmail;
    private String userName;

    private String fundingTitle;
    private String fundingContent;
    private int targetAmount;
    private int currentAmount;
    private double progressPercent;

    private FundingStatus fundingStatus;
    private String progress;

    private LocalDate fundingDate;
    private int fundingHits;
    private int fundingLikes;

    // Funding 엔티티를 매개변수로 받는 생성자 추가
    public ListResponse(Funding funding) {
        this.fundingId = funding.getFundingId();
        this.userId = funding.getUserId();
        this.userEmail = funding.getCreatedBy();
        this.userName = funding.getUserName();
        this.fundingTitle = funding.getFundingTitle();
        this.fundingContent = funding.getFundingContent();
        this.targetAmount = funding.getTargetAmount();
        this.currentAmount = funding.getCurrentAmount();
        this.progressPercent = ( (double) funding.getCurrentAmount() / funding.getTargetAmount()) * 100;
        this.progress = funding.getProgress().getText();
        this.fundingStatus = funding.getFundingStatus();
        this.fundingDate = funding.getFundingDate();
        this.fundingHits = funding.getFundingHits();
        this.fundingLikes = funding.getFundingLikes();
    }

}
