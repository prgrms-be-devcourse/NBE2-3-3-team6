package com.redbox.domain.community.funding.service

import com.redbox.domain.community.funding.dto.FundingDetailResponse
import com.redbox.domain.community.funding.dto.FundingFilter
import com.redbox.domain.community.funding.dto.FundingWriteRequest
import com.redbox.domain.community.funding.dto.ListResponse
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import com.redbox.domain.community.funding.entity.Like
import com.redbox.domain.community.funding.entity.Priority
import com.redbox.domain.community.funding.exception.FundingNotFoundException
import com.redbox.domain.community.funding.exception.UnauthorizedAccessException
import com.redbox.domain.community.funding.repository.LikeRepository
import com.redbox.domain.funding.repository.FundingRepository
import com.redbox.global.entity.PageResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FundingService(
    private val fundingRepository: FundingRepository,
    private val likeRepository: LikeRepository

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

        // TODO : 파일 처리
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

        val savedFunding = fundingRepository.save(funding)
        val fundingId = savedFunding.fundingId ?: throw FundingNotFoundException()
        return getFundingDetail(fundingId)
    }

    // 게시글 정보 가져오기 (조회수 증가 X) - 게시글 등록 및 수정 즉시
    fun getFundingDetail(fundingId: Long): FundingDetailResponse {
        var funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()
        //val userId = currentUserId

        // TODO : 좋아요 처리
        // 좋아요 여부 조회
        //val like = likesRepository!!.findByUserIdAndFundingId(userId, fundingId)
        //val isLiked = like != null && like.isLiked

        //return FundingDetailResponse(funding, isLiked)
        return FundingDetailResponse.from(funding, true)
    }

    // 게시글 목록 조회 (페이지 처리)
    fun getFundingList(page: Int, size: Int, funding: FundingFilter): PageResponse<ListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        //val userId: Long = getCurrentUserId() // TODO : UserID
        val userId: Long = 0L

        val boardPage: Page<Funding> = fundingRepository.searchBoards(userId, funding, pageable)
        val responsePage: Page<ListResponse> = boardPage.map { ListResponse(it) }

        return PageResponse(responsePage)
    }

    // 게시글 상세 조회 (조회수 증가 O)
    @Transactional
    fun viewFunding(fundingId: Long): FundingDetailResponse {
        val funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()
        funding.incrementHits()
        return getFundingDetail(funding.fundingId ?: throw FundingNotFoundException())
    }

    // 게시글 좋아요 확인
    @Transactional
    fun likeFunding(fundingId: Long) {
        val funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()
        // val userId: Long = getCurrentUserId() TODO: UserID
        val userId: Long = 0L

        // 좋아요 로직
        val like: Like? = likeRepository.findByUserIdAndFundingId(userId, fundingId)
        like?.let {
            if (it.isLiked) {
                it.falseLike()
                funding.decrementLikes()
                println(it.isLiked)
            } else {
                it.trueLike()
                funding.incrementLikes()
                println(it.isLiked)
            }
            likeRepository.save(it)
            fundingRepository.save(funding)
        } ?: run {
            // Like 엔티티가 없으면 새로 생성
            val newLike = Like(
                userId = userId,
                fundingId = fundingId,
                isLiked = true,
            )
            funding.incrementLikes()
            likeRepository.save(newLike)
            fundingRepository.save(funding)
        }
    }

    // 게시글 수정
    @Transactional
    fun modifyFunding(fundingId: Long, writeRequest: FundingWriteRequest): FundingDetailResponse? {
        val funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()

        // 업데이트 메서드 호출
        funding.updateFunding(
            writeRequest.fundingTitle,
            writeRequest.fundingContent,
            writeRequest.donationStartDate,
            writeRequest.donationEndDate,
            writeRequest.targetAmount,
        )
        // 저장 후 수정된 기금의 상세 정보 반환
        val modifyFunding = fundingRepository.save(funding)
        return getFundingDetail( modifyFunding.fundingId ?: throw FundingNotFoundException())
    }

    // 게시글 수정 권한
    fun modifyAuthorize(fundingId: Long) {
        val funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()
        // val userId: Long = getCurrentUserId() TODO: UserID
        val userId: Long = 0L

        require(funding.userId == userId){
            throw UnauthorizedAccessException()
        }
    }

    // 게시글 삭제
    @Transactional
    fun deleteFunding(fundingId: Long) {
        val funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() } ?: throw FundingNotFoundException()
        funding.drop()
        funding.dropProgress()
        fundingRepository.save(funding)
    }
}