package com.redbox.domain.community.funding.dto

import com.redbox.domain.community.attach.dto.AttachFileResponse
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import java.time.LocalDate

data class FundingDetailResponse(
    val id: Long,
    val userName: String,
    var date: LocalDate,
    var title: String,
    var views: Int,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var targetAmount: Int,
    var currentAmount: Int,
    var likes: Int,
    var fundingStatus: FundingStatus,
    var status: String,
    var content: String,
    var isLiked: Boolean,
    var attachFileResponses: MutableList<AttachFileResponse>
) {
    companion object {
        fun from(funding: Funding, userName: String, isLiked: Boolean): FundingDetailResponse {
            return FundingDetailResponse(
                id = funding.fundingId ?: throw IllegalStateException("Funding ID is null"),
                userName = userName,
                date = funding.createdAt?.toLocalDate() ?: LocalDate.now(),
                title = funding.fundingTitle ?: "No Title",
                views = funding.fundingHits,
                startDate = funding.donationStartDate ?: LocalDate.MIN,
                endDate = funding.donationEndDate ?: LocalDate.MAX,
                targetAmount = funding.targetAmount ?: 0,
                currentAmount = funding.currentAmount ?: 0,
                likes = funding.fundingLikes,
                fundingStatus = funding.fundingStatus,
                status = funding.progress.text,
                content = funding.fundingContent ?: "No Content",
                isLiked = isLiked,
                attachFileResponses = funding.attachFiles.mapNotNull { attachFile ->
                    attachFile?.let { AttachFileResponse(it) }
                }.toMutableList()
            )
        }
    }
}