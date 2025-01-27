package com.redbox.domain.community.attach.strategy

import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.community.attach.entity.Category
import com.redbox.domain.community.funding.exception.FundingNotFoundException
import com.redbox.domain.funding.repository.FundingRepository
import org.springframework.stereotype.Component

@Component
class FundingFileStrategy(
    private val fundingRepository: FundingRepository
) : FileAttachStrategy {

    override fun attach(postId: Long?, originalFilename: String?, newFilename: String?): AttachFile {
        val id = requireNotNull(postId) { "postId cannot be null" }

        val funding = fundingRepository.findById(id)
            .orElseThrow { FundingNotFoundException() }

        return AttachFile(
            category = Category.FUNDING,
            funding = funding,
            originalFilename = requireNotNull(originalFilename) { "originalFilename cannot be null" },
            newFilename = requireNotNull(newFilename) { "newFilename cannot be null" }
        )
    }
}