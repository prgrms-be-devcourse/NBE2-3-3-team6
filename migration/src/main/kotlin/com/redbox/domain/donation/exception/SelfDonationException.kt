package com.redbox.domain.donation.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class SelfDonationException() : BusinessException(ErrorCode.DONATION_NOT_SELF) {


}