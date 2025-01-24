package com.redbox.domain.redcard.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class RegisterRedcardRequest(
    @field:NotBlank(message = "헌혈증 번호를 입력해주세요.")
    val cardNumber: String,

    @field:NotNull(message = "헌혈일자를 입력해주세요.")
    val donationDate: LocalDate,

    @field:NotBlank(message = "헌혈장소를 입력해주세요.")
    val hospitalName: String
)