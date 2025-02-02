package com.redbox.domain.community.notice.service

import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.notice.dto.CreateNoticeRequest
import com.redbox.domain.community.notice.dto.NoticeResponse
import com.redbox.domain.community.notice.entity.Notice
import com.redbox.domain.community.notice.repository.NoticeRepository
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.infra.s3.S3Service
import com.redbox.global.util.FileUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class NoticeService(
    private val authenticationService: AuthenticationService,
    private val noticeRepository: NoticeRepository,
    private val s3Service: S3Service,
    private val redisTemplate: RedisTemplate<String, Object>,
) {
    companion object {
        private const val NOTICE_DETAIL_KEY = "notices:detail:%d"
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

    private fun deleteNoticeCaches(noticeId: Long) {
        //redisTemplate.delete(NoticeService.TOP5_NOTICES_KEY) // 공지사항 탑 5개의 글에 대한 캐시
        //redisTemplate.delete(String.format(NoticeService.NOTICE_PAGE_KEY, 1)) // 공지사항 첫페이지에 대한 캐시
        redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, noticeId)) // 공지사항 게시글에 대한 캐시
    }
}