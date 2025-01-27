package com.redbox.domain.user.service

import com.redbox.domain.auth.dto.CustomUserDetails
import com.redbox.domain.community.funding.exception.UserNotFoundException
import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.facade.RedcardFacade
import com.redbox.domain.user.dto.*
import com.redbox.domain.user.entity.User
import com.redbox.domain.user.exception.DuplicateEmailException
import com.redbox.domain.user.exception.EmailNotVerifiedException
import com.redbox.domain.user.repository.EmailVerificationCodeRepository
import com.redbox.domain.user.repository.UserRepository
import com.redbox.global.util.RandomCodeGenerator
import com.redbox.global.util.email.EmailSender
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine


@Service
class UserService(
    private val templateEngine: SpringTemplateEngine,
    private val emailSender: EmailSender,
    private val emailVerificationCodeRepository: EmailVerificationCodeRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val redcardFacade: RedcardFacade,
    //private val donationGroupRepository: DonationGroupRepository,
    //private val fundingRepository: FundingRepository,

) {
    private fun isDuplicatedEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    private fun createEmailContent(templateName: String, variableName: String, variableValue: String): String {
        val context: Context = Context()
        context.setVariable(variableName, variableValue)
        return templateEngine.process(templateName, context)
    }

    private fun encodePassword(password: String): String = passwordEncoder.encode(password)

    fun sendVerificationCode(request: VerificationCodeRequest) {
        // 이미 회원가입이 된 이메일인지 확인
        if (isDuplicatedEmail(request.email)) {
            throw DuplicateEmailException()
        }
        val verificationCode = RandomCodeGenerator.generateRandomCode()
        val subject = "[Redbox] 이메일 인증 코드입니다."
        val content: String = createEmailContent("verification-code-email", "verificationCode", verificationCode)
        emailSender.sendMail(request.email, subject, content)
        emailVerificationCodeRepository.save(request.email, verificationCode)
    }

    @Transactional
    fun validateVerificationCode(request: ValidateVerificationCodeRequest): Boolean {
        val verificationCode = emailVerificationCodeRepository.getVerificationCodeByEmail(request.email)

        return if (verificationCode == request.verificationCode) {
            emailVerificationCodeRepository.deleteByEmail(request.email)
            true
        } else {
            false
        }
    }

    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        // 이메일 인증이 완료되었는지 확인
        if (!request.isVerified()) {
            throw EmailNotVerifiedException()
        }

        // 이미 회원가입이 된 이메일인지 확인
        if (isDuplicatedEmail(request.email)) {
            throw DuplicateEmailException()
        }

        val encodedPassword = encodePassword(request.password)
        val user = SignupRequest.toEntity(request, encodedPassword)
        // 처음 회원가입 시 인증된 상태가 아니므로 직접 설정
        user.setCreatedBy(request.email)
        user.setUpdatedBy(request.email)

        userRepository.save(user)
        return SignupResponse(user.email, user.name)
    }

    @Transactional
    fun resetPassword(request: ResetPasswordRequest) {
        // 사용자 조회
        val user = userRepository.findByEmailAndName(request.email, request.username) ?: throw UserNotFoundException()

        // 임시 비밀번호 생성
        val tempPassword = RandomCodeGenerator.generateRandomCode()
        val encodedPassword = encodePassword(tempPassword)

        // 비밀번호 변경
        user.changePassword(encodedPassword)
        userRepository.save(user)

        // 이메일 전송
        val subject = "[Redbox] 임시 비밀번호 안내"
        val content = createEmailContent("temp-password-email", "tempPassword", tempPassword)
        emailSender.sendMail(request.email, subject, content)
    }

    fun findUserId(request: FindIdRequest): FindIdResponse {
        val name = request.userName
        val phoneNumber = request.phoneNumber

        val email = userRepository.findByNameAndPhoneNumber(name, phoneNumber)?.email ?: throw UserNotFoundException()

        return FindIdResponse(email)
    }

    fun registerRedCard(request: RegisterRedcardRequest) {
        redcardFacade.registerRedCard(request)
    }
}