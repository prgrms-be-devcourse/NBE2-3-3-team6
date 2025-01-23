package com.redbox.domain.request.controller;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.attach.service.AttachFileService;
import com.redbox.domain.request.dto.*;
import com.redbox.domain.request.application.RequestService;
import com.redbox.global.entity.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestController {

    private final AttachFileService attachFileService;
    private final RequestService requestService;

    // 게시글 등록 (조회수 증가 X)
    @PostMapping("/write/requests")
    public ResponseEntity<DetailResponse> requestWrite(
            @RequestPart("post") @Valid WriteRequest writeRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        DetailResponse detailResponse = requestService.createRequest(writeRequest, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
    }

    // 게시글 권한 확인
    @GetMapping("/write/requests")
    public ResponseEntity<Void> getWrite() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 요청 게시글 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<PageResponse<ListResponse>> getRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute RequestFilter request
            ) {
        PageResponse<ListResponse> response = requestService.getRequests(page, size, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 요청 게시글 상세 조회 (조회수 증가 O)
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<DetailResponse> viewRequestDetail(@PathVariable Long requestId) {
        DetailResponse detailResponse = requestService.viewRequest(requestId);
        return ResponseEntity.ok(detailResponse);
    }

    // 좋아요 처리 로직
    @PostMapping("/requests/{requestId}/like")
    public ResponseEntity<LikeResponse> requestLike(@PathVariable Long requestId) {
        requestService.likeRequest(requestId);
        LikeResponse likeResponse = new LikeResponse("처리되었습니다");
        return ResponseEntity.status(HttpStatus.OK).body(likeResponse);
    }

    // 내용 수정 (조회수 증가 X)
    @PutMapping("/requests/{requestId}")
    public ResponseEntity<DetailResponse> requestModify(
            @PathVariable Long requestId,
            @RequestBody @Valid WriteRequest writeRequest
    ) {
        DetailResponse detailResponse = requestService.modifyRequest(requestId, writeRequest);
        return ResponseEntity.ok(detailResponse);
    }

    // 수정한 내용 불러오기 (조회수 증가 X)
    @GetMapping("/requests/modify/{requestId}")
    public ResponseEntity<DetailResponse> requestModify(@PathVariable Long requestId) {
        requestService.modifyAuthorize(requestId);
        DetailResponse detailResponse = requestService.getRequestDetail(requestId);
        return ResponseEntity.ok(detailResponse);
    }

    // 게시글 삭제하기
    @DeleteMapping("/requests/{requestId}")
    public ResponseEntity<Void> requestDelete(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/requests/{requestId}/files/{fileId}")
    public ResponseEntity<String> downloadFile(
            @PathVariable Long requestId,
            @PathVariable Long fileId) {
        return ResponseEntity.ok(attachFileService.getFileDownloadUrl(requestId, fileId));
    }

    @PostMapping(value = "/requests/{requestId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachFileResponse> addFile(
            @PathVariable Long requestId,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(attachFileService.addFile(Category.REQUEST, requestId, file));
    }

    @DeleteMapping("/requests/{requestId}/files/{fileId}")
    public ResponseEntity<Void> removeFile(
            @PathVariable Long requestId,
            @PathVariable Long fileId) {
        attachFileService.removeFile(Category.REQUEST, requestId, fileId);
        return ResponseEntity.ok().build();
    }
}