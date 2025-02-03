package com.redbox.domain.dashboard.dto

import com.redbox.domain.user.user.entity.Gender
import java.time.LocalDate

data class UserInfo(
    val name: String,
    val birth: LocalDate?,
    val gender: Gender,
    val phoneNumber: String
)