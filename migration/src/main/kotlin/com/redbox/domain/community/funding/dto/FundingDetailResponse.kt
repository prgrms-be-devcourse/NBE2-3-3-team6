package com.redbox.domain.community.funding.dto

import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import java.time.LocalDate

data class FundingDetailResponse(
    val id: Long,
    // val userName: String = funding.getUserName() // user 통해서 name 보내주기 변경
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
    var isLiked: Boolean

    //var attachFileResponses: List<AttachFileResponse> = funding.getAttachFiles()
    //    .stream().map { AttachFileResponse() }.toList() // 첨부 파일 리스트
) {
    companion object {
        fun from(funding: Funding, isLiked: Boolean): FundingDetailResponse {
            return FundingDetailResponse(
                id = funding.fundingId ?: throw IllegalStateException("Funding ID is null"),
                // userName = user 통해서 name 보내주기
                date = funding.createdAt?.toLocalDate() ?: LocalDate.now(),
                title = funding.fundingTitle ?: "No Title",
                views = funding.fundingHits,
                startDate = funding.donationStartDate ?: LocalDate.MIN,
                endDate = funding.donationEndDate ?: LocalDate.MAX,
                targetAmount = funding.targetAmount ?: 0,
                currentAmount = funding.currentAmount ?: 0,
                likes = funding.fundingLikes,
                fundingStatus = funding.fundingStatus,
                status = funding.progress.text ?: "No Status",
                content = funding.fundingContent ?: "No Content",
                isLiked = isLiked
            )
        }
    }
}