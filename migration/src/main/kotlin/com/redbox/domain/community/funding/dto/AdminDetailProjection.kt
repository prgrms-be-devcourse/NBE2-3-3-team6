package com.redbox.domain.community.funding.dto

import java.time.LocalDate

interface AdminDetailProjection {
    val id: Long
    val title: String
    val author: String
    val date: LocalDate
    val startDate: LocalDate
    val endDate: LocalDate
    val targetAmount: Int
    val status: String
    val views: Int
    val content: String
}
