package com.redbox.domain.community.funding.entity

enum class FundingStatus(val text: String) {
    REQUEST("요청"), APPROVE("승인"), REJECT("거절"), EXPIRED("만료"), IN_PROGRESS("진행중"), DROP("삭제");
}