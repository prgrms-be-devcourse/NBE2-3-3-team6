package com.redbox.domain.dashboard.controller

import com.redbox.domain.dashboard.dto.DashboardResponse
import com.redbox.domain.dashboard.service.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DashboardController(
    private val dashboardService: DashboardService
) {

    @GetMapping("/dashboard")
    fun getDashboardData(): ResponseEntity<DashboardResponse> {
        val response = dashboardService.getDashboardData()
        return ResponseEntity.ok(response)
    }
}
