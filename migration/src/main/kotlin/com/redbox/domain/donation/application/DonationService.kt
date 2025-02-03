package com.redbox.domain.donation.application

import com.redbox.domain.community.funding.service.FundingService
import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.service.RedcardService
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class DonationService(
    val donationFactory: DonationFactory,
    val donationGroupRepository: DonationGroupRepository,
    val donationDetailRepository: DonationDetailRepository,
    val redcardService: RedcardService,
    val authenticationService: AuthenticationService,
    val fundingService: FundingService
) {

    @Transactional
    fun processDonation(type: String, donationRequest: DonationRequest): DonationGroup {
        val donorId = authenticationService.getCurrentUserId()

        val redCards = redcardService.getAvailableRedcardList(donorId, donationRequest.quantity)

        val donation = donationFactory.createDonation(type, donorId, donationRequest)
        donation.validateSelfDonate()

        val donationGroup = saveDonationGroup(donation)
        val donationGroupId = requireNotNull(donationGroup.id) { "DonationGroup 저장 실패" }

        saveDonationDetails(donation, donationGroupId, redCards)

        redcardService.updateDonatedRedcards(
            redCards,
            donation.ownerType,
            donation.cardStatus,
            donation.getReceiverId()
        )

        return donationGroup
    }

    fun saveDonationGroup(donation: Donation): DonationGroup {
        val donationGroup = donation.createDonationGroup()
        return donationGroupRepository.save(donationGroup)
    }

    fun saveDonationDetails(donation: Donation, donationGroupId: Long, redCards: List<Redcard>) {
        val donationDetails = donation.createDonationDetails(donationGroupId, redCards)
        donationDetailRepository.saveAll(donationDetails)
    }

    fun donationConfirm(donationId: Long) {
        val donationGroup = donationGroupRepository.findByIdOrNull(donationId)!!
        donationGroup.donationConfirm()
        val pendingRedcards: List<Redcard> = getPendingRedcards(donationId)
        val receiverId = findWriterId(donationGroup.receiverId)
        redCardsConfirm(pendingRedcards, receiverId)
    }

    fun getPendingRedcards(donationId: Long): List<Redcard> {
        val donationDetails = donationDetailRepository.findAllByDonationGroupId(donationId)
        return donationDetails.map { redcardService.getRedcardById(it.redcardId) }
    }

    fun redCardsConfirm(pendingRedcards: List<Redcard>, receiverId: Long) {
        redcardService.updateDonatedRedcards(pendingRedcards, OwnerType.USER, RedcardStatus.AVAILABLE, receiverId)
    }

    fun findWriterId(fundingId: Long): Long {
        return fundingService.findWriter(fundingId)
    }

    fun donationCancel(donationId: Long) {
        val donationGroup = donationGroupRepository.findByIdOrNull(donationId)!!
        donationGroup.donationCancel()
        val pendingRedcards: List<Redcard> = getPendingRedcards(donationId)
        redcardService.cancelDonateRedcards(pendingRedcards)
    }

    fun getDonations(
        page: Int, size: Int
    ): PageResponse<DonationListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size)
        return PageResponse(donationGroupRepository.findAllWithReceiverNameByDonorId(authenticationService.getCurrentUserId(), pageable))
    }

    fun getReceptions(
        page: Int, size: Int
    ): PageResponse<ReceptionListResponse> {
        val pageable: Pageable = PageRequest.of(page - 1, size)
        return PageResponse(donationGroupRepository.findAllWithDonorNameByReceiverId(authenticationService.getCurrentUserId(), pageable))
    }

    fun getSumDonationAmountInRedbox(): Int? {
        return donationGroupRepository.sumDonationAmountInRedbox()
    }
}