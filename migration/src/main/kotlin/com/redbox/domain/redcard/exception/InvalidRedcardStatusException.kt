package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class InvalidRedcardStatusException : BusinessException(ErrorCode.INVALID_REDCARD_STATUS)
