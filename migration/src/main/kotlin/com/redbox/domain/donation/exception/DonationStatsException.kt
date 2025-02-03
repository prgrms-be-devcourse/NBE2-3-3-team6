package com.redbox.domain.donation.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class DonationStatsException(errorCode: ErrorCode) : BusinessException(errorCode)