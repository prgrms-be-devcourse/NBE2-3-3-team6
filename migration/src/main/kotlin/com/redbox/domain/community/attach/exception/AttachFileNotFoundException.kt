package com.redbox.domain.community.attach.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class AttachFileNotFoundException : BusinessException(ErrorCode.FAIL_TO_FIND_ATTACHFILE)
