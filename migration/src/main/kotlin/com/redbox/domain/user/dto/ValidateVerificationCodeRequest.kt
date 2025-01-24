package com.redbox.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ValidateVerificationCodeRequest(
    @field:Email(message = "이메일 형식이 맞지 않습니다.") @NotBlank(message = "이메일을 입력해주세요.")
    val email: String,

    @field:NotBlank(message = "인증코드를 입력해주세요.")
    val verificationCode: String
)