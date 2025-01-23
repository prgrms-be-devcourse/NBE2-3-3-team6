package com.redbox.domain.donation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.List;

@Getter
@JsonDeserialize
public class Top5DonorWrapper {

    private List<Top5DonorResponse> donors;

    @JsonCreator
    public Top5DonorWrapper() {
    }

    public Top5DonorWrapper(List<Top5DonorResponse> donors) {
        this.donors = donors;
    }
}
