package com.redbox.domain.article.entity;

import com.redbox.domain.article.dto.UpdateArticleRequest;
import com.redbox.domain.user.entity.User;
import com.redbox.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "articles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String subject;
    private String articleUrl;
    private String source;

    @Builder
    public Article(Long userId, String subject, String articleUrl, String source) {
        this.userId = userId;
        this.subject = subject;
        this.articleUrl = articleUrl;
        this.source = source;
    }

    public void updateArticle(UpdateArticleRequest request) {
        this.subject = request.getSubject();
        this.articleUrl = request.getUrl();
        this.source = request.getSource();
    }
}
