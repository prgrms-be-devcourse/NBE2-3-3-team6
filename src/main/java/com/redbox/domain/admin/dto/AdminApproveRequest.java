package com.redbox.domain.admin.dto;

import lombok.Getter;

@Getter
public class AdminApproveRequest {
    private String approveStatus; // 승인, 거절
}
