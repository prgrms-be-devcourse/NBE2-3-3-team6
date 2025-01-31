package com.redbox.domain.user.admin.controller

import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.user.admin.service.AdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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
}