package com.redbox.domain.community.funding.facade

import com.redbox.domain.community.funding.dto.*
import com.redbox.domain.community.funding.service.FundingService
import com.redbox.global.entity.PageResponse
import org.springframework.stereotype.Component

@Component
class FundingFacade(
    private val fundingService: FundingService
) {

    fun getMyRequests(
        page: Int, size: Int
    ): PageResponse<FundingListResponse> {
        return fundingService.getMyRequests(page, size)
    }

    fun getAdminFundings(): List<AdminListResponse> {
        return fundingService.getAdminFundings()
    }

    fun approveRequest(
        fundingId: Long,
        request: AdminApproveRequest
    ) {
        fundingService.approveRequest(fundingId, request)
    }

    fun getAdminFundingDetail(
        fundingId: Long
    ): AdminDetailResponse {
        return fundingService.getAdminFundingDetail(fundingId)
    }

    fun getHotFundings(): List<AdminListResponse> {
        return fundingService.getHotFundings()
    }
}