package com.redbox.domain.community.funding.controller

import com.redbox.domain.community.funding.dto.FundingDetailResponse
import com.redbox.domain.community.funding.dto.FundingFilter
import com.redbox.domain.community.funding.dto.FundingWriteRequest
import com.redbox.domain.community.funding.dto.ListResponse
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.service.FundingService
import com.redbox.global.entity.PageResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.thymeleaf.engine.IThrottledTemplateWriterControl

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

    // 게시글 상세 조회
    @GetMapping("/fundings/{fundingId}")
    fun viewFundingDetail(
        @PathVariable("fundingId") fundingId: Long,
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.viewFunding(fundingId)
        return ResponseEntity.status(HttpStatus.OK).body(detailResponse)
    }

    // 게시글 목록 조회
    @GetMapping("/fundings")
    fun getFundings(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute funding: FundingFilter
    ): ResponseEntity<PageResponse<ListResponse>> {
        val response: PageResponse<ListResponse> = fundingService.getFundingList(page, size, funding)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    // 게시글 수정
    @PutMapping("/fundings/{fundingId}")
    fun fundingModify(
        @PathVariable("fundingId") fundingId: Long,
        @RequestBody writeFunding: @Valid FundingWriteRequest
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.modifyFunding(fundingId, writeFunding)
        return ResponseEntity.status(HttpStatus.OK).body(detailResponse)
    }
}