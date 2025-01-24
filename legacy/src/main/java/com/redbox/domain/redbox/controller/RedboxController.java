package com.redbox.domain.redbox.controller;

import com.redbox.domain.redbox.dto.RedboxStatsResponse;
import com.redbox.domain.redbox.applicaction.RedboxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class RedboxController {

    private final RedboxService redboxService;

    // 레드박스 통계 조회
    @GetMapping("/redbox/stats")
    public ResponseEntity<RedboxStatsResponse> getRedboxStats() {
        RedboxStatsResponse response = redboxService.getRedboxStats();
        return ResponseEntity.ok(response);
    }

}
