package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class InvalidDonationTypeException extends BusinessException {
    public InvalidDonationTypeException() {
        super(ErrorCode.NOT_FOUND_DONATION_TYPE);
    }
}
