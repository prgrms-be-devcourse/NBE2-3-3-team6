package com.redbox.domain.donation.config;

import com.redbox.domain.donation.application.DonationService;

import com.redbox.domain.donation.application.RequestDonationService;
import com.redbox.domain.donation.application.UserDonationService;
import com.redbox.domain.redbox.applicaction.RedboxService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class DonationServiceConfig {
    // Map 을 이용한 서비스 관리
    @Bean
    public Map<String, DonationService> donationServiceMap(
            UserDonationService userDonationService,
            RequestDonationService requestDonationService,
            RedboxService redboxService) {

        return Map.of(
                "redbox", redboxService,
                "user", userDonationService,
                "request", requestDonationService
        );
    }
}
