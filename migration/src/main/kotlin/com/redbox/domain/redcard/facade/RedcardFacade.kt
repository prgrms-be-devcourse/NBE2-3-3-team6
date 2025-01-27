package com.redbox.domain.redcard.facade

import com.redbox.domain.redcard.dto.RedcardResponse
import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.dto.UpdateRedcardStatusRequest
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.service.RedcardService
import com.redbox.global.entity.PageResponse
import org.springframework.stereotype.Component

@Component
class RedcardFacade(
    private val redcardService: RedcardService
) {

    fun registerRedCard(request: RegisterRedcardRequest) {
        redcardService.registerRedCard(request)
    }

    fun getRedcards(page: Int, size: Int): PageResponse<RedcardResponse> {
        return redcardService.getRedcards(page, size)
    }

    fun updateRedcardStatus(request: UpdateRedcardStatusRequest, redcardId: Long) {
        redcardService.updateRedcardStatus(request, redcardId)
    }
}