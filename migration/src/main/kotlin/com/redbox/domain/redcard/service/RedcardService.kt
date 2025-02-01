package com.redbox.domain.redcard.service

// import com.redbox.domain.user.service.UserService
import com.redbox.domain.redcard.dto.RedcardResponse
import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.dto.UpdateRedcardStatusRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException
import com.redbox.domain.redcard.exception.NotEnoughRedCardException
import com.redbox.domain.redcard.exception.PendingRedcardException
import com.redbox.domain.redcard.exception.RedcardNotBelongException
import com.redbox.domain.redcard.exception.RedcardNotFoundException
import com.redbox.domain.redcard.repository.RedcardRepository
import com.redbox.global.auth.service.AuthenticationService
import com.redbox.global.entity.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RedcardService(
    private val redcardRepository: RedcardRepository,
    private val authenticationService: AuthenticationService
    // private val userService: UserService
) {

    // TODO: Auth 관련 마이그레이션이 끝난 후 테스트 해봐야함
    @Transactional
    fun registerRedCard(request: RegisterRedcardRequest) {

        // 헌혈증 번호 중복 체크
        val isDuplicate = redcardRepository.existsBySerialNumber(request.cardNumber)
        if (isDuplicate) {
            throw DuplicateSerialNumberException()
        }

        // Redcard 생성
        val redcard = Redcard(
            userId = authenticationService.getCurrentUserId(),
            donationDate = request.donationDate,
            serialNumber = request.cardNumber,
            hospitalName = request.hospitalName,
            redcardStatus = RedcardStatus.AVAILABLE,
            ownerType = OwnerType.USER
        )
        redcardRepository.save(redcard)
    }

    // TODO: Auth 관련 마이그레이션이 끝난 후 테스트 해봐야함
    fun getRedcards(page: Int, size: Int): PageResponse<RedcardResponse> {
        val pageable = PageRequest.of(page - 1, size)
        val redcards = redcardRepository.findAllByUserId(authenticationService.getCurrentUserId(), pageable)
        return PageResponse(redcards.map { RedcardResponse(it) })
    }

    fun getRedcardById(redcardId: Long): Redcard {
        return redcardRepository.findByIdOrNull(redcardId) ?: throw RedcardNotFoundException()
    }

    @Transactional
    fun updateRedCardUser(redcardId: Long, receiverId: Long) {
        val redcard = getRedcardById(redcardId)
        redcard.updateUser(receiverId)
        redcard.changeRedcardStatus(RedcardStatus.AVAILABLE)
    }

    @Transactional
    fun updateRedcardStatus(request: UpdateRedcardStatusRequest, redcardId: Long) {
        val redcard = redcardRepository.findByUserIdAndId(
            authenticationService.getCurrentUserId(),
            redcardId
        ) ?: throw RedcardNotBelongException()
        if (redcard.redcardStatus == RedcardStatus.PENDING) {
            throw PendingRedcardException()
        }
        redcard.changeRedcardStatus(request.validateAndGetOppositeStatus())
    }

    @Transactional
    fun updateRedCardList(redcardList: List<Redcard>, receiveUserId: Long, ownerType: OwnerType) {
        redcardList.forEach {
            it.updateUser(receiveUserId)
            it.changeOwnerType(ownerType)
        }
        redcardRepository.saveAll(redcardList) // 변경 사항 저장
    }

    @Transactional
    fun updateRedCardStatusPending(redcardList: List<Redcard>) {
        redcardList.forEach { it.changeRedcardStatus(RedcardStatus.PENDING) }
        redcardRepository.saveAll(redcardList) // 변경 사항 저장
    }

    @Transactional
    fun updateRedCardCancel(redcardId: Long) {
        val redcard = getRedcardById(redcardId)
        redcard.changeRedcardStatus(RedcardStatus.AVAILABLE)
    }

    fun getAvailableRedcardList(userId: Long, quantity: Int): List<Redcard> {

        val pageable = PageRequest.of(0, quantity)
        val redcards: List<Redcard> =
            redcardRepository.findByUserIdAndRedcardStatus(userId, RedcardStatus.AVAILABLE, pageable)

        if (redcards.isEmpty() || redcards.size < quantity) {
            throw NotEnoughRedCardException()
        }

        return redcards
    }

    fun updateDonatedRedcards(redcards: List<Redcard>, ownerType: OwnerType, cardStatus: RedcardStatus, userId: Long?) {

        redcards.map {
            redcard ->
            redcard.changeOwnerType(ownerType)
            redcard.updateUser(userId)
            redcard.changeRedcardStatus(cardStatus)
        }
    }

    fun getCountAllInRedbox(): Int? {
        return redcardRepository.countAllInRedbox()
    }
}
