package com.redbox.domain.user.service

import com.redbox.domain.user.dto.VerificationCodeRequest
import com.redbox.domain.user.exception.DuplicateEmailException
import com.redbox.domain.user.repository.EmailVerificationCodeRepository
import com.redbox.domain.user.repository.UserRepository
import com.redbox.global.util.RandomCodeGenerator
import com.redbox.global.util.email.EmailSender
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
}