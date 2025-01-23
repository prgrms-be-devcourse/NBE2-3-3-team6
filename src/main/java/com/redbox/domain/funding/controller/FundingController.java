package com.redbox.domain.funding.controller;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.attach.service.AttachFileService;
import com.redbox.domain.funding.dto.*;
import com.redbox.domain.funding.application.FundingService;
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
public class FundingController {

    private final AttachFileService attachFileService;
    private final FundingService fundingService;

    // 게시글 등록 (조회수 증가 X)
    @PostMapping("/write/fundings")
    public ResponseEntity<DetailResponse> fundingWrite(
            @RequestPart("post") @Valid WriteFunding writeFunding,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        DetailResponse detailResponse = fundingService.createFunding(writeFunding, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(detailResponse);
    }

    // 게시글 권한 확인
    @GetMapping("/write/fundings")
    public ResponseEntity<Void> getWrite() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 요청 게시글 목록 조회
    @GetMapping("/fundings")
    public ResponseEntity<PageResponse<ListResponse>> getFundings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FundingFilter funding
            ) {
        PageResponse<ListResponse> response = fundingService.getFundings(page, size, funding);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 요청 게시글 상세 조회 (조회수 증가 O)
    @GetMapping("/fundings/{fundingId}")
    public ResponseEntity<DetailResponse> viewFundingDetail(@PathVariable Long fundingId) {
        DetailResponse detailResponse = fundingService.viewFunding(fundingId);
        return ResponseEntity.ok(detailResponse);
    }

    // 좋아요 처리 로직
    @PostMapping("/fundings/{fundingId}/like")
    public ResponseEntity<LikeResponse> requestLike(@PathVariable Long fundingId) {
        fundingService.likeRequest(fundingId);
        LikeResponse likeResponse = new LikeResponse("처리되었습니다");
        return ResponseEntity.status(HttpStatus.OK).body(likeResponse);
    }

    // 내용 수정 (조회수 증가 X)
    @PutMapping("/fundings/{fundingId}")
    public ResponseEntity<DetailResponse> fundingModify(
            @PathVariable Long fundingId,
            @RequestBody @Valid WriteFunding writeFunding
    ) {
        DetailResponse detailResponse = fundingService.modifyFunding(fundingId, writeFunding);
        return ResponseEntity.ok(detailResponse);
    }

    // 수정한 내용 불러오기 (조회수 증가 X)
    @GetMapping("/fundings/modify/{fundingId}")
    public ResponseEntity<DetailResponse> fundingModify(@PathVariable Long fundingId) {
        fundingService.modifyAuthorize(fundingId);
        DetailResponse detailResponse = fundingService.getFundingDetail(fundingId);
        return ResponseEntity.ok(detailResponse);
    }

    // 게시글 삭제하기
    @DeleteMapping("/fundings/{fundingId}")
    public ResponseEntity<Void> fundingDelete(@PathVariable Long fundingId) {
        fundingService.deleteFunding(fundingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fundings/{fundingId}/files/{fileId}")
    public ResponseEntity<String> downloadFile(
            @PathVariable Long fundingId,
            @PathVariable Long fileId) {
        return ResponseEntity.ok(attachFileService.getFileDownloadUrl(fundingId, fileId));
    }

    @PostMapping(value = "/fundings/{fundingId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AttachFileResponse> addFile(
            @PathVariable Long fundingId,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return ResponseEntity.ok(attachFileService.addFile(Category.FUNDING, fundingId, file));
    }

    @DeleteMapping("/fundings/{fundingId}/files/{fileId}")
    public ResponseEntity<Void> removeFile(
            @PathVariable Long fundingId,
            @PathVariable Long fileId) {
        attachFileService.removeFile(Category.FUNDING, fundingId, fileId);
        return ResponseEntity.ok().build();
    }
}