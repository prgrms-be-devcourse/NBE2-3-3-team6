package com.redbox.domain.funding.repository;

import com.redbox.domain.funding.dto.FundingFilter;
import com.redbox.domain.funding.entity.Funding;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FundingRepositoryCustom {
    public Page<Funding> searchBoards(Long userId, FundingFilter fundingFilter, Pageable pageable);
}
