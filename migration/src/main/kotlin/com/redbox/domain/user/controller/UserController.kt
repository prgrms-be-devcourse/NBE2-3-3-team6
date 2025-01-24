package com.redbox.domain.user.controller

import com.redbox.domain.user.dto.VerificationCodeRequest
import com.redbox.domain.user.service.UserService
import jakarta.validation.Valid
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
}