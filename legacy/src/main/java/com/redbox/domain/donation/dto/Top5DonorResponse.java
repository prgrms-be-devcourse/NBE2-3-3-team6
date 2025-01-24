package com.redbox.domain.donation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

@Getter
@JsonDeserialize
public class Top5DonorResponse {

    private Long rank;
    private Long donorId;
    private String donorName;
    private Long totalAmount;

    @JsonCreator
    public Top5DonorResponse() {
    }

    public Top5DonorResponse(Long rank, Long donorId, String donorName, Long totalAmount) {
        this.rank = rank;
        this.donorId = donorId;
        this.donorName = donorName;
        this.totalAmount = totalAmount;
    }
}
