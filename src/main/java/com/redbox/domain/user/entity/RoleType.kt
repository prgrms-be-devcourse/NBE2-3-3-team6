package com.redbox.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("관리자", "ROLE_ADMIN"),
    USER("일반 사용자", "ROLE_USER");

    private final String text;
    private final String fullRole;
}
