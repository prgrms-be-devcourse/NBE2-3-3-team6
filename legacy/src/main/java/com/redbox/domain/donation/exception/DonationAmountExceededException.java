package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationAmountExceededException extends BusinessException {
    public DonationAmountExceededException() {
        super(ErrorCode.INVALID_DONATION_AMOUNT);
    }
}
