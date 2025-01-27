package com.redbox.domain.community.funding.dto

import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import java.time.LocalDate

data class ListResponse(

    val fundingId: Long?, // 게시글 아이디

    val userId: Long?,
    val userEmail: String?,
    //var userName: String,

    val fundingTitle: String?,
    val fundingContent: String?,
    val targetAmount: Int?,
    val currentAmount: Int?,
    val progressPercent: Double,

    val fundingStatus: FundingStatus,
    val progress: String?,

    val fundingDate: LocalDate?,
    val fundingHits: Int,
    val fundingLikes: Int,
) {
    constructor(funding: Funding) : this(
        fundingId = funding.fundingId,
        userId = funding.userId,
        userEmail = funding.createdBy,
        // userName
        fundingTitle = funding.fundingTitle,
        fundingContent = funding.fundingContent,
        targetAmount = funding.targetAmount,
        currentAmount = funding.currentAmount,
        progressPercent = (funding.targetAmount?.let { funding.currentAmount?.toDouble()?.div(it.toDouble()) })?.times(100) ?: 1.0,
        fundingStatus = funding.fundingStatus,
        progress = funding.progress.text,
        fundingDate = funding.fundingDate,
        fundingHits = funding.fundingHits,
        fundingLikes = funding.fundingLikes
    )
}