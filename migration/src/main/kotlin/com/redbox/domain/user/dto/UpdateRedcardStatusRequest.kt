package com.redbox.domain.user.dto

import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.InvalidRedcardStatusException
import jakarta.validation.constraints.NotNull

data class UpdateRedcardStatusRequest(
    @field:NotNull
    val cardStatus: RedcardStatus
) {

    fun validateAndUpdateStatus(currentStatus: RedcardStatus): RedcardStatus {
        if (currentStatus == cardStatus) {
            throw InvalidRedcardStatusException()
        }
        return cardStatus
    }
}