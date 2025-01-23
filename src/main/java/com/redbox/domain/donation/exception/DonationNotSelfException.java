package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationNotSelfException extends BusinessException {
    public DonationNotSelfException() {
        super(ErrorCode.DONATION_NOT_SELF);
    }
}
