package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import java.time.LocalDate

class UserDonation(
) : Donation {

    override fun createDonationGroup(donorId: Long, donationRequest: DonationRequest): DonationGroup {
        return DonationGroup(
            donorId,
            donationRequest.receiveId,
            donationRequest.quantity,
            LocalDate.now(),
            donationRequest.comment,
            DonationType.TO_USER,
            DonationStatus.DONE
        )
    }

    override fun createDonationDetails(donationGroupId: Long, redCards: List<Redcard>): List<DonationDetail> {
        return redCards.map { redcard ->
            DonationDetail(
                donationGroupId,
                redcard.getCardId()
            )
        }
    }

    override fun updateRedCardEntities(receiverId: Long, redCards: List<Redcard>) {
        redCards.map {
            redcard ->
            redcard.changeUserId(receiverId)
        }
    }
}