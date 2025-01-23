package com.redbox.domain.funding.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class UnauthorizedAccessException : BusinessException(ErrorCode.FAIL_TO_ACCESS)
