package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.entity.Redcard
import com.redbox.domain.donation.exception.SelfDonationException
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
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
        // redCard 가져오기..
//        val redCards = redCardService.getDonationRedCards(donationRequest.quantity, donorId)
        val redCards = listOf(
            Redcard(1,1),
            Redcard(2,1),
            Redcard(3,1),
            Redcard(4,1),
        )

        // redCards 에 대한 에러처리는 Redcard service에서 처리할 예정
        if (redCards.isEmpty() || redCards.size < donationRequest.quantity) {
            throw IllegalArgumentException("Invalid donation request")
        }

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
        donation.updateRedCardEntities(donationRequest.receiveId, redCards)
        donationDetailRepository.saveAll(donationDetails)

        return savedDonation
    }
}