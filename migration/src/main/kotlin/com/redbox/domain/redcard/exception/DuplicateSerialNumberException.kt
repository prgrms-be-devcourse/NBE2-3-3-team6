package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class DuplicateSerialNumberException : BusinessException(ErrorCode.DUPLICATE_SERIAL_NUMBER)
