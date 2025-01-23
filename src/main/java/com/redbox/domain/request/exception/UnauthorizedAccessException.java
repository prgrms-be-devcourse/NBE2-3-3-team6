package com.redbox.domain.request.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException() {
        super(ErrorCode.FAIL_TO_ACCESS);
    }
}
