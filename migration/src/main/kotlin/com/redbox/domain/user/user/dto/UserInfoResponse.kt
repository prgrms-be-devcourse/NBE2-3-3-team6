package com.redbox.domain.user.user.dto

import com.redbox.domain.user.user.entity.User

data class UserInfoResponse(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val roadAddress: String? = null,
    val extraAddress: String? = null,
    val detailAddress: String? = null,
) {
    constructor(user: User) : this(
        email = user.email,
        name = user.name,
        phoneNumber = user.phoneNumber,
        roadAddress = user.roadAddress,
        extraAddress = user.extraAddress,
        detailAddress = user.detailAddress
    )
}