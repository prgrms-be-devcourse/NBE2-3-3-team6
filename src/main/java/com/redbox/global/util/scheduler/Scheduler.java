package com.redbox.global.util.scheduler;

import com.redbox.domain.donation.application.RequestDonationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final RequestDonationService requestDonationService;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
    public void runBatchJob() {
        log.info("만료 게시글 업데이트(자정)");
        requestDonationService.updateExpiredRequests();
    }

    // 서버 실행시 적용(test)
    @PostConstruct
    public void runOnStartup() {
        log.info("만료 게시글 업데이트");
        requestDonationService.updateExpiredRequests();
    }

}
