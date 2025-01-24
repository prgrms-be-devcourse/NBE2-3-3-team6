package com.redbox.domain.user.dto

import com.redbox.domain.donation.entity.DonationGroup
import java.time.LocalDate

data class ReceptionResponse(
    val donorName: String,
    val donationAmount: Int,
    val donationDate: LocalDate,
    val donationMessage: String
) {
    constructor(donationGroup: DonationGroup, donorName: String) : this(
        donorName = donorName,
        donationAmount = donationGroup.donationAmount,
        donationDate = donationGroup.donationDate,
        donationMessage = donationGroup.donationMessage
    )
}