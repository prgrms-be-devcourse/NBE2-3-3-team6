package com.redbox.domain.community.attach.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class NullAttachFileException : BusinessException(ErrorCode.INVALID_ATTACHFILE)
