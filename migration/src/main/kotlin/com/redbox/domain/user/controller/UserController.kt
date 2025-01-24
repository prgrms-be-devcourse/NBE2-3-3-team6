package com.redbox.domain.user.controller

import com.redbox.domain.user.dto.SignupRequest
import com.redbox.domain.user.dto.SignupResponse
import com.redbox.domain.user.dto.ValidateVerificationCodeRequest
import com.redbox.domain.user.dto.VerificationCodeRequest
import com.redbox.domain.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(
    private val userService: UserService,
//    private val redcardService: RedcardService,
//    private val fundingService: FundingService,
) {

    @PostMapping("/auth/email/verification-code")
    fun sendVerificationCode(
        @RequestBody @Valid request: VerificationCodeRequest
    ): ResponseEntity<Void> {
        userService.sendVerificationCode(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/auth/email/verify-code")
    fun validateVerificationCode(
        @RequestBody @Valid request: ValidateVerificationCodeRequest
    ): ResponseEntity<Void> {
        val isValid = userService.validateVerificationCode(request)
        if (isValid) {
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
    }

    @PostMapping("/auth/signup")
    fun signup(
        @RequestBody @Valid request: SignupRequest
    ): ResponseEntity<SignupResponse> {
        val response = userService.signup(request)
        return ResponseEntity.ok(response)
    }
}