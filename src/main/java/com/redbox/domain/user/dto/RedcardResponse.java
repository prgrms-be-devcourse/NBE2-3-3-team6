package com.redbox.domain.user.dto;

import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RedcardResponse {

    private final Long id;
    private final LocalDate donationDate;
    private final String cardNumber;
    private final String hospitalName;
    private final LocalDate registrationDate;
    private final String status;

    public RedcardResponse(Redcard redcard) {
        this.id = redcard.getId();
        this.donationDate = redcard.getDonationDate();
        this.cardNumber = redcard.getSerialNumber();
        this.hospitalName = redcard.getHospitalName();
        this.registrationDate = redcard.getCreatedAt().toLocalDate();
        this.status = redcard.getRedcardStatus().equals(RedcardStatus.AVAILABLE) ?
                "available" : redcard.getRedcardStatus().equals(RedcardStatus.USED) ? "used" : "pending";
    }
}
