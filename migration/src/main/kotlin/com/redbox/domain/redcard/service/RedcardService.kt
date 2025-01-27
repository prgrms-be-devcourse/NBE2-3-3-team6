package com.redbox.domain.redcard.service

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException
import com.redbox.domain.redcard.repository.RedcardRepository
// import com.redbox.domain.user.service.UserService
import com.redbox.global.entity.PageResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RedcardService(
    private val redcardRepository: RedcardRepository
    // private val userService: UserService
) {

    @Transactional
    fun registerRedCard(request: RegisterRedcardRequest) {

        // 헌혈증 번호 중복 체크
        val isDuplicate = redcardRepository.findBySerialNumber(request.cardNumber).isPresent
        if (isDuplicate) {
            throw DuplicateSerialNumberException()
        }

        // Redcard 생성
        val redcard = Redcard(
            // userId = userService.getCurrentUserId(),
            userId = 0L, // TODO: UserService 추가 후 수정 필요
            donationDate = request.donationDate,
            serialNumber = request.cardNumber,
            hospitalName = request.hospitalName,
            redcardStatus = RedcardStatus.AVAILABLE,
            ownerType = OwnerType.USER
        )

        redcardRepository.save(redcard)
    }

    fun getRedcards(page: Int, size: Int): PageResponse<Redcard> {
        val pageable = PageRequest.of(page - 1, size)
        // val userId = userService.getCurrentUserId() // TODO: UserService 추가 후 수정 필요
        val userId = 0L // 임시 ID
        val redcards = redcardRepository.findAllByUserId(userId, pageable)
        return PageResponse(redcards)
    }
}
