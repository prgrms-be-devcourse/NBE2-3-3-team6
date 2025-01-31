package com.redbox.global.auth.exception

import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode

class RefreshTokenNotFoundException : AuthException(ErrorCode.TOKEN_NOT_FOUND)
