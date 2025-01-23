package com.redbox.domain.redcard.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class PendingRedcardException extends BusinessException {
    public PendingRedcardException() {
        super(ErrorCode.PENDING_REDCARD);
    }
}
