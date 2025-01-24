package com.redbox.domain.redbox.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class NegativeQuantityException extends BusinessException {
    public NegativeQuantityException() {
        super(ErrorCode.INVALID_REDCARD_COUNT);
    }
}
