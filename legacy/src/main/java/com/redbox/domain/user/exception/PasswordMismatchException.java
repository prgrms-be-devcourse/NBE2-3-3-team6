package com.redbox.domain.user.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class PasswordMismatchException extends AuthException {
    public PasswordMismatchException() {
        super(ErrorCode.PASSWORD_INVALID);
    }
}
