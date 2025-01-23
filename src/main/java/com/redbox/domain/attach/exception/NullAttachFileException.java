package com.redbox.domain.attach.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class NullAttachFileException extends BusinessException {
    public NullAttachFileException() {
        super(ErrorCode.INVALID_ATTACHFILE);
    }
}
