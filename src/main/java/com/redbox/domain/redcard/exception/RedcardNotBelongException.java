package com.redbox.domain.redcard.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class RedcardNotBelongException extends BusinessException {
    public RedcardNotBelongException() {
        super(ErrorCode.NOT_BELONG_TO_REDCARD);
    }
}

