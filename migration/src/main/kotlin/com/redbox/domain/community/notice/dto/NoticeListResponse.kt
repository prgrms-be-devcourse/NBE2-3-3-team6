package com.redbox.domain.community.notice.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class NoticeListResponse(
    val noticeNo: Long,
    val title: String,
    val createdDate: LocalDate,
    val writer: String,
    val views: Int,
    val hasAttachFiles: Boolean
) {
    constructor(noticeNo: Long, title: String, createdDate: LocalDateTime, writer: String, views: Int, hasAttachFiles: Boolean) : this(
        noticeNo,
        title,
        createdDate.toLocalDate(),
        writer,
        views,
        hasAttachFiles
    )
}