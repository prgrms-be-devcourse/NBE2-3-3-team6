package com.redbox.domain.community.funding.dto

import java.time.LocalDate

interface AdminListResponse {
    val id: Long
    val title: String
    val author: String
    val date: LocalDate
    val status: String
}
