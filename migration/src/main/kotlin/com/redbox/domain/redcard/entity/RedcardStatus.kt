package com.redbox.domain.redcard.entity

enum class RedcardStatus(val description: String) {
    USED("사용 완료"),
    AVAILABLE("사용 가능"),
    PENDING("처리중")
}
