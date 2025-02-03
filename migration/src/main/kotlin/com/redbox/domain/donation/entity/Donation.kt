package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus

interface Donation {
    val donorId: Long
    val donationType: DonationType
    val ownerType: OwnerType
    val cardStatus: RedcardStatus

    fun createDonationGroup(): DonationGroup
    fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail>
    fun getReceiverId(): Long?
    fun validateSelfDonate()
}
