package com.redbox.domain.attach.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    REQUEST("요청게시판","request"), NOTICE("공지사항","notice");

    private final String text;
    private final String path; // s3 디렉토리 구조에 사용
}
