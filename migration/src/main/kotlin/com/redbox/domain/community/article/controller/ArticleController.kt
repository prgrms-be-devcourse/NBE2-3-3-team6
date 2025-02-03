package com.redbox.domain.community.article.controller

import com.redbox.domain.community.article.dto.ArticleResponse
import com.redbox.domain.community.article.dto.CreateArticleRequest
import com.redbox.domain.community.article.service.ArticleService
import com.redbox.global.entity.PageResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping("/articles")
    fun getArticles(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<ArticleResponse>> {
        return ResponseEntity.ok(articleService.getArticles(page, size))
    }

    @PostMapping("/articles")
    fun createArticle(
        @RequestBody request: @Valid CreateArticleRequest
    ): ResponseEntity<Void> {
        articleService.createArticle(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/articles/{articleId}")
    fun updateArticle(
        @RequestBody @Valid request: CreateArticleRequest
    ): ResponseEntity<Void> {
        articleService.updateArticle(articleId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/articles/{articleId}")
    fun getArticle(@PathVariable articleId: Long): ResponseEntity<ArticleResponse> {
        return ResponseEntity.ok(articleService.getArticle(articleId));
    }

    @DeleteMapping("/articles/{articleId}")
    fun deleteArticle(@PathVariable articleId : Long) : ResponseEntity<Void>
    {
        articleService.deleteArticle(articleId);
        return ResponseEntity.ok().build();
    }
}