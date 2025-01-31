package com.redbox.domain.user.admin.controller

import com.redbox.domain.community.funding.dto.AdminApproveRequest
import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.user.admin.service.AdminService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AdminController(
    private val adminService: AdminService
) {

    // 요청 게시글 리스트 조회
    @GetMapping("/admin/fundings")
    fun getFundings(): ResponseEntity<List<AdminListResponse>> {
        val response: List<AdminListResponse> = adminService.getFundings()
        return ResponseEntity.ok(response)
    }

    // 요청 게시글 승인
    // TODO auth 쪽 마무리 후 테스트 진행
    @PostMapping("/admin/fundings/{requestId}")
    fun approveRequest(
        @PathVariable fundingId: Long,
        @RequestBody request: AdminApproveRequest
    ): ResponseEntity<Void> {
        adminService.approveRequest(fundingId, request)
        return ResponseEntity.ok().build()
    }
}