package com.redbox.domain.admin.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class InvalidApproveStatusException extends BusinessException {
    public InvalidApproveStatusException() {
        super(ErrorCode.FAIL_TO_APPROVAL_STATUS);
    }
}
