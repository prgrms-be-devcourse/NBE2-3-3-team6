package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.exception.SelfDonationException
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import java.time.LocalDate

class FundingDonation(override val donorId: Long, val donationRequest: DonationRequest, val writerId: Long) : Donation {
    override val donationType = DonationType.FUNDING
    override val ownerType = OwnerType.USER
    override val cardStatus = RedcardStatus.PENDING

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
        return donorId
    }

    override fun validateSelfDonate() {
        if (donorId == writerId) {
            throw SelfDonationException()
        }
    }
}