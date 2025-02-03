package com.redbox.domain.community.attach.strategy

import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.notice.entity.Notice
import com.redbox.domain.community.notice.exception.NoticeNotFoundException
import com.redbox.domain.community.notice.repository.NoticeRepository
import org.springframework.stereotype.Component

@Component
class NoticeFileStrategy(
    private val noticeRepository: NoticeRepository
) : FileAttachStrategy {

    override fun attach(postId: Long?, originalFilename: String?, newFilename: String?): AttachFile {
        val id = requireNotNull(postId) { "postId cannot be null" }
        val notice: Notice = noticeRepository.findById(id)
            .orElseThrow { NoticeNotFoundException() }

        return AttachFile(
            category = Category.NOTICE,
            notice = notice,
            originalFilename = requireNotNull(originalFilename) { "originalFilename cannot be null" },
            newFilename = requireNotNull(newFilename) { "newFilename cannot be null" }

        )
    }
}
