package com.redbox.domain.user.admin.service

import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.community.funding.facade.FundingFacade
import org.springframework.stereotype.Service

@Service
class AdminService(
    private val fundingFacade: FundingFacade
) {
    // 요청 게시글 리스트 조회
    fun getFundings(): List<AdminListResponse> {
        // 요청중 리스트만 추출
        return fundingFacade.getAdminFundings()
    }
}