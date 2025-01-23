package com.redbox.domain.auth.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;

public class ExpiredRefreshTokenException extends AuthException {
    public ExpiredRefreshTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
