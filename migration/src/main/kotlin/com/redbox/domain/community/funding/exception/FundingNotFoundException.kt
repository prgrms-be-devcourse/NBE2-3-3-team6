package com.redbox.domain.community.funding.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class FundingNotFoundException : BusinessException(ErrorCode.FAIL_TO_FIND_FUNDING)