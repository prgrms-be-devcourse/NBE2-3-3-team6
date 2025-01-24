package com.redbox.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ACTIVE("활성"), INACTIVE("비활성");

    private final String text;
}
