package com.redbox.domain.user.dto

import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import java.time.LocalDate

data class RedcardResponse(
    val id: Long,
    val donationDate: LocalDate,
    val cardNumber: String,
    val hospitalName: String,
    val registrationDate: LocalDate,
    val status: String
) {
    constructor(redcard: Redcard) : this(
        id = redcard.id,
        donationDate = redcard.donationDate,
        cardNumber = redcard.serialNumber,
        hospitalName = redcard.hospitalName,
        registrationDate = redcard.createdAt.toLocalDate(),
        status = when (redcard.redcardStatus!!) {
            RedcardStatus.AVAILABLE -> "available"
            RedcardStatus.USED -> "used"
            RedcardStatus.PENDING -> "pending"
        }
    )
}
