package com.redbox.domain.user.admin.service

import com.redbox.domain.community.funding.dto.AdminApproveRequest
import com.redbox.domain.community.funding.dto.AdminDetailResponse
import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.community.funding.entity.FundingStatus
import com.redbox.domain.community.funding.facade.FundingFacade
import com.redbox.domain.donation.facade.DonationFacade
import com.redbox.domain.redcard.facade.RedcardFacade
import com.redbox.domain.user.admin.dto.AdminStatsResponse
import com.redbox.domain.user.user.facade.UserFacade
import com.redbox.global.auth.service.AuthenticationService
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val fundingFacade: FundingFacade,
    private val userFacade: UserFacade,
    private val redcardFacade: RedcardFacade,
    private val donationFacade: DonationFacade,
    private val authenticationService: AuthenticationService
) {

    fun getFundings(): List<AdminListResponse> {
        return fundingFacade.getAdminFundings()
    }

    fun approveRequest(
        fundingId: Long,
        request: AdminApproveRequest
    ) {
        fundingFacade.approveRequest(fundingId, request)
    }

    fun getFundingDetail(fundingId: Long): AdminDetailResponse {
        return fundingFacade.getAdminFundingDetail(fundingId)
    }

    fun getHotFundings(): List<AdminListResponse> {
        return fundingFacade.getHotFundings()
    }

    fun getLikedFundings(): List<AdminListResponse> {
        val userId = authenticationService.getCurrentUserId()
        return fundingFacade.getLikedFundings(userId)
    }

    fun getAdminStats(): AdminStatsResponse {
        val userCount: Int? = userFacade.getActiveUserCount()
        val redcardCountInRedbox: Int? = redcardFacade.getCountAllInRedbox()
        val sumDonation: Int? = donationFacade.getSumDonationAmountInRedbox()
        val fundingCount: Int? = fundingFacade.getCountByFundingStatus(FundingStatus.REQUEST)

        return AdminStatsResponse(
            userCount ?: 0,
            redcardCountInRedbox ?: 0,
            sumDonation ?: 0,
            fundingCount ?: 0
        )
    }
}