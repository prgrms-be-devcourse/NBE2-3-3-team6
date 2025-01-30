package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.service.RedcardService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DonationService(
    val donationFactory: DonationFactory,
    val donationGroupRepository: DonationGroupRepository,
    val donationDetailRepository: DonationDetailRepository,
    val redcardService: RedcardService
) {

    @Transactional
    fun processDonation(type: String, donationRequest: DonationRequest): DonationGroup {
        // TODO: donorId (securityHolder 에서 가져오기)
        var donorId: Long = 1L

        val redCards = redcardService.getAvailableRedcardList(donorId, donationRequest.quantity)

        val donation = donationFactory.createDonation(type)
        donation.validateSelfDonate(donorId, donationRequest)
        // donationGroup 저장
        val donationGroup = saveDonationGroup(donation, donorId, donationRequest)
        val donationGroupId = requireNotNull(donationGroup.id) { "DonationGroup 저장 실패" }

        // donationDetail 저장
        saveDonationDetails(donation, donationGroupId, redCards)

        // redCard 소유자 변경
        redcardService.updateDonatedRedcards(redCards, donation.getOwnerType(), donation.getCardStatus(), donation.getReceiverId(donationRequest))

        return donationGroup
    }

    fun saveDonationGroup(donation: Donation, donorId: Long, donationRequest: DonationRequest): DonationGroup {
        val donationGroup = donation.createDonationGroup(donorId, donationRequest)
        return donationGroupRepository.save(donationGroup)
    }

    fun saveDonationDetails(donation: Donation, donationGroupId: Long, redCards: List<Redcard>) {
        val donationDetails = donation.createDonationDetails(donationGroupId, redCards)
        donationDetailRepository.saveAll(donationDetails)
    }

}