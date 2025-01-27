package com.redbox.domain.redcard.service

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException
import com.redbox.domain.redcard.repository.RedcardRepository
import com.redbox.global.auth.service.AuthenticationService
// import com.redbox.domain.user.service.UserService
import com.redbox.global.entity.PageResponse
import org.springframework.data.domain.PageRequest
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
        val isDuplicate = redcardRepository.findBySerialNumber(request.cardNumber).isPresent
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
    fun getRedcards(page: Int, size: Int): PageResponse<Redcard> {
        val pageable = PageRequest.of(page - 1, size)
         val userId = authenticationService.getCurrentUserId()
        val redcards = redcardRepository.findAllByUserId(userId, pageable)
        return PageResponse(redcards)
    }
}
