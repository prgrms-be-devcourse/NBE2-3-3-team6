package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationDuplicateException extends BusinessException {
    public DonationDuplicateException() {
        super(ErrorCode.DONATION_DUPLICATE);
    }
}
