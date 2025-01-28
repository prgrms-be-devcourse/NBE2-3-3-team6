package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.dto.DonationResponse
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.exception.SelfDonationException
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
import com.redbox.domain.redcard.service.RedcardService
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class DonationService(
    val donationFactory: DonationFactory,
    val donationGroupRepository: DonationGroupRepository,
    val donationDetailRepository: DonationDetailRepository,
    val redcardService: RedcardService,
    val authenticationService: AuthenticationService
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
        // TODO: funding 기부시 다르게 처리해야함 (전략패턴 도입 고려 중)
        redcardService.updateDonatedRedcards(redCards, donation.getOwnerType(), donation.getReceiverId(donationRequest))

        return savedDonation
    }

    fun getDonations(
        page: Int, size: Int
    ): PageResponse<DonationListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size)
        return PageResponse(donationGroupRepository.findAllWithReceiverNameByDonorId(authenticationService.getCurrentUserId(), pageable))
    }
}