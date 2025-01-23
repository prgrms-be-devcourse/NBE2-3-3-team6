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
    @GetMapping("/admin/requests")
    public ResponseEntity<List<AdminListResponse>> getRequests() {
        List<AdminListResponse> response = adminService.getRequests();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 요청 게시글 승인
    @PostMapping("/admin/requests/{requestId}")
    public ResponseEntity<Void> approveRequest(
            @PathVariable Long requestId,
            @RequestBody AdminApproveRequest request
    ) {
        adminService.approveRequest(requestId, request);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    // 요청 게시글 상세조회(게시글 상세조회와 동일)
    @GetMapping("/admin/requests/{requestId}")
    public ResponseEntity<AdminDetailResponse> detailRequest(@PathVariable Long requestId) {
        AdminDetailResponse detailResponse = adminService.getRequestDetails(requestId);
        return ResponseEntity.ok(detailResponse);
    }

    @GetMapping("/admin/hot")
    public ResponseEntity<List<AdminListResponse>> getHotBoards() {
        return ResponseEntity.ok(adminService.getHotBoards());
    }

    @GetMapping("/admin/like")
    public ResponseEntity<List<AdminListResponse>> getLikedBoards() {
        return ResponseEntity.ok(adminService.getLikedBoards());
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }
}
