package com.redbox.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminStatsResponse {

    private int userCount;
    private int redcardCountInRedbox;
    private int sumDonation;
    private int requestCount;
}
