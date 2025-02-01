package com.redbox.domain.community.notice.controller

import com.redbox.domain.community.notice.dto.CreateNoticeRequest
import com.redbox.domain.community.notice.dto.NoticeResponse
import com.redbox.domain.community.notice.service.NoticeService
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
class NoticeController(
    private val noticeService: NoticeService,
) {
    // 공지사항 등록
    @PostMapping(value = ["/notices"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createNotice(
        @RequestPart(value = "request") request: @Valid CreateNoticeRequest,
        @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?
    ): ResponseEntity<NoticeResponse> {
        val response: NoticeResponse = noticeService.createNotice(request, files)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.noticeNo)
            .toUri()

        return ResponseEntity.created(location).body<NoticeResponse>(response)
    }
}