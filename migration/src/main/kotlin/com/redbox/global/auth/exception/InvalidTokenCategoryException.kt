package com.redbox.global.auth.exception

import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode

class InvalidTokenCategoryException : AuthException(ErrorCode.INVALID_TOKEN_CATEGORY)
