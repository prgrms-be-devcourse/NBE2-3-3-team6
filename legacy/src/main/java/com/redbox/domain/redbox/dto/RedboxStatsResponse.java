package com.redbox.domain.redbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RedboxStatsResponse {
    private int totalDonatedCards;  // 헌혈증 누적 개수
    private int totalPatientsHelped;    // 도움을 받은 환자 수
    private int inProgressRequests; // 진행 중인 요청 게시글 수

}
