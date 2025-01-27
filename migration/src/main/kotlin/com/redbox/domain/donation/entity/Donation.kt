package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard

interface Donation {
    fun createDonationGroup(donorId: Long, donationRequest: DonationRequest): DonationGroup
    fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail>
    fun getDonationType(): DonationType
    fun getOwnerType(): OwnerType

}