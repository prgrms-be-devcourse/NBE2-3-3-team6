package com.redbox.domain.dashboard.dto

data class DashboardResponse(
    val userInfo: UserInfo?,                  // 사용자 정보
    val donationStats: DonationStats?         // 기부 통계
)