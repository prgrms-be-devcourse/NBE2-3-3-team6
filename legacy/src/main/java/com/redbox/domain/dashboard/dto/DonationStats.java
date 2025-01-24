package com.redbox.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DonationStats {
    private int totalDonatedCards; // 총 기부한 헌혈증 개수
    private int patientsHelped;    // 도움을 준 사람 수
    private String grade;
    private LocalDate lastDonationDate;
    private int inProgressRequests;     // 진행 중인 요청 게시글 수

}