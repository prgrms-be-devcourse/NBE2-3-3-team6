package com.redbox.global.exception

class AuthException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)

