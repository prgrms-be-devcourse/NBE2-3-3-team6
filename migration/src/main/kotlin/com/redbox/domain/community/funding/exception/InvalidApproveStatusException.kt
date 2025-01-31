package com.redbox.domain.community.funding.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class InvalidApproveStatusException: BusinessException(ErrorCode.FAIL_TO_APPROVAL_STATUS)