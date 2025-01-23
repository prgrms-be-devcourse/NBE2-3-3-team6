package com.redbox.domain.request.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class RequestNotFoundException extends BusinessException {
    public RequestNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_REQUEST);
    }
}
