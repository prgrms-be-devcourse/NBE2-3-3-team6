package com.redbox.domain.auth.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;

public class ExpiredAccessTokenException extends AuthException {
    public ExpiredAccessTokenException() {
        super(ErrorCode.EXPIRED_TOKEN);
    }
}
