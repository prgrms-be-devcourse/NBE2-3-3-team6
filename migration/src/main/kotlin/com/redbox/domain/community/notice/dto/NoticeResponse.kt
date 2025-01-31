package com.redbox.domain.community.notice.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.redbox.domain.community.attach.dto.AttachFileResponse
import com.redbox.domain.community.notice.entity.Notice
import java.time.LocalDate

data class NoticeResponse(
    val noticeNo: Long,
    val title: String,
    val content: String,
    val createdDate: LocalDate,
    val writer: String,
    val views: Int,
    val attachFileResponses: MutableList<AttachFileResponse>
) {
    companion object {
        @JsonCreator
        fun fromNotice(notice: Notice): NoticeResponse {
            return NoticeResponse(
                noticeNo = notice.id ?: throw IllegalStateException("Notice not found"),
                title = notice.noticeTitle ?: "No Title",
                content = notice.noticeContent ?: "No Content",
                createdDate = notice.createdAt?.toLocalDate() ?: LocalDate.now(),
                writer = notice.user?.name ?: throw IllegalStateException("User not found"),
                views = notice.noticeHits ?: 0,
                attachFileResponses = notice.attachFiles.map { attachFile -> AttachFileResponse(attachFile) }.toMutableList()
            )
        }
    }
}