package com.redbox.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.notice.entity.Notice;
import com.redbox.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@JsonDeserialize
public class NoticeResponse {

    private Long noticeNo;
    private String title;
    private String content;
    private LocalDate createdDate;
    private String writer;
    private int views;
    private List<AttachFileResponse> attachFileResponses;

    @JsonCreator
    public NoticeResponse() {
    }

    public NoticeResponse(Notice notice) {
        this.noticeNo = notice.getId();
        this.title = notice.getNoticeTitle();
        this.content = notice.getNoticeContent();
        this.createdDate = notice.getCreatedAt().toLocalDate();
        this.writer = Optional.ofNullable(notice.getUser())
                .map(User::getName)
                .orElse("Unknown");
        this.views = notice.getNoticeHits();
        this.attachFileResponses = notice.getAttachFiles()
                .stream().map(AttachFileResponse::new).toList();

    }
}
