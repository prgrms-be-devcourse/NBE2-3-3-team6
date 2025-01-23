package com.redbox.domain.auth.exception;

import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;

public class InvalidRefreshTokenException extends AuthException {
  public InvalidRefreshTokenException() {
    super(ErrorCode.INVALID_TOKEN);
  }
}
