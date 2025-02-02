package com.redbox.domain.community.funding.dto

import com.redbox.domain.community.attach.dto.AttachFileResponse
import java.time.LocalDate

data class AdminDetailResponse(
    val id: Long,
    val title: String,
    val author: String,
    val date: LocalDate,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val targetAmount: Int,
    val status: String,
    val views: Int,
    val content: String,
    val attachFiles: List<AttachFileResponse>
)