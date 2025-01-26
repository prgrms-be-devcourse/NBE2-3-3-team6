package com.redbox.domain.community.funding.controller

import com.redbox.domain.community.funding.dto.FundingDetailResponse
import com.redbox.domain.community.funding.dto.FundingWriteRequest
import com.redbox.domain.community.funding.service.FundingService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class FundingController(
    val fundingService: FundingService,
) {
    // 게시글 등록 (조회수 증가 X)
    @PostMapping("/write/funding")
    fun fundingWrite(
        @RequestPart("post") writeFunding: @Valid FundingWriteRequest,
        @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?
    ): ResponseEntity<FundingDetailResponse> {
        val detailResponse = fundingService.createFunding(writeFunding, files)
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse)
    }
}