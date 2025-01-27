package com.redbox.domain.community.funding.repository

import com.redbox.domain.community.funding.entity.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun findByUserIdAndFundingId(userId: Long?, fundingId: Long?): Like?
}