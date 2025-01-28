package com.redbox.domain.donation.dto

import java.time.LocalDate

interface ReceptionListResponse {
    val donorName: String
    val donationAmount: Int
    val donationDate: LocalDate
    val donationMessage: String
}