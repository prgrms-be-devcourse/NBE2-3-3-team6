package com.redbox.domain.user.dto

import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.InvalidRedcardStatusException
import jakarta.validation.constraints.NotNull

data class UpdateRedcardStatusRequest(
    @field:NotNull
    val cardStatus: String
) {

    fun validateAndGetOppositeStatus(): RedcardStatus {
        if (cardStatus != STATUS_AVAILABLE && cardStatus != STATUS_USED) {
            throw InvalidRedcardStatusException()
        }

        return oppositeRedcardStatus()
    }

    private fun oppositeRedcardStatus(): RedcardStatus = when (cardStatus) {
        STATUS_AVAILABLE -> RedcardStatus.USED
        STATUS_USED -> RedcardStatus.AVAILABLE
        else -> throw InvalidRedcardStatusException()
    }

    companion object {
        private const val STATUS_AVAILABLE = "available"
        private const val STATUS_USED = "used"
    }
}
