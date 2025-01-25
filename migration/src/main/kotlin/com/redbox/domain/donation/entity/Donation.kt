package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest

interface Donation {
    fun createDonationGroup(donorId: Long, donationRequest: DonationRequest): DonationGroup
    fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail>
    fun updateRedCardEntities(receiverId: Long, redCards: List<Redcard>)
}