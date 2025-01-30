package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus

interface Donation {
    fun createDonationGroup(donorId: Long, donationRequest: DonationRequest): DonationGroup
    fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail>
    fun getDonationType(): DonationType
    fun getOwnerType(): OwnerType
    fun getReceiverId(donationRequest: DonationRequest): Long?
    fun validateSelfDonate(donorId: Long, donationRequest: DonationRequest)
    fun getCardStatus(): RedcardStatus
}