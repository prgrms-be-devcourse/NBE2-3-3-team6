package com.redbox.domain.donation.entity

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import java.time.LocalDate

class FundingDonation(): Donation {
    override fun createDonationGroup(donorId: Long, donationRequest: DonationRequest): DonationGroup {
        return DonationGroup(
            donorId,
            donationRequest.receiveId,
            donationRequest.quantity,
            LocalDate.now(),
            donationRequest.comment,
            DonationType.FUNDING,
            DonationStatus.PENDING
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
        return DonationType.FUNDING
    }

    override fun getOwnerType(): OwnerType {
        return OwnerType.USER
    }

    override fun getReceiverId(donationRequest: DonationRequest): Long? {
        // 소유자가 아직 변경되면 안되므로 (donation 단계에서) 기존 id 반환
        return 1L
    }

    override fun validateSelfDonate(donorId: Long, donationRequest: DonationRequest) {
        //TODO: receiverId (게시글Id) 로부터 작성자 Id 를 추출해와야함
    }

    override fun getCardStatus(): RedcardStatus {
        return RedcardStatus.PENDING
    }
}