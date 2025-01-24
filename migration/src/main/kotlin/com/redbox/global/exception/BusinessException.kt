package com.redbox.global.exception

open class BusinessException(
    val errorCodes: ErrorCode,
) : RuntimeException(errorCodes.message)