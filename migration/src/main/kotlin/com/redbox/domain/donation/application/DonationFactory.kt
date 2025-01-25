package com.redbox.domain.donation.application

import com.redbox.domain.donation.dto.DonationRequest
import com.redbox.domain.donation.entity.Donation
import com.redbox.domain.donation.entity.UserDonation
import org.springframework.stereotype.Component
import java.util.Locale

@Component
class DonationFactory {

    fun createDonation(type: String): Donation {
        return when (type.lowercase(Locale.getDefault())) {
            "user" -> UserDonation()
//            "redbox" ->
//            "request"
            else -> throw IllegalArgumentException("지원되지 않는 기부 유형")
        }
    }
}