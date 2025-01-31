package com.redbox.domain.user.admin.service

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
}