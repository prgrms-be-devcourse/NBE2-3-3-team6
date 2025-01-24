package com.redbox.domain.redcard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedcardStatus {

    USED("사용 완료"), AVAILABLE("사용 가능"), PENDING("처리중");

    private final String text;
}
