package com.redbox.domain.community.funding.controller

import com.redbox.domain.community.funding.dto.*
import com.redbox.domain.community.funding.service.FundingService
import com.redbox.global.entity.PageResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class FundingController(
    val fundingService: FundingService,
) {
    // 게시글 등록
    @PostMapping("/write/funding")
    fun fundingWrite(
        @RequestPart("post") writeFunding: @Valid FundingWriteRequest,
        @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.createFunding(writeFunding, files)
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse)
    }

    // 게시글 목록 조회
    @GetMapping("/fundings")
    fun getFundings(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute funding: FundingFilter
    ): ResponseEntity<PageResponse<ListResponse>> {
        val response: PageResponse<ListResponse> = fundingService.getFundingList(page, size, funding)
        return ResponseEntity.ok(response)
    }

    // 게시글 상세 조회
    @GetMapping("/fundings/{fundingId}")
    fun viewFundingDetail(
        @PathVariable("fundingId") fundingId: Long,
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.viewFunding(fundingId)
        return ResponseEntity.ok(detailResponse)
    }

    // 게시글 좋아요 확인
    @PostMapping("/fundings/{fundingId}/like")
    fun fundingLike(@PathVariable fundingId: Long): ResponseEntity<LikeResponse> {
        fundingService.likeFunding(fundingId)
        val likeResponse = LikeResponse("처리되었습니다")
        return ResponseEntity.status(HttpStatus.OK).body(likeResponse)
    }

    // 게시글 수정
    @PutMapping("/fundings/{fundingId}")
    fun fundingModify(
        @PathVariable("fundingId") fundingId: Long,
        @RequestBody writeFunding: @Valid FundingWriteRequest
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.modifyFunding(fundingId, writeFunding)
        return ResponseEntity.ok(detailResponse)
    }

    // 게시글 수정 내용 확인
    @GetMapping("/fundings/modify/{fundingId}")
    fun fundingModify(
        @PathVariable("fundingId") fundingId: Long,
    ): ResponseEntity<FundingDetailResponse> {
        fundingService.modifyAuthorize(fundingId)
        val detailResponse = fundingService.getFundingDetail(fundingId)
        return ResponseEntity.ok(detailResponse)
    }

    // 게시글 삭제
    @DeleteMapping("/fundings/{fundingId}")
    fun fundingDelete(
        @PathVariable("fundingId") fundingId: Long
    ): ResponseEntity<FundingDetailResponse> {
        fundingService.deleteFunding(fundingId)
        return ResponseEntity.ok().build()
    }
}