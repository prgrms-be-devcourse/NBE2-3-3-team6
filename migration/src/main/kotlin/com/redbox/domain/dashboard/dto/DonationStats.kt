package com.redbox.domain.dashboard.dto

import java.time.LocalDate

data class DonationStats(
    val totalDonatedCards: Int = 0,           // 총 기부한 헌혈증 개수
    val patientsHelped: Int = 0,              // 도움을 준 사람 수
    val grade: String? = null,                // 등급
    val lastDonationDate: LocalDate? = null,  // 최근 기부 날짜
    val inProgressRequests: Int = 0           // 진행 중인 요청 게시글 수
)