package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.exception.DonationStatsException
import com.redbox.domain.donation.exception.SelfDonationException
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.service.RedcardService
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import com.redbox.global.exception.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

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

    fun getTotalDonatedCards(userId: Long): Int {
        return try {
            val total = donationGroupRepository.sumDonationAmountByDonorId(userId)
            total ?: 0 // null이면 0 반환
        } catch (e: Exception) {
            throw DonationStatsException(ErrorCode.STATS_CALCULATION_FAILED)
        }
    }

    fun getPatientsHelped(userId: Long): Int {
        return try {
            val patients = donationGroupRepository.countDistinctReceiverIdByDonorIdAndReceiverIdNot(userId, 0L)
            patients ?: 0 // null이면 0 반환
        } catch (e: Exception) {
            throw DonationStatsException(ErrorCode.STATS_CALCULATION_FAILED)
        }
    }

    // 최근 기부 일자 조회
    fun getLastDonationDate(userId: Long): LocalDate? {
        return donationGroupRepository.findLastDonationDateByDonorId(userId).orElse(null)
    }
}