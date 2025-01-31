package com.redbox.domain.user.user.controller

import com.redbox.domain.community.funding.dto.FundingListResponse
import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.redcard.dto.RedcardResponse
import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.dto.UpdateRedcardStatusRequest
import com.redbox.domain.user.dto.*
import com.redbox.domain.user.user.service.UserService
import com.redbox.domain.user.user.dto.*
import com.redbox.global.entity.PageResponse
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

    // TODO: auth 쪽 완성 시 테스트 진행
    @GetMapping("/users/my-info")
    fun getUserInfo(): ResponseEntity<UserInfoResponse> {
        return ResponseEntity.ok(userService.getUserInfo())
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @PutMapping("/users/my-info")
    fun updateUserInfo(@RequestBody @Valid request: UpdateUserInfoRequest): ResponseEntity<UserInfoResponse> {
        return ResponseEntity.ok(userService.updateUserInfo(request))
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @PutMapping("/users/my-info/password")
    fun updatePassword(@RequestBody @Valid request: UpdatePasswordRequest): ResponseEntity<Void> {
        userService.changePassword(request)
        return ResponseEntity.ok().build()
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @GetMapping("/users/my-info/redcards")
    fun getRedcards(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") size: Int
    ): ResponseEntity<PageResponse<RedcardResponse>> {
        return ResponseEntity.ok(userService.getRedcards(page, size))
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @PutMapping("/users/my-info/redcards/{redcardId}")
    fun updateRedcardStatus(
        @RequestBody @Valid request: UpdateRedcardStatusRequest,
        @PathVariable redcardId: Long
    ): ResponseEntity<Void> {
        userService.updateRedcardStatus(request, redcardId)
        return ResponseEntity.ok().build()
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @PostMapping("/auth/email-check")
    fun checkUserByEmail(
        @RequestBody request: CheckUserRequest
    ): ResponseEntity<CheckUserResponse> {
        val response: CheckUserResponse = userService.checkUser(request)

        return ResponseEntity.ok(response)
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @GetMapping("/users/my-info/redcards/donations")
    fun getDonations(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") size: Int
    ): ResponseEntity<PageResponse<DonationListResponse>> {
        return ResponseEntity.ok(userService.getDonations(page, size))
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @GetMapping("/users/my-info/redcards/receipts")
    fun getReceptions(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") size: Int
    ): ResponseEntity<PageResponse<ReceptionListResponse>> {
        return ResponseEntity.ok(userService.getReceptions(page, size))
    }

    // TODO: auth 쪽 완성 시 테스트 진행
    @GetMapping("/users/my-info/requests")
    fun getMyRequests(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<PageResponse<FundingListResponse>> {
        return ResponseEntity.ok(userService.getMyRequests(page, size))
    }
}