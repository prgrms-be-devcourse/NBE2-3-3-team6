package com.redbox.domain.notice.controller;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.attach.service.AttachFileService;
import com.redbox.domain.notice.dto.*;
import com.redbox.domain.notice.service.NoticeService;
import com.redbox.global.entity.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final AttachFileService attachFileService;

    @GetMapping("/notices")
    public ResponseEntity<PageResponse<NoticeListResponse>> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(noticeService.getNotices(page, size));
    }

    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNotice(noticeId));
    }

    @PostMapping(value = "/notices", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse> createNotice(
            @RequestPart(value = "request") @Valid CreateNoticeRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        NoticeResponse response = noticeService.createNotice(request, files);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getNoticeNo())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/notices/{noticeId}")
    public ResponseEntity<NoticeResponse> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid UpdateNoticeRequest request) {
        return ResponseEntity
                .ok()
                .body(noticeService.updateNotice(noticeId, request));
    }

    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notices/{noticeId}/files/{fileId}")
    public ResponseEntity<String> downloadFile(
            @PathVariable Long noticeId,
            @PathVariable Long fileId) {
        return ResponseEntity.ok(attachFileService.getFileDownloadUrl(noticeId, fileId));
    }

    @PostMapping(value = "/notices/{noticeId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachFileResponse> addFile(
            @PathVariable Long noticeId,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(attachFileService.addFile(Category.NOTICE, noticeId, file));
    }

    @DeleteMapping("/notices/{noticeId}/files/{fileId}")
    public ResponseEntity<Void> removeFile(
            @PathVariable Long noticeId,
            @PathVariable Long fileId) {
        attachFileService.removeFile(Category.NOTICE, noticeId, fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notices/top5")
    public ResponseEntity<NoticeListWrapper> getTop5Notices() {
        return ResponseEntity.ok(noticeService.getCachedTop5Notices());
    }
}
