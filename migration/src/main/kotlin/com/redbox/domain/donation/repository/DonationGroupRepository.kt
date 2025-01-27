package com.redbox.domain.donation.repository

import com.redbox.domain.donation.entity.DonationGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DonationGroupRepository : JpaRepository<DonationGroup, Long> {
}