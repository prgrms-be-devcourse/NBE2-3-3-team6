package com.redbox.domain.article.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.redbox.domain.article.entity.Article;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonDeserialize
public class ArticleResponse {

    private Long articleNo;
    private String subject;
    private String url;
    private String source;
    private LocalDate createdDate;

    @JsonCreator
    public ArticleResponse() {
    }

    public ArticleResponse(Article article) {
        this.articleNo = article.getId();
        this.subject = article.getSubject();
        this.url = article.getArticleUrl();
        this.source = article.getSource();
        this.createdDate = article.getCreatedAt().toLocalDate();
    }
}
