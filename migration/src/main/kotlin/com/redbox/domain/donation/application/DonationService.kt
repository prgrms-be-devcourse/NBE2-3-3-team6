package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.exception.SelfDonationException
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
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
        // donor 에 대한 검증 ( 자기 자신에게 기부 막기, 기부 수량에 대한 검증 )
        // donorId (securityHolder 에서 가져오기)
        var donorId: Long = 1L
        if (donorId == donationRequest.receiveId) {
            throw SelfDonationException();
        }
        val redCards = redcardService.getAvailableRedcardList(donorId, donationRequest.quantity)

        val donation = donationFactory.createDonation(type)
        // donationGroup 저장
        val donationGroup = donation.createDonationGroup(donorId, donationRequest)
        val savedDonation = donationGroupRepository.save(donationGroup)

        // donationDetail 저장
        val donationDetails = donation.createDonationDetails(savedDonation.id!!, redCards)
        donationDetailRepository.saveAll(donationDetails)

        // redCard 소유자 변경
        redcardService.updateDonatedRedcards(redCards, donation.getOwnerType(), donationRequest.receiveId)

        return savedDonation
    }
}