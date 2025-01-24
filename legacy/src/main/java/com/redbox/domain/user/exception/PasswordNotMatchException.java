package com.redbox.domain.user.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class PasswordNotMatchException extends BusinessException {
    public PasswordNotMatchException() {
        super(ErrorCode.NOT_MATCH_PASSWORD);
    }
}
