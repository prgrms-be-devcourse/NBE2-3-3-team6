package com.redbox.domain.donation.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class InvalidDonationTypeException() : BusinessException(ErrorCode.NOT_FOUND_DONATION_TYPE) {
}