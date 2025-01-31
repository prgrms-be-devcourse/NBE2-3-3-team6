package com.redbox.domain.user.user.entity

enum class RoleType(
    val text: String,
    val fullRole: String
) {
    ADMIN("관리자", "ROLE_ADMIN"),
    USER("일반 사용자", "ROLE_USER");
}