package com.redbox.domain.funding.repository

import com.redbox.domain.community.funding.dto.FundingFilter
import com.redbox.domain.community.funding.entity.Funding
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FundingRepositoryCustom {
    fun searchBoards(userId: Long?, fundingFilter: FundingFilter, pageable: Pageable): Page<Funding>
}
