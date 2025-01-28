package com.redbox.domain.redcard.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class NotEnoughRedCardException() : BusinessException(ErrorCode.INVALID_DONATION_AMOUNT){

}