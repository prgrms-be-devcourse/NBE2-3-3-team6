package com.redbox.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonDeserialize
public class RecentNoticeResponse {

    private Long noticeNo;
    private String title;
    private LocalDate createdDate;

    @JsonCreator
    public RecentNoticeResponse() {
    }

    public RecentNoticeResponse(Long noticeNo, String title, LocalDate createdDate) {
        this.noticeNo = noticeNo;
        this.title = title;
        this.createdDate = createdDate;
    }
}
