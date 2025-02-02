package com.redbox.domain.community.notice.controller

import com.redbox.domain.community.attach.service.AttachFileService
import com.redbox.domain.community.notice.dto.*
import com.redbox.domain.community.notice.service.NoticeService
import com.redbox.global.entity.PageResponse
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
class NoticeController(
    private val noticeService: NoticeService,
    private val attachFileService: AttachFileService,
) {
    // 공지사항 목록 조회
    @GetMapping("/notices")
    fun getNotices(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<NoticeListResponse>> {
        return ResponseEntity.ok(noticeService.getNotices(page, size))
    }

    // 공지사항 상세 조회
    @GetMapping("/notices/{noticeId}")
    fun getNotice(@PathVariable noticeId: Long): ResponseEntity<NoticeResponse> {
        return ResponseEntity.ok(noticeService.getNotice(noticeId))
    }

    // 공지사항 등록
    @PostMapping(value = ["/notices"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createNotice(
        @RequestPart(value = "request") request: @Valid CreateNoticeRequest,
        @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>
    ): ResponseEntity<NoticeResponse> {
        val response: NoticeResponse = noticeService.createNotice(request, files)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.noticeNo)
            .toUri()

        return ResponseEntity.created(location).body(response)
    }

    // 공지사항 수정
    @PutMapping("/notices/{noticeId}")
    fun updateNotice(
        @PathVariable noticeId: Long,
        @RequestBody request: @Valid UpdateNoticeRequest
    ): ResponseEntity<NoticeResponse?>? {
        return ResponseEntity.ok().body(noticeService.updateNotice(noticeId, request))
    }

    // 공지사항 top 5
    @GetMapping("/notices/top5")
    fun getTop5Notices(): ResponseEntity<NoticeListWrapper> {
        return ResponseEntity.ok(noticeService.getCachedTop5Notices())
    }
}