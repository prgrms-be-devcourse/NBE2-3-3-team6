package com.redbox.domain.admin.controller;

import com.redbox.domain.admin.application.AdminService;
import com.redbox.domain.admin.dto.AdminApproveRequest;
import com.redbox.domain.admin.dto.AdminDetailResponse;
import com.redbox.domain.admin.dto.AdminListResponse;
import com.redbox.domain.admin.dto.AdminStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 요청 게시글 리스트 조회
    @GetMapping("/admin/fundings")
    public ResponseEntity<List<AdminListResponse>> getFundings() {
        List<AdminListResponse> response = adminService.getFundings();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 요청 게시글 승인
    @PostMapping("/admin/fundings/{requestId}")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long fundingId,
            @RequestBody AdminApproveRequest request
    ) {
        adminService.approveRequest(fundingId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 요청 게시글 상세조회(게시글 상세조회와 동일)
    @GetMapping("/admin/fundings/{fundingId}")
    public ResponseEntity<AdminDetailResponse> detailFunding(@PathVariable Long fundingId) {
        AdminDetailResponse detailResponse = adminService.getFundingDetails(fundingId);
        return ResponseEntity.ok(detailResponse);
    }

    @GetMapping("/admin/hot")
    public ResponseEntity<List<AdminListResponse>> getHotFundings() {
        return ResponseEntity.ok(adminService.getHotFundings());
    }

    @GetMapping("/admin/like")
    public ResponseEntity<List<AdminListResponse>> getLikedFundings() {
        return ResponseEntity.ok(adminService.getLikedFundings());
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }
}
