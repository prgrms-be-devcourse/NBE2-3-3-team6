package com.redbox.domain.donation.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DonationStatsException extends BusinessException {
    public DonationStatsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
