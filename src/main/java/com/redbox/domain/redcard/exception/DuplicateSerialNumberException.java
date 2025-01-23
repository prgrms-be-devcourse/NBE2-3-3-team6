package com.redbox.domain.redcard.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class DuplicateSerialNumberException extends BusinessException {
    public DuplicateSerialNumberException() {
        super(ErrorCode.DUPLICATE_SERIAL_NUMBER);
    }
}
