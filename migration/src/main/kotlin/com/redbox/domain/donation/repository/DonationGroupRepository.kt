package com.redbox.domain.donation.repository

import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.entity.DonationGroup
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface DonationGroupRepository : JpaRepository<DonationGroup, Long> {

    @Query("""
    SELECT 
        CASE 
            WHEN d.receiverId = 0 THEN '레드박스' 
            ELSE u.name 
        END as receiverName,
        d.donationAmount as donationAmount,
        d.donationDate as donationDate,
        d.donationMessage as donationMessage
    FROM DonationGroup d
    LEFT JOIN User u ON d.receiverId = u.id
    WHERE d.donorId = :donorId
    AND d.donationStatus = 'DONE'
    """)
    fun findAllWithReceiverNameByDonorId(
        donorId: Long,
        pageable: Pageable
    ): Page<DonationListResponse>
}