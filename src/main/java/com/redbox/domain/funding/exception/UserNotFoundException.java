package com.redbox.domain.funding.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_FUNDING);
    }
}
