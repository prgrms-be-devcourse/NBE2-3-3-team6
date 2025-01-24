package com.redbox.global.exception

class BusinessException(
    val errorCodes: ErrorCode,
) : RuntimeException(errorCodes.message)