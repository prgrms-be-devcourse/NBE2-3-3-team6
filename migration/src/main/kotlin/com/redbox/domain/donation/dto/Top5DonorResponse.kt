package com.redbox.domain.donation.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class Top5DonorResponse(
    val rank: Long,
    val donorId: Long,
    val donorName: String,
    val totalAmount: Long
)
