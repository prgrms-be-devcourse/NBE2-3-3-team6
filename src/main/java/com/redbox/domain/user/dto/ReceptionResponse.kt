package com.redbox.domain.user.dto;

import com.redbox.domain.donation.entity.DonationGroup;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReceptionResponse {

    private final String donorName;
    private final int donationAmount;
    private final LocalDate donationDate;
    private final String donationMessage;

    public ReceptionResponse(DonationGroup donationGroup, String donorName) {
        this.donorName = donorName;
        this.donationAmount = donationGroup.getDonationAmount();
        this.donationDate = donationGroup.getDonationDate();
        this.donationMessage = donationGroup.getDonationMessage();
    }
}
