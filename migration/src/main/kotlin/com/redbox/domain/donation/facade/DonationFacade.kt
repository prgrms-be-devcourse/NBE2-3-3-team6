package com.redbox.domain.donation.facade

import com.redbox.domain.donation.application.DonationService
import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.global.entity.PageResponse
import org.springframework.stereotype.Component

@Component
class DonationFacade(
    private val donationService: DonationService
) {
    fun getDonations(
        page: Int, size: Int
    ): PageResponse<DonationListResponse> {
        return donationService.getDonations(page, size)
    }
}