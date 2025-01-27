package com.redbox.domain.community.attach.service

import com.redbox.domain.community.attach.dto.AttachFileResponse
import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.attach.exception.AttachFileNotFoundException
import com.redbox.domain.community.attach.exception.FileNotBelongException
import com.redbox.domain.community.attach.repository.AttachFileRepository
import com.redbox.domain.community.attach.strategy.FileAttachStrategy
import com.redbox.domain.community.attach.strategy.FileAttachStrategyFactory
import com.redbox.global.infra.s3.S3Service
import com.redbox.global.util.FileUtils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AttachFileService(
    private val s3Service: S3Service,
    private val fileAttachStrategyFactory: FileAttachStrategyFactory,
    private val attachFileRepository: AttachFileRepository,
    private val redisTemplate: RedisTemplate<String, Object>,
) {
    companion object {
        private const val NOTICE_DETAIL_KEY = "notices:detail:%d"
    }

    // TODO : (수정) 파일 있는데, null 반환
    fun getFileDownloadUrl(postId: Long, fileId: Long): String {

        val attachFile = attachFileRepository.findById(fileId)
            .filter{it.funding?.fundingId == postId}
            .orElseThrow { AttachFileNotFoundException() }

        validateFileOwnership(attachFile, postId)

        // PreSignedURL 생성
        return s3Service.generatePresignedUrl(
            attachFile.category,
            postId,
            attachFile.newFilename,
            attachFile.originalFilename
        )
    }

    private fun validateFileOwnership(attachFile: AttachFile, postId: Long) {
        if (!attachFile.belongToPost(postId)) {
            throw FileNotBelongException()
        }
    }

    @Transactional
    fun addFile(category: Category, postId: Long, file: MultipartFile): AttachFileResponse {
        // S3에 파일 업로드
        val newFilename = FileUtils.generateNewFilename()
        val extension = FileUtils.getExtension(file)
        val fullFilename = "$newFilename.$extension"
        s3Service.uploadFile(file, category, postId, fullFilename)

        // 카테고리에 맞는 전략 사용
        val strategy: FileAttachStrategy? = fileAttachStrategyFactory.getStrategy(category)
        val attachFile = strategy?.attach(postId, file.originalFilename, fullFilename)
            ?: throw IllegalArgumentException("No strategy found for category: $category")
        attachFileRepository.save(attachFile)

        /*if (category.equals(Category.NOTICE)) {
            redisTemplate.delete(String.format(AttachFileService.NOTICE_DETAIL_KEY, postId))
        }*/

        return AttachFileResponse(attachFile)
    }

    @Transactional
    fun removeFile(category: Category, postId: Long, fileId: Long) {
        val attachFile = attachFileRepository.findById(fileId)
            .orElseThrow { AttachFileNotFoundException() } ?: throw FileNotBelongException()

        validateFileOwnership(attachFile, postId)
        s3Service.deleteFile(category, postId, attachFile.newFilename)

        if (category.equals(Category.NOTICE)) {
            redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, postId))
        }

        attachFileRepository.delete(attachFile)
    }
}