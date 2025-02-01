package com.redbox.domain.user.user.dto

import jakarta.validation.constraints.NotBlank

data class UpdateUserInfoRequest(
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String,
    @field:NotBlank(message = "연락처를 입력해주세요.")
    val phoneNumber: String,

    val roadAddress: String,
    val extraAddress: String,
    val detailAddress: String
)