package com.redbox.domain.community.notice.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class NoticeListWrapper(
    val notices: List<RecentNoticeResponse>
) {
    @JsonCreator
    constructor() : this(emptyList())
}
