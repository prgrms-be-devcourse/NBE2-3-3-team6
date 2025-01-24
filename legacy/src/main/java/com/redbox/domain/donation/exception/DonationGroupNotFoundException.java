package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationGroupNotFoundException extends BusinessException {
    public DonationGroupNotFoundException() {
        super(ErrorCode.NOT_FOUND_DONATION_GROUP);
    }
}
