package com.redbox.domain.request.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestStatus {
    REQUEST("요청"), APPROVE("승인"), REJECT("거절"), EXPIRED("만료"), IN_PROGRESS("진행중"), DROP("삭제");
    private final String text;
}
