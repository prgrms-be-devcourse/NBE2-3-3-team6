package com.redbox.domain.community.article.service

import com.redbox.domain.community.article.dto.ArticleResponse
import com.redbox.domain.community.article.dto.CreateArticleRequest
import com.redbox.domain.community.article.dto.UpdateArticleRequest
import com.redbox.domain.community.article.entity.Article
import com.redbox.domain.community.article.exception.ArticleNotFoundException
import com.redbox.domain.community.article.repository.ArticleRepository
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import io.lettuce.core.RedisConnectionException
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
class ArticleService(
    private val authenticationService: AuthenticationService,
    private val articleRepository: ArticleRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {

    companion object {
        private val CACHE_TTL: Duration = Duration.ofMinutes(30)
        private const val ARTICLE_PAGE_KEY = "article:page:%d"
    }

    @Transactional(readOnly = true)
    fun getArticles(page: Int, size: Int): PageResponse<ArticleResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending())

        if (page == 1) {
            val cacheKey = String.format(ARTICLE_PAGE_KEY, page)
            try {
                val cached = redisTemplate.opsForValue().get(cacheKey)
                //TODO: 여기 손좀 봐주십쇼..

//                if (cached is PageResponse<*>) {
//                    return cached as PageResponse<ArticleResponse>
//                }
//
//                val response = PageResponse(
//                    articleRepository.findAll(pageable).map { ArticleResponse.fromArticle(it) })
//
//                redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL)
//
//                return response
            } catch (e: RedisConnectionException) {
                log.error("Redis 연결 실패", e)
            }
        }

        return PageResponse(articleRepository.findAll(pageable).map { ArticleResponse.fromArticle(it) })
    }

    @Transactional
    fun createArticle(request: CreateArticleRequest) {
        articleRepository.save(Article(null, authenticationService.getCurrentUserId(), request.subject!!,  request.url!!, request.source!!))
//        //TODO: 기존 캐시 삭제
    //        redisTemplate.delete()
    }

    @Transactional
    fun updateArticle(articleId: Long, request: UpdateArticleRequest) {
        val article = articleRepository.findById(articleId).orElseThrow {ArticleNotFoundException()}
        article.update(request)
        //TODO: 기존 캐시 삭제
    }

    @Transactional
    fun deleteArticle(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow {ArticleNotFoundException()}

        articleRepository.delete(article)
        //TODO: 기존 캐시 삭제
    }
}