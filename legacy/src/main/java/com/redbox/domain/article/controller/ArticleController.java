package com.redbox.domain.article.controller;

import com.redbox.domain.article.dto.ArticleResponse;
import com.redbox.domain.article.dto.CreateArticleRequest;
import com.redbox.domain.article.dto.UpdateArticleRequest;
import com.redbox.domain.article.service.ArticleService;
import com.redbox.global.entity.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public ResponseEntity<PageResponse<ArticleResponse>> getArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.getArticles(page, size));
    }

    @PostMapping("/articles")
    public ResponseEntity<Void> createArticle(@RequestBody @Valid CreateArticleRequest request) {
        articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/articles/{articleId}")
    public ResponseEntity<Void> updateArticle(@PathVariable Long articleId, @RequestBody @Valid UpdateArticleRequest request) {
        articleService.updateArticle(articleId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/articles/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long articleId) {
        return ResponseEntity.ok(articleService.getArticle(articleId));
    }

    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.ok().build();
    }
}
