package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class RedcardNotBelongException : BusinessException(ErrorCode.NOT_BELONG_TO_REDCARD)

