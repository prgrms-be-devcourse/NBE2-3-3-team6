package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class PendingRedcardException : BusinessException(ErrorCode.PENDING_REDCARD)
