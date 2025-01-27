package com.redbox.domain.community.attach.strategy

import com.redbox.domain.community.attach.entity.AttachFile

interface FileAttachStrategy {
    fun attach(postId: Long?, originalFilename: String?, newFilename: String?): AttachFile
}
