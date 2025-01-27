package com.redbox.domain.user.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class PasswordNotMatchException: BusinessException(ErrorCode.NOT_MATCH_PASSWORD)