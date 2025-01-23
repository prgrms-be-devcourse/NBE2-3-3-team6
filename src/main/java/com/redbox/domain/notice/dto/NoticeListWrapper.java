package com.redbox.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.List;

@Getter
@JsonDeserialize
public class NoticeListWrapper {
    private List<RecentNoticeResponse> notices;

    @JsonCreator
    public NoticeListWrapper() {
    }

    public NoticeListWrapper(List<RecentNoticeResponse> notices) {
        this.notices = notices;
    }
}
