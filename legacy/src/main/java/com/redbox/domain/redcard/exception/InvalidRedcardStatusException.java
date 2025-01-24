package com.redbox.domain.redcard.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class InvalidRedcardStatusException extends BusinessException {
    public InvalidRedcardStatusException() {
        super(ErrorCode.INVALID_REDCARD_STATUS);
    }
}
