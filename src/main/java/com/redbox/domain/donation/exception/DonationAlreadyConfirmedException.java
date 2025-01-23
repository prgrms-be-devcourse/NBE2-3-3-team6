package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationAlreadyConfirmedException extends BusinessException {
    public DonationAlreadyConfirmedException() {super(ErrorCode.DONATION_ALREADY_CONFIRMED);}
}

