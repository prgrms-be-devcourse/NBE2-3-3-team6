package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.FundingDonation
import com.redbox.domain.donation.entity.RedboxDonation
import com.redbox.domain.donation.entity.UserDonation
import com.redbox.domain.donation.exception.InvalidDonationTypeException
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class DonationFactory {

    fun createDonation(type: String): Donation {
        return when (type.lowercase(Locale.getDefault())) {
            "user" -> UserDonation()
            "redbox" -> RedboxDonation()
            "request" -> FundingDonation()
            else -> throw InvalidDonationTypeException()
        }
    }
}