package com.redbox.global.util.email

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class EmailCreationException : BusinessException(ErrorCode.FAIL_TO_CREATE_EMAIL)