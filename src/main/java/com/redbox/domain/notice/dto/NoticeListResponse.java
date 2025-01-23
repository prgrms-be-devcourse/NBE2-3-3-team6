package com.redbox.domain.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NoticeListResponse {

    private Long noticeNo;
    private String title;
    private LocalDate createdDate;
    private String writer;
    private int views;
    private boolean hasAttachFiles;

    public NoticeListResponse(Long noticeNo, String title, LocalDateTime createdDate, String writer, int views, boolean hasAttachFiles) {
        this.noticeNo = noticeNo;
        this.title = title;
        this.createdDate = createdDate.toLocalDate();
        this.writer = writer;
        this.views = views;
        this.hasAttachFiles = hasAttachFiles;
    }
}
