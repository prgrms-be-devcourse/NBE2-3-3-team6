package com.redbox.domain.user.exception

import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode

class PasswordMismatchException : AuthException(ErrorCode.PASSWORD_INVALID)