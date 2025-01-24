package com.redbox.domain.attach.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class FileNotBelongException extends BusinessException {
    public FileNotBelongException() {
        super(ErrorCode.NOT_BELONG_TO_FILE);
    }
}

