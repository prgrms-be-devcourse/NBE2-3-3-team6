package com.redbox.domain.redcard.facade

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.service.RedcardService
import org.springframework.stereotype.Component

@Component
class RedcardFacade(
    private val redcardService: RedcardService
) {

    fun registerRedCard(request: RegisterRedcardRequest) {
        redcardService.registerRedCard(request)
    }
}