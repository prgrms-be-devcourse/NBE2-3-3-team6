package com.redbox.domain.donation.controller

import com.redbox.domain.donation.application.DonationService
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.dto.DonationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DonationController(
    val donationService: DonationService
) {

    @PostMapping("/donation/{type}")
    fun donate(
        @PathVariable type: String,
        @RequestBody donationRequest: DonationRequest
    ) : ResponseEntity<DonationResponse> {
        donationService.processDonation(type, donationRequest)

        return ResponseEntity.ok(DonationResponse("기부성공"))
    }
}