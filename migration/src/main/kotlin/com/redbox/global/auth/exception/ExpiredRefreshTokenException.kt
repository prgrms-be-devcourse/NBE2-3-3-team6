package com.redbox.global.auth.exception

import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode

class ExpiredRefreshTokenException : AuthException(ErrorCode.EXPIRED_TOKEN)
