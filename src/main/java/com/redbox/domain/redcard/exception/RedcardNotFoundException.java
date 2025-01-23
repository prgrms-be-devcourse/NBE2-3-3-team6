package com.redbox.domain.redcard.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class RedcardNotFoundException extends BusinessException {
    public RedcardNotFoundException() {
        super(ErrorCode.NOT_FOUND_REDCARD);
    }
}
