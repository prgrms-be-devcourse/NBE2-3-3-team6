package com.redbox.domain.community.article.dto

import jakarta.validation.constraints.NotBlank

data class CreateArticleRequest(
    @NotBlank(message = "제목을 입력해주세요.")
    val subject: String? = null,

    @NotBlank(message = "기사 URL을 입력해주세요.")
    val url: String? = null,

    @NotBlank(message = "출처를 입력해주세요.")
    val source: String? = null
) {
}

