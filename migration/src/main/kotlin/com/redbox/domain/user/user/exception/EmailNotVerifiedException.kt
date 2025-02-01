package com.redbox.domain.user.user.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class EmailNotVerifiedException: BusinessException(ErrorCode.UNVERIFIED_EMAIL)