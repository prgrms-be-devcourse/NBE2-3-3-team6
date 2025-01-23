package com.redbox.domain.user.controller;

import com.redbox.domain.funding.application.FundingService;
import com.redbox.domain.funding.dto.ListResponse;
import com.redbox.domain.user.dto.*;
import com.redbox.domain.redcard.dto.RegisterRedcardRequest;
import com.redbox.domain.redcard.service.RedcardService;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RedcardService redCardService;
    private final FundingService fundingService;

    @PostMapping("/auth/email/verification-code")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody @Valid VerificationCodeRequest request) {
        userService.sendVerificationCode(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/email/verify-code")
    public ResponseEntity<Void> validateVerificationCode(@RequestBody @Valid ValidateVerificationCodeRequest request) {
        boolean isValid = userService.validateVerificationCode(request);
        if (isValid) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/find-id")
    public ResponseEntity<FindIdResponse> findUserId(@RequestBody @Valid FindIdRequest request) {
        // 요청 객체를 그대로 서비스에 넘김
        FindIdResponse response = userService.findUserId(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/my-info/redcards")
    public ResponseEntity<Void> registerRedCard(@RequestBody @Valid RegisterRedcardRequest request) {
        redCardService.registerRedCard(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/auth/drop-info")
    public ResponseEntity<Void> dropUser(@RequestBody @Valid DropInfoRequest request){
        userService.dropUser(request);
        return ResponseEntity.ok().build();
    }
  
    @GetMapping("/users/my-info")
    public UserInfoResponse getUserInfo() {
        return userService.getUserInfo();
    }

    @PutMapping("/users/my-info")
    public UserInfoResponse updateUserInfo(@RequestBody UpdateUserInfoRequest request) {
        return userService.updateUserInfo(request);
    }

    @PutMapping("/users/my-info/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/my-info/redcards")
    public ResponseEntity<PageResponse<RedcardResponse>> getRedcards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(redCardService.getRedcards(page, size));
    }

    @PutMapping("/users/my-info/redcards/{redcardId}")
    public ResponseEntity<Void> updateRedcardStatus(
            @RequestBody @Valid UpdateRedcardStatusRequest request,
            @PathVariable Long redcardId
    ) {
        redCardService.updateRedcardStatus(request, redcardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/email-check")
    public ResponseEntity<CheckUserResponse> checkUserByEmail(
            @RequestBody CheckUserRequest request) {
        CheckUserResponse response = userService.checkUser(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/my-info/redcards/donations")
    public ResponseEntity<PageResponse<DonationResponse>> getDonations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(userService.getDonations(page, size));
    }

    @GetMapping("/users/my-info/redcards/receipts")
    public ResponseEntity<PageResponse<ReceptionResponse>> getReceptions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(userService.getReceptions(page, size));
    }

    @GetMapping("/users/my-info/requests")
    public ResponseEntity<PageResponse<ListResponse>> getRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ListResponse> response = userService.getRequests(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
