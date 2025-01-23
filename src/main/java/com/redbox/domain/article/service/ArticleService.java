package com.redbox.domain.article.service;

import com.redbox.domain.article.dto.ArticleResponse;
import com.redbox.domain.article.dto.CreateArticleRequest;
import com.redbox.domain.article.dto.UpdateArticleRequest;
import com.redbox.domain.article.entity.Article;
import com.redbox.domain.article.exception.ArticleNotFoundException;
import com.redbox.domain.article.repository.ArticleRepository;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final UserService userService;
    private final ArticleRepository articleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);       // 30분
    private static final String ARTICLE_PAGE_KEY = "article:page:%d";       //헌혈기사 1페이지 리스트

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> getArticles(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        // 첫페이지만 캐싱
        if (page == 1) {
            String cacheKey = String.format(ARTICLE_PAGE_KEY, page);
            try {
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return (PageResponse<ArticleResponse>) cached;
                }

                PageResponse<ArticleResponse> response = new PageResponse<>(articleRepository.findAll(pageable).map(ArticleResponse::new));
                redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL);
                return response;
            } catch (RedisConnectionException e) {
                log.error("Redis 연결 실패", e);
                return new PageResponse<>(articleRepository.findAll(pageable).map(ArticleResponse::new));
            }
        }

        // 첫페이지 외에는 DB로 가져옴
        return new PageResponse<>(articleRepository.findAll(pageable).map(ArticleResponse::new));
    }

    @Transactional
    public void createArticle(CreateArticleRequest request) {
        articleRepository.save(Article.builder()
                .userId(userService.getCurrentUserId())
                .subject(request.getSubject())
                .articleUrl(request.getUrl())
                .source(request.getSource())
                .build());

        redisTemplate.delete(String.format(ARTICLE_PAGE_KEY, 1));
    }

    @Transactional
    public void updateArticle(Long articleId, UpdateArticleRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        article.updateArticle(request);
        redisTemplate.delete(String.format(ARTICLE_PAGE_KEY, 1));
    }

    public ArticleResponse getArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        return new ArticleResponse(article);
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        articleRepository.delete(article);
        redisTemplate.delete(String.format(ARTICLE_PAGE_KEY, 1));
    }
}
