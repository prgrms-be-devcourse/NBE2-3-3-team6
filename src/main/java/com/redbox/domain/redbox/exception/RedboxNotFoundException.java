package com.redbox.domain.redbox.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class RedboxNotFoundException extends BusinessException {
    public RedboxNotFoundException() {
        super(ErrorCode.REDBOX_NOT_FOUND);  // ErrorCode에 해당하는 항목을 추가해야 합니다.
    }
}
