package com.redbox.domain.community.notice.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class NoticeListWrapper(
    val notices: List<RecentNoticeResponse>
) {
    @JsonCreator
    constructor() : this(emptyList())
}
