package com.redbox.domain.community.funding.dto

import java.time.LocalDate

interface FundingListResponse {
    val fundingId: Long
    val fundingTitle: String
    val writer: String
    val createdDate: LocalDate
    val status: String
    val hits: Int
    val likes: Int
}