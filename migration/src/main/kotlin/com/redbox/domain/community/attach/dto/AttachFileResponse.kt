package com.redbox.domain.community.attach.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.redbox.domain.community.attach.entity.AttachFile

@JsonDeserialize
data class AttachFileResponse @JsonCreator constructor(
    var fileNo: Long? = null,
    var originFilename: String? = null,
    var filename: String? = null
) {
    constructor(file: AttachFile) : this(
        fileNo = file.id,
        originFilename = file.originalFilename,
        filename = file.newFilename
    )
}
