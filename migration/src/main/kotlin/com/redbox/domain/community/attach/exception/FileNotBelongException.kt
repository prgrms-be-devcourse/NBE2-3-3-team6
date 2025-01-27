package com.redbox.domain.community.attach.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class FileNotBelongException : BusinessException(ErrorCode.NOT_BELONG_TO_FILE)

