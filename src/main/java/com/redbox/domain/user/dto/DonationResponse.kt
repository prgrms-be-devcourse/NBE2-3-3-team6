package com.redbox.domain.user.dto

import com.redbox.domain.donation.entity.DonationGroup
import java.time.LocalDate

data class DonationResponse(
    val receiverName: String,
    val donationAmount: Int,
    val donationDate: LocalDate,
    val donationMessage: String
) {
    companion object {
        fun from(donationGroup: DonationGroup, receiverName: String) = DonationResponse(
            receiverName = receiverName,
            donationAmount = donationGroup.donationAmount,
            donationDate = donationGroup.donationDate,
            donationMessage = donationGroup.donationMessage
        )
    }
}