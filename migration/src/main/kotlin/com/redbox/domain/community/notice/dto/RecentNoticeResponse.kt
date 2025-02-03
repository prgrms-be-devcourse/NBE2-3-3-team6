package com.redbox.domain.community.notice.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDate

@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class RecentNoticeResponse(
    val noticeNo: Long,
    val title: String,
    val createdDate: LocalDate
)
