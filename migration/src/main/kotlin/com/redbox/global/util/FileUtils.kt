package com.redbox.global.util

import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FileUtils {
    companion object {
        fun generateNewFilename(): String {
            val now = LocalDateTime.now()
            val datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val uniquePart = UUID.randomUUID().toString().substring(0, 13).uppercase().replace("-", "")

            return String.format("%s_%s", datePart, uniquePart)
        }

        fun getExtension(file: MultipartFile): String {
            return StringUtils.getFilenameExtension(file.originalFilename ?: "") ?: ""
        }
    }
}