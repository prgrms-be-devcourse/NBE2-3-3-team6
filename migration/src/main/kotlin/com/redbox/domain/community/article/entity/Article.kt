package com.redbox.domain.community.article.entity

import com.redbox.domain.community.article.dto.UpdateArticleRequest
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "articles")
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    subject: String,
    articleUrl: String,
    source: String
) : BaseEntity() {
    var subject: String? = subject
        protected set

    var articleUrl: String? = articleUrl
        protected set

    var source: String? = source
        protected set

    fun update(request: UpdateArticleRequest) {
        subject = request.subject
        articleUrl = request.subject
        source = request.source
    }
}