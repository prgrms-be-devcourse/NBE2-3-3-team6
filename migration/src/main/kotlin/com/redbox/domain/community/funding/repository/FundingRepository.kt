package com.redbox.domain.funding.repository

import com.redbox.domain.community.funding.entity.Funding
import org.springframework.data.jpa.repository.JpaRepository

interface FundingRepository : JpaRepository<Funding?, Long?>, FundingRepositoryCustom {}