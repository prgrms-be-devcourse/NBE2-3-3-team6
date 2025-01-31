package com.redbox.domain.user.admin.service

import com.redbox.domain.community.funding.dto.AdminApproveRequest
import com.redbox.domain.community.funding.dto.AdminDetailProjection
import com.redbox.domain.community.funding.dto.AdminDetailResponse
import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.community.funding.facade.FundingFacade
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val fundingFacade: FundingFacade
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
}