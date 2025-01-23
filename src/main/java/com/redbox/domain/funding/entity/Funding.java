package com.redbox.domain.funding.entity;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "fundings")
public class Funding extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_id")
    private Long fundingId; // 게시글 아이디

    private Long userId;
    private String userName;
    private String fundingTitle;
    private String fundingContent;
    private int targetAmount;
    private int currentAmount;

    @Enumerated(EnumType.STRING)
    private FundingStatus fundingStatus;

    @Enumerated(EnumType.STRING)
    private FundingStatus progress;

    private LocalDate fundingDate;
    private LocalDate donationStartDate;
    private LocalDate donationEndDate;

    private String fundingAttachFile; // 파일 로컬에 저장

    @Enumerated(EnumType.STRING)
    private Priority priority; // 중요도

    private int fundingHits;
    private int fundingLikes; // 좋아요 수

    @OneToMany(mappedBy = "funding", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachFile> attachFiles = new ArrayList<>();

    @Builder
    public Funding(Long userId, String userName, String fundingTitle, String fundingContent, int targetAmount, int currentAmount, FundingStatus fundingStatus, FundingStatus progress, LocalDate donationStartDate, LocalDate donationEndDate, LocalDate fundingDate, String fundingAttachFile, Priority priority, int fundingHits, int fundingLikes) {
        this.userId = userId;
        this.userName = userName;
        this.fundingTitle = fundingTitle;
        this.fundingContent = fundingContent;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.fundingStatus = fundingStatus;
        this.progress = progress;
        this.donationStartDate = donationStartDate;
        this.donationEndDate = donationEndDate;
        this.fundingDate = LocalDate.now();
        this.fundingAttachFile = fundingAttachFile;
        this.priority = priority;
        this.fundingHits = fundingHits;
        this.fundingLikes = fundingLikes;
    }

    public void addAttachFiles(AttachFile attachFile) {
        attachFile.validateNull();
        if (attachFile.isDuplicateIn(this.attachFiles)) return;

        this.attachFiles.add(attachFile);
        attachFile.setFunding(this);
    }

    public void removeAttachFiles(AttachFile attachFile) {
        attachFile.validateNull();

        this.attachFiles.remove(attachFile);
        attachFile.setFunding(null);
    }

    public void updateFunding(String title, String content, LocalDate DonationStartDate, LocalDate DonationEndDate, int targetAmount) {
        this.fundingTitle = title;
        this.fundingContent = content;
        this.donationStartDate = DonationStartDate;
        this.donationEndDate = DonationEndDate;
        this.targetAmount = targetAmount;
    }

    public void approve() {this.fundingStatus = FundingStatus.APPROVE;}
    public void reject() {this.fundingStatus = FundingStatus.REJECT;}
    public void drop() {this.fundingStatus = FundingStatus.DROP;}

    public void expired() {this.progress = FundingStatus.EXPIRED;}
    public void inProgress() {this.progress = FundingStatus.IN_PROGRESS;}
    public void rejectProgress() {this.progress = FundingStatus.REJECT;}
    public void dropProgress() {this.progress = FundingStatus.DROP;}

    public void incrementHits() {this.fundingHits = fundingHits + 1;}
    public void incrementLikes() {this.fundingLikes = fundingLikes + 1;}
    public void decrementLikes() {this.fundingLikes = fundingLikes - 1;}
}
