package com.redbox.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardResponse {
    private UserInfo userInfo;
    private DonationStats donationStats;
}
