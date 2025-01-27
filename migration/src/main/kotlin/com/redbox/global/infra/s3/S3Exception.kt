package com.redbox.global.infra.s3

class S3Exception(message: String?, cause: Throwable?) : RuntimeException(message, cause)
