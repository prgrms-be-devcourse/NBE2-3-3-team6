package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class RedcardNotFoundException : BusinessException(ErrorCode.NOT_FOUND_REDCARD)
