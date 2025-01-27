package com.redbox.domain.donation.repository

import com.redbox.domain.donation.entity.DonationDetail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DonationDetailRepository : JpaRepository<DonationDetail, Long> {
}