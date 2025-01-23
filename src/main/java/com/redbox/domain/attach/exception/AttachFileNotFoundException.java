package com.redbox.domain.attach.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class AttachFileNotFoundException extends BusinessException {
    public AttachFileNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_ATTACHFILE);
    }
}
