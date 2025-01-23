package com.redbox.domain.article.entity

import com.redbox.domain.article.dto.UpdateArticleRequest
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "$articles")
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    var id: Long? = null,

    @Column(name = "user_id")
    var userId: Long,

    var subject: String,
    var articleUrl: String,
    var source: String
) : BaseEntity() {

    fun updateArticle(request: UpdateArticleRequest) {
        this.subject = request.subject
        this.articleUrl = request.url
        this.source = request.source
    }
}
