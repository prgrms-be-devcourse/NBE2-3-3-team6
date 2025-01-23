package com.redbox.domain.user.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class EmptyPasswordException extends BusinessException {
    public EmptyPasswordException() {
        super(ErrorCode.EMPTY_PASSWORD);
    }
}