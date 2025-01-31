package com.redbox.domain.community.funding.service

import com.redbox.domain.community.attach.dto.AttachFileResponse
import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.attach.repository.AttachFileRepository
import com.redbox.domain.community.funding.dto.*
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import com.redbox.domain.community.funding.entity.Like
import com.redbox.domain.community.funding.entity.Priority
import com.redbox.domain.community.funding.exception.FundingNotFoundException
import com.redbox.domain.community.funding.exception.InvalidApproveStatusException
import com.redbox.domain.community.funding.exception.UnauthorizedAccessException
import com.redbox.domain.community.funding.repository.LikeRepository
import com.redbox.domain.funding.repository.FundingRepository
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import com.redbox.global.infra.s3.S3Service
import com.redbox.global.util.FileUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FundingService(
    private val fundingRepository: FundingRepository,
    private val likeRepository: LikeRepository,
    private val authenticationService: AuthenticationService,
    private val s3Service: S3Service,
    private val attachFileRepository: AttachFileRepository
) {
    // 게시글 등록
    @Transactional
    fun createFunding(fundingWriteRequest: FundingWriteRequest, files: MutableList<MultipartFile>?): FundingDetailResponse {

        val funding = Funding(
            userId = authenticationService.getCurrentUserId(),
            fundingTitle = fundingWriteRequest.fundingTitle,
            fundingContent = fundingWriteRequest.fundingContent,
            targetAmount = fundingWriteRequest.targetAmount,
            fundingStatus = FundingStatus.REQUEST,
            progress = FundingStatus.REQUEST,
            donationStartDate = fundingWriteRequest.donationStartDate,
            donationEndDate = fundingWriteRequest.donationEndDate,
            priority = Priority.MEDIUM
        )

        val savedFunding = fundingRepository.save(funding)
        val fundingId = savedFunding.fundingId ?: throw FundingNotFoundException()

        if (files != null && !files.isEmpty()) {
            for (file in files) {
                // S3에 파일 업로드
                val newFilename = FileUtils.generateNewFilename()
                val extension = FileUtils.getExtension(file)
                val fullFilename = "$newFilename.$extension"
                s3Service.uploadFile(file, Category.FUNDING, savedFunding.fundingId, fullFilename)

                // 파일 데이터 저장
                var attachFile = AttachFile(
                    category = Category.FUNDING,
                    funding = savedFunding,
                    originalFilename = requireNotNull(file.originalFilename),
                    newFilename = fullFilename,
                )
                savedFunding.addAttachFiles(attachFile)
            }
        }
        return getFundingDetail(fundingId)
    }

    // 게시글 정보 가져오기 (조회수 증가 X) - 게시글 등록 및 수정 즉시
    fun getFundingDetail(fundingId: Long): FundingDetailResponse {
        val funding: Funding = fundingRepository.findById(fundingId).orElseThrow { FundingNotFoundException() }
        val userId = authenticationService.getCurrentUserId()
        val userName = fundingRepository.findUserNameByFundingId(fundingId) ?: "Unknown"

        val like = likeRepository.findByUserIdAndFundingId(userId, fundingId)
        val isLiked = like != null && like.isLiked

        return FundingDetailResponse.from(funding, userName, isLiked)
    }

    // 게시글 목록 조회 (페이지 처리)
    fun getFundingList(page: Int, size: Int, funding: FundingFilter): PageResponse<ListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        val userId = authenticationService.getCurrentUserId()

        val boardPage: Page<Funding> = fundingRepository.searchBoards(userId, funding, pageable)
        val responsePage: Page<ListResponse> = boardPage.map { funding ->
            val userName = fundingRepository.findUserNameByFundingId(funding.fundingId ?: 0L) ?: "Unknown"
            ListResponse(funding, userName)
        }
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
        val userId = authenticationService.getCurrentUserId()

        // 좋아요 로직
        val like: Like? = likeRepository.findByUserIdAndFundingId(userId, fundingId)
        like?.let {
            if (it.isLiked) {
                it.falseLike()
                funding.decrementLikes()
            } else {
                it.trueLike()
                funding.incrementLikes()
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
        val userId = authenticationService.getCurrentUserId()

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

    fun getMyRequests(
        page: Int, size: Int
    ): PageResponse<FundingListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending())
        return PageResponse(fundingRepository.findMyFundings(0L, pageable))
    }

    fun getAdminFundings(): List<AdminListResponse> {
        return fundingRepository.findAllByStatusRequest()
    }

    @Transactional
    fun approveRequest(
        fundingId: Long,
        request: AdminApproveRequest
    ) {
        val changeFunding =
            fundingRepository.findByIdOrNull(fundingId) ?: throw FundingNotFoundException()
        val approveStatus: String = request.approveStatus

        when (approveStatus) {
            "승인" -> {
                changeFunding.approve()
                changeFunding.inProgress()
            }

            "거절" -> {
                changeFunding.reject()
                changeFunding.rejectProgress()
            }

            else -> throw InvalidApproveStatusException()
        }

        fundingRepository.save(changeFunding)
    }

    fun getAdminFundingDetail(
        fundingId: Long
    ): AdminDetailResponse {
        // 1. 기본 정보 조회 (게시글 + 작성자)
        val fundingDetail = fundingRepository.findDetailById(fundingId)
            ?: throw FundingNotFoundException()

        // 2. 첨부파일 조회
        val attachFiles = attachFileRepository.findAttachFiles(fundingId)
            .map { AttachFileResponse(it) }

        // 3. 결과 조합
        return AdminDetailResponse(
            id = fundingDetail.id,
            title = fundingDetail.title,
            author = fundingDetail.author,
            date = fundingDetail.date,
            startDate = fundingDetail.startDate,
            endDate = fundingDetail.endDate,
            targetAmount = fundingDetail.targetAmount,
            status = fundingDetail.status,
            views = fundingDetail.views,
            content = fundingDetail.content,
            attachFiles = attachFiles
        )
    }
}