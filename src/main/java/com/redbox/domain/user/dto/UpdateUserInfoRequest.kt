package com.redbox.domain.user.dto

data class UpdateUserInfoRequest(
    val name: String,
    val phoneNumber: String,
    val roadAddress: String,
    val extraAddress: String,
    val detailAddress: String
)
