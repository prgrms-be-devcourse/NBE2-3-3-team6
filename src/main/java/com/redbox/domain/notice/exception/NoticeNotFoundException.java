package com.redbox.domain.notice.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class NoticeNotFoundException extends BusinessException {
    public NoticeNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_NOTICE);
    }
}
