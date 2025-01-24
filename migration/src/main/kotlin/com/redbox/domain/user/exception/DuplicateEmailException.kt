package com.redbox.domain.user.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class DuplicateEmailException : BusinessException(ErrorCode.DUPLICATE_EMAIL)