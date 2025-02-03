package com.redbox.domain.dashboard.service

import com.redbox.domain.dashboard.dto.DashboardResponse
import com.redbox.domain.dashboard.dto.DonationStats
import com.redbox.domain.dashboard.dto.UserInfo
import com.redbox.domain.donation.application.DonationService
import com.redbox.domain.funding.repository.FundingRepository
import com.redbox.global.auth.service.AuthenticationService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DashboardService(
    private val authenticationService: AuthenticationService,
    private val donationService: DonationService,
    private val fundingRepository: FundingRepository
) {

    fun getDashboardData(): DashboardResponse {
        // 현재 인증된 사용자 정보 가져오기
        val user = authenticationService.getCurrentUser()

        // UserInfo 생성
        val userInfo = UserInfo(
            name = user.name,
            birth = user.birth,
            gender = user.gender,
            phoneNumber = user.phoneNumber
        )

        // 기부 통계 조회
        val userId: Long = user.id ?: throw IllegalStateException("User ID is null")
        val totalDonatedCards: Int = donationService.getTotalDonatedCards(userId)
        val patientsHelped: Int = donationService.getPatientsHelped(userId)
        val lastDonationDate: LocalDate = donationService.getLastDonationDate(userId)
            ?: LocalDate.now() // 기본 값 설정

        // 진행 중인 요청 게시글 수 조회
        val inProgressRequests: Int =
            fundingRepository.countInProgressFundingsByUserId(userId)

        // 등급 계산
        val grade = calculateGrade(totalDonatedCards)

        val donationStats = DonationStats(
            totalDonatedCards,
            patientsHelped,
            grade,
            lastDonationDate,
            inProgressRequests
        )
        return DashboardResponse(userInfo, donationStats)
    }

    private fun calculateGrade(totalDonatedCards: Int): String {
        return when {
            totalDonatedCards >= 50 -> "VIP"
            totalDonatedCards >= 20 -> "GOLD"
            totalDonatedCards >= 10 -> "SILVER"
            else -> "BRONZE"
        }
    }
}