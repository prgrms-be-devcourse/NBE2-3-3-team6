package com.redbox.domain.community.notice.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class NoticeNotFoundException : BusinessException(ErrorCode.FAIL_TO_FIND_NOTICE)
