package com.redbox.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class FindIdRequest (
    @field:NotBlank(message = "이름을 입력해주세요.")
    val userName: String,

    @field:Pattern(
        regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
        message = "핸드폰 번호의 형식에 맞게 입력해주세요. 010-0000-0000"
    )
    @field:NotBlank(message = "연락처를 입력해주세요.")
    val phoneNumber: String
)