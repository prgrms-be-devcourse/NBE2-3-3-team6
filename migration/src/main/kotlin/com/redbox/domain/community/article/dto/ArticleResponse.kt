package com.redbox.domain.community.article.dto

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.redbox.domain.community.article.entity.Article
import java.time.LocalDate

@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class ArticleResponse(
    val articleNo: Long,
    val subject: String,
    val url: String,
    val source: String,
    val createdDate: LocalDate
) {
    companion object {
        fun fromArticle(article: Article): ArticleResponse {
            return ArticleResponse(
                articleNo = article.id ?: throw IllegalStateException("Notice not found"),
                subject = article.subject ?: "No Title",
                url = article.articleUrl ?: "No Content",
                source = article.source ?: "No Source",
                createdDate = article.createdAt?.toLocalDate() ?: LocalDate.now(),
            )
        }
    }
}

