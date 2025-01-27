package com.redbox.domain.user.controller

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.user.dto.*
import com.redbox.domain.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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

    @PostMapping("/auth/reset-password")
    fun resetPassword(
        @RequestBody @Valid request: ResetPasswordRequest
    ): ResponseEntity<Void> {
        userService.resetPassword(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/auth/find-id")
    fun findUserId(
        @RequestBody @Valid request: FindIdRequest
    ): ResponseEntity<FindIdResponse> {
        // 요청 객체를 그대로 서비스에 넘김
        val response = userService.findUserId(request)
        return ResponseEntity.ok(response)
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @PostMapping("/users/my-info/redcards")
    fun registerRedCard(@RequestBody @Valid request: RegisterRedcardRequest): ResponseEntity<Void> {
        userService.registerRedCard(request)
        return ResponseEntity.ok().build()
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @DeleteMapping("/auth/drop-info")
    fun dropUser(@RequestBody @Valid request: DropInfoRequest): ResponseEntity<Void> {
        userService.dropUser(request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/users/my-info")
    fun getUserInfo(): ResponseEntity<UserInfoResponse> {
        return ResponseEntity.ok(userService.getUserInfo())
    }
}