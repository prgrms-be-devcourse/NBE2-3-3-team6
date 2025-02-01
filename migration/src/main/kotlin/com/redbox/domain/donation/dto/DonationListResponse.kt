package com.redbox.domain.donation.dto

import java.time.LocalDate

interface DonationListResponse{
    val receiverName: String
    val donationAmount: Int
    val donationDate: LocalDate
    val donationMessage: String
}