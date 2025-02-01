package com.redbox.domain.user.user.dto

import jakarta.validation.constraints.NotBlank

data class DropInfoRequest(
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String
)
