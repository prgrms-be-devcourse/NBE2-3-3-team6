package com.redbox.domain.community.notice.dto

import jakarta.validation.constraints.NotBlank

data class CreateNoticeRequest(
    var title: @NotBlank(message = "제목을 입력해주세요.") String? = null,
    var content: @NotBlank(message = "내용을 입력해주세요.") String? = null
)