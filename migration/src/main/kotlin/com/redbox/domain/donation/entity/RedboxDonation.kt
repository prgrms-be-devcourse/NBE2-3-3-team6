package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import java.time.LocalDate

class RedboxDonation(override val donorId: Long, val donationRequest: DonationRequest) : Donation {
    override val donationType = DonationType.REDBOX
    override val ownerType = OwnerType.REDBOX
    override val cardStatus = RedcardStatus.AVAILABLE

    override fun createDonationGroup(): DonationGroup {
        return DonationGroup(
            donorId,
            donationRequest.receiveId,
            donationRequest.quantity,
            LocalDate.now(),
            donationRequest.comment,
            donationType,
            DonationStatus.DONE
        )
    }

    override fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail> {
        return redCards.map { redcard ->
            DonationDetail(
                donationGroupId,
                redcard.id!!
            )
        }
    }

    override fun getReceiverId(): Long? {
        return null
    }

    override fun validateSelfDonate() {

    }
}