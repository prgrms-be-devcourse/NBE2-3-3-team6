package com.redbox.domain.donation.controller;

import com.redbox.domain.donation.application.DonationService;
import com.redbox.domain.donation.application.DonationStatsService;
import com.redbox.domain.donation.application.RequestDonationService;
import com.redbox.domain.donation.application.UserDonationService;
import com.redbox.domain.donation.dto.DonationRequest;
import com.redbox.domain.donation.dto.Top5DonorWrapper;
import com.redbox.domain.donation.exception.InvalidDonationTypeException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DonationController {

    private final Map<String, DonationService> donationServiceMap;

    private final DonationStatsService donationStatsService;
    private final UserDonationService userDonationService;
    private final RequestDonationService requestDonationService;

    @PostMapping("/donate/{type}")
    public ResponseEntity<String> donate(@PathVariable String type, @RequestBody DonationRequest donationRequest) {
        DonationService donationService = donationServiceMap.get(type.toLowerCase());

        if (donationService == null) {
            throw new InvalidDonationTypeException();
        }

        donationService.processDonation(donationRequest);
        return ResponseEntity.ok("기부 성공");
    }

    @GetMapping("/donations/top")
    public ResponseEntity<Top5DonorWrapper> getTop5Donor() {
        return ResponseEntity.ok(userDonationService.getCachedTop5Donors());
    }

    @PutMapping("/donate/cancel/{type}/{receiveId}")
    public ResponseEntity<String> donateCancel(@PathVariable String type, @PathVariable long receiveId) {
        DonationService donationService = donationServiceMap.get(type.toLowerCase());

        if (donationService == null) {
            throw new InvalidDonationTypeException();
        }

        donationService.cancelDonation(receiveId);
        return ResponseEntity.ok("기부 취소");
    }

//    기부확정 테스트용 api 잘 돌아가는지 확인해보세영
    @GetMapping("/test/confirm/{requestId}/{receiverId}")
    public ResponseEntity<String> testConfirm(@PathVariable long requestId, @PathVariable long receiverId) {
        requestDonationService.donationConfirm(requestId, receiverId);
        return ResponseEntity.ok("기부 확정");
    }
}