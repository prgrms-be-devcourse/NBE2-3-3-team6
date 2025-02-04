package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.donation.dto.Top5DonorWrapper
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.exception.DonationStatsException
import com.redbox.domain.donation.repository.DonationDetailRepository
import com.redbox.domain.donation.repository.DonationGroupRepository
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.service.RedcardService
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import com.redbox.global.exception.ErrorCode
import io.lettuce.core.RedisConnectionException
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDate

@Service
class DonationService(
    val donationFactory: DonationFactory,
    val donationGroupRepository: DonationGroupRepository,
    val donationDetailRepository: DonationDetailRepository,
    val redcardService: RedcardService,
    val authenticationService: AuthenticationService,
    val redisTemplate: RedisTemplate<String, Any>,
) {
    companion object {
        private val CACHE_TTL: Duration = Duration.ofMinutes(30)
        private const val TOP5_DONOR_KEY = "donors:top5"
    }

    // 서버 시작시 캐시 초기화
    // 메인페이지 기능이기 때문에
    @PostConstruct
    fun initializeCache() {
        redisTemplate.delete(TOP5_DONOR_KEY)
        updateTop5DonorsCache()
    }

    @Scheduled(cron = "0 0/30 0 * * *")
    fun run() {
        updateTop5DonorsCache()
    }

    fun getCachedTop5Donors() : Top5DonorWrapper {
        try {
            val cachedObject = redisTemplate.opsForValue().get(TOP5_DONOR_KEY)

            if (cachedObject != null) {
                return cachedObject as? Top5DonorWrapper ?: getTop5DonorsFromDB()
            }

            val donors: Top5DonorWrapper = getTop5DonorsFromDB()
            redisTemplate.opsForValue().set(TOP5_DONOR_KEY, donors, CACHE_TTL)
            return donors
        } catch(e: RedisConnectionException) {
            log.error("Redis 연결 실패, DB에서 직접 조회합니다", e)
            return getTop5DonorsFromDB()
        }
    }

    fun updateTop5DonorsCache() {
        val top5Donors: Top5DonorWrapper = getTop5DonorsFromDB();
        try {
            redisTemplate.opsForValue().set(TOP5_DONOR_KEY, top5Donors, CACHE_TTL)
        } catch (e: RedisConnectionException) {
            log.error("Redis 캐시 갱신 실패", e)
        }
    }

    fun getTop5DonorsFromDB(): Top5DonorWrapper {
        return Top5DonorWrapper(donationGroupRepository.findTop5DonorsOfTheMonth())
    }

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

    fun donationConfirm(fundingId: Long, receiverId: Long) {
        val donationGroupList: List<DonationGroup> =
            donationGroupRepository.findAllByReceiverIdAndDonationType(fundingId)

        for (donationGroup in donationGroupList) {
            donationGroup.donationConfirm()
            val pendingRedcards: List<Redcard> = getPendingRedcards(donationGroup.id!!)
            redCardsConfirm(pendingRedcards, receiverId)
        }
    }

    fun getPendingRedcards(donationId: Long): List<Redcard> {
        val donationDetails = donationDetailRepository.findAllByDonationGroupId(donationId)
        return donationDetails.map { redcardService.getRedcardById(it.redcardId) }
    }

    fun redCardsConfirm(pendingRedcards: List<Redcard>, receiverId: Long) {
        redcardService.updateDonatedRedcards(pendingRedcards, OwnerType.USER, RedcardStatus.AVAILABLE, receiverId)
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