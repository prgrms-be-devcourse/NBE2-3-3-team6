package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
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
            DonationType.USER,
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

    override fun getDonationType(): DonationType {
        return DonationType.USER
    }

    override fun getOwnerType(): OwnerType {
        return OwnerType.USER
    }

    override fun getReceiverId(donationRequest: DonationRequest): Long {
        return donationRequest.receiveId
    }
}