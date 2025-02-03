package com.redbox.domain.donation.application

import com.redbox.domain.community.funding.service.FundingService
import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.FundingDonation
import com.redbox.domain.donation.entity.RedboxDonation
import com.redbox.domain.donation.entity.UserDonation
import com.redbox.domain.donation.exception.InvalidDonationTypeException
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class DonationFactory() {
    fun createDonation(type: String, donorId: Long, donationRequest: DonationRequest): Donation {
        return when (type.lowercase(Locale.getDefault())) {
            "user" -> UserDonation(donorId, donationRequest)
            "redbox" -> RedboxDonation(donorId, donationRequest)
            "request" -> FundingDonation(donorId, donationRequest)
            else -> throw InvalidDonationTypeException()
        }
    }
}