package com.redbox.domain.community.notice.service

import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.notice.dto.CreateNoticeRequest
import com.redbox.domain.community.notice.dto.NoticeListResponse
import com.redbox.domain.community.notice.dto.NoticeResponse
import com.redbox.domain.community.notice.entity.Notice
import com.redbox.domain.community.notice.exception.NoticeNotFoundException
import com.redbox.domain.community.notice.repository.NoticeQueryRepository
import com.redbox.domain.community.notice.repository.NoticeRepository
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import com.redbox.global.infra.s3.S3Service
import com.redbox.global.util.FileUtils
import io.lettuce.core.RedisConnectionException
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Duration

@Service
class NoticeService(
    private val authenticationService: AuthenticationService,
    private val noticeRepository: NoticeRepository,
    private val s3Service: S3Service,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val noticeQueryRepository: NoticeQueryRepository
) {
    companion object {
        private val CACHE_TTL: Duration = Duration.ofMinutes(30)
        private const val NOTICE_DETAIL_KEY = "notices:detail:%d"
        private const val NOTICE_PAGE_KEY = "notices:page:%d"
        private const val NOTICE_HIT_KEY = "notices:hit:%d"
    }

    private fun deleteNoticeCaches(noticeId: Long) {
        //redisTemplate.delete(NoticeService.TOP5_NOTICES_KEY) // 공지사항 탑 5개의 글에 대한 캐시
        //redisTemplate.delete(String.format(NoticeService.NOTICE_PAGE_KEY, 1)) // 공지사항 첫페이지에 대한 캐시
        redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, noticeId)) // 공지사항 게시글에 대한 캐시
    }

    private fun incrementHitCount(noticeId: Long) {
        val hitKey = String.format(NOTICE_HIT_KEY, noticeId)
        redisTemplate.opsForValue().increment(hitKey, 1)
    }

    private fun handleAttachFiles(notice: Notice, files: MutableList<MultipartFile>?) {
        if (files != null && !files.isEmpty()) {
            for (file in files) {
                val newFilename = FileUtils.generateNewFilename()
                val extension = FileUtils.getExtension(file)
                val fullFilename = "$newFilename.$extension"
                s3Service.uploadFile(file, Category.NOTICE, notice.id!!, fullFilename)

                var attachFile = AttachFile(
                    category = Category.NOTICE,
                    notice = notice,
                    originalFilename = requireNotNull(file.originalFilename),
                    newFilename = fullFilename,
                )
                notice.addAttachFiles(attachFile)
            }
        }
    }

    @Transactional(readOnly = true)
    fun getNotices(page: Int, size: Int): PageResponse<NoticeListResponse> {
        // 첫페이지만 캐싱
        if (page == 1) {
            val cacheKey = String.format(NOTICE_PAGE_KEY, page)
            try {
                val cached = redisTemplate.opsForValue().get(cacheKey)
                if (cached != null) {
                    return cached as PageResponse<NoticeListResponse>
                }

                val response = PageResponse(
                    noticeQueryRepository.findNotices(PageRequest.of(page - 1, size))
                )
                redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL)
                return response
            } catch (e: RedisConnectionException) {
                log.error("Redis 연결 실패", e)
                return PageResponse(noticeQueryRepository.findNotices(PageRequest.of(page - 1, size)))
            }
        }

        // 첫페이지 외에는 DB로 가져옴
        return PageResponse(noticeQueryRepository.findNotices(PageRequest.of(page - 1, size)))
    }

    // 공지사항 상세 조회
    @Transactional
    fun getNotice(noticeId: Long): NoticeResponse {
        // 조회수 증가 (별도 메서드 호출)
        incrementHitCount(noticeId)

        // 캐시된 상세 정보 조회
        val detailKey = NOTICE_DETAIL_KEY.format(noticeId)

        return try {
            val cached = redisTemplate.opsForValue().get(detailKey)
            if (cached is NoticeResponse) {
                return cached
            }

            val notice = noticeRepository.findForDetail(noticeId).orElseThrow { NoticeNotFoundException() }
            val response = NoticeResponse.fromNotice(notice)

            // 조회된 공지사항을 5분 동안 캐시
            redisTemplate.opsForValue().set(detailKey, response, CACHE_TTL)
            response

        } catch (e: RedisConnectionFailureException) {
            log.error("Redis 연결 실패", e)
            val notice = noticeRepository.findForDetail(noticeId).orElseThrow { NoticeNotFoundException() }
            NoticeResponse.fromNotice(notice)
        }
    }

    @Transactional
    fun createNotice(request: CreateNoticeRequest, files: MutableList<MultipartFile>?): NoticeResponse {
        val notice = Notice(
            user = authenticationService.getCurrentUser(),
            noticeTitle = request.title ?: "No title",
            noticeContent = request.content ?: "No content",
        )

        noticeRepository.save(notice)
        handleAttachFiles(notice, files)

        deleteNoticeCaches(notice.id ?: 0L)
        return NoticeResponse.fromNotice(notice)
    }
}