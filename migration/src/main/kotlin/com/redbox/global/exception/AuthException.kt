package com.redbox.global.exception

open class AuthException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)

