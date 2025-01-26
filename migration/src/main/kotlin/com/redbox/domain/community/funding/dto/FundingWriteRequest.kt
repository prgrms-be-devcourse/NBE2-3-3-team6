package com.redbox.domain.community.funding.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDate

data class FundingWriteRequest(
    @field:NotBlank(message = "제목을 입력해주세요")
    var fundingTitle: String? = null,

    @field:NotBlank(message = "내용을 입력해주세요")
    var fundingContent: String? = null, // 게시글 내용

    @field:NotBlank(message = "필요한 헌혈증 개수를 입력해주세요")
    var targetAmount: Int? = null, // 목표 개수

    @field:NotBlank(message = "시작 일자를 입력해주세요")
    var donationStartDate: LocalDate? = null, // 기부 시작 일자

    @field:NotBlank(message = "종료 일자를 입력해주세요")
    var donationEndDate: LocalDate? = null // 기부 종료 일자
)