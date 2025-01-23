package com.redbox.domain.user.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class InvalidUserInfoException extends BusinessException {
    public InvalidUserInfoException() {
        super(ErrorCode.INVALID_USER_INFO); // 에러코드 활용
    }
}
