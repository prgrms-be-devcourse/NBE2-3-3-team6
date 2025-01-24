package com.redbox.domain.auth.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;

public class TokenReissueFailedException extends AuthException {
    public TokenReissueFailedException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
