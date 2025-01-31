package com.redbox.domain.user.admin.dto

data class AdminStatsResponse(
    val userCount: Int,
    val redcardCountInRedbox: Int,
    val sumDonation: Int,
    val requestCount: Int
)
