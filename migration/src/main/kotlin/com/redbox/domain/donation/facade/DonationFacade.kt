package com.redbox.domain.donation.facade

import com.redbox.domain.donation.application.DonationService
import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.global.entity.PageResponse
import org.springframework.data.repository.findByIdOrNull
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

    fun getReceptions(
        page: Int, size: Int
    ): PageResponse<ReceptionListResponse> {
        return donationService.getReceptions(page, size)
    }

    fun getSumDonationAmountInRedbox(): Int? {
        return donationService.getSumDonationAmountInRedbox()
    }

    fun donationConfirm(fundingId: Long, receiverId: Long) {
        return donationService.donationConfirm(fundingId, receiverId)
    }
}