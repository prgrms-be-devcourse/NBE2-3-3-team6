package com.redbox.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class VerificationCodeRequest(
    @field:Email(message = "이메일 형식이 맞지 않습니다.")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String
)
