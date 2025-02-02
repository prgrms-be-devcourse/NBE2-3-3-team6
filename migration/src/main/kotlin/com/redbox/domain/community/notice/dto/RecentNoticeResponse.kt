package com.redbox.domain.community.notice.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDate

@JsonDeserialize
data class RecentNoticeResponse(
    val noticeNo: Long,
    val title: String,
    val createdDate: LocalDate
)
