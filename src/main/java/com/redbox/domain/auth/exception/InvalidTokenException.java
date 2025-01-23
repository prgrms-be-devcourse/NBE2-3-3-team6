package com.redbox.domain.auth.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}