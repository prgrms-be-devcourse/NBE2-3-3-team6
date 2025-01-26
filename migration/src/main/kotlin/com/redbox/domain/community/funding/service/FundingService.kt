package com.redbox.domain.community.funding.service

import com.redbox.domain.community.funding.dto.FundingDetailResponse
import com.redbox.domain.community.funding.dto.FundingWriteRequest
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import com.redbox.domain.community.funding.entity.Priority
import com.redbox.domain.community.funding.exception.FundingNotFoundException
import com.redbox.domain.community.funding.exception.UserNotFoundException
import com.redbox.domain.funding.repository.FundingRepository
import com.redbox.global.util.FileUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FundingService(
    val fundingRepository: FundingRepository? = null

) {
    // 게시글 등록
    @Transactional
    fun createFunding(fundingWriteRequest: FundingWriteRequest, files: MutableList<MultipartFile>?): FundingDetailResponse {
        // val name: String = userRepository.findNameById(getCurrentUserId()).orElseThrow { UserNotFoundException() }

        var funding = Funding(
            //userId = getCurrentUserId(),
            userId = 0L,
            //userName = name,
            fundingTitle = fundingWriteRequest.fundingTitle,
            fundingContent = fundingWriteRequest.fundingContent,
            targetAmount = fundingWriteRequest.targetAmount,
            fundingStatus = FundingStatus.REQUEST,
            progress = FundingStatus.REQUEST,
            donationStartDate = fundingWriteRequest.donationStartDate,
            donationEndDate = fundingWriteRequest.donationEndDate,
            priority = Priority.MEDIUM
        )
        fundingRepository?.save(funding)

        /*if (files != null && !files.isEmpty()) {
            for (file in files) {
                // S3에 파일 업로드
                val newFilename = FileUtils.generateNewFilename()
                val extension = FileUtils.getExtension(file)
                val fullFilename = "$newFilename.$extension"
                s3Service.uploadFile(file, Category.FUNDING, funding.getFundingId(), fullFilename)

                // 파일 데이터 저장
                val attachFile: AttachFile = AttachFile.builder()
                    .category(Category.FUNDING)
                    .funding(funding)
                    .originalFilename(file.originalFilename)
                    .newFilename(fullFilename)
                    .build()

                funding.addAttachFiles(attachFile)
            }
        }*/

        return getFundingDetail(funding.fundingId!!) // null 이 아님
    }

    // 게시글 정보 가져오기
    fun getFundingDetail(fundingId: Long): FundingDetailResponse {
        var funding = fundingRepository!!.findById(fundingId).orElseThrow { FundingNotFoundException() }
        //val userId = currentUserId

        // 좋아요 여부 조회
        //val like = likesRepository!!.findByUserIdAndFundingId(userId, fundingId)
        //val isLiked = like != null && like.isLiked

        //return FundingDetailResponse(funding, isLiked)
        return FundingDetailResponse.from(funding!!, true)
    }
}