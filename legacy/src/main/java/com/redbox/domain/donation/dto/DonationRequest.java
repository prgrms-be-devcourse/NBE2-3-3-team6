package com.redbox.domain.donation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DonationRequest {
    private final Long receiveId;
    private final int quantity;
    private final String comment;

    @Builder
    public DonationRequest(Long receiveId, int quantity, String comment) {
        this.receiveId = receiveId;
        this.quantity = quantity;
        this.comment = comment;
    }
    
}
