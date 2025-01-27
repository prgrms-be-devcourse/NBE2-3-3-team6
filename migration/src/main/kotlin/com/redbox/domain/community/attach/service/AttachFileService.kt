package com.redbox.domain.community.attach.service

import com.redbox.domain.community.attach.dto.AttachFileResponse
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.attach.repository.AttachFileRepository
import com.redbox.domain.community.attach.strategy.FileAttachStrategy
import com.redbox.domain.community.attach.strategy.FileAttachStrategyFactory
import com.redbox.global.infra.s3.S3Service
import com.redbox.global.util.FileUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AttachFileService(
    private val s3Service: S3Service,
    private val fileAttachStrategyFactory: FileAttachStrategyFactory,
    private val attachFileRepository: AttachFileRepository,
) {
    @Transactional
    fun addFile(category: Category, postId: Long?, file: MultipartFile): AttachFileResponse {
        // S3에 파일 업로드
        val newFilename = FileUtils.generateNewFilename()
        val extension = FileUtils.getExtension(file)
        val fullFilename = "$newFilename.$extension"
        postId?.let { s3Service.uploadFile(file, category, it, fullFilename) }

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
}