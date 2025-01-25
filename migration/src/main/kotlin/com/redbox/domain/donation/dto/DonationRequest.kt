package com.redbox.domain.donation.dto

data class DonationRequest(
    val receiveId: Long,
    val quantity: Int,
    val comment: String
)