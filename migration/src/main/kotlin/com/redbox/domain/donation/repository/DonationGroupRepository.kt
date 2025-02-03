package com.redbox.domain.donation.repository

import com.redbox.domain.donation.dto.DonationListResponse
import com.redbox.domain.donation.dto.ReceptionListResponse
import com.redbox.domain.donation.entity.DonationGroup
import com.redbox.domain.donation.entity.DonationType
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

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

    @Query("""
    SELECT 
        CASE 
            WHEN d.donorId = 0 THEN '레드박스' 
            ELSE u.name 
        END as donorName,
        d.donationAmount as donationAmount,
        d.donationDate as donationDate,
        d.donationMessage as donationMessage
    FROM DonationGroup d
    LEFT JOIN User u ON d.donorId = u.id
    WHERE d.receiverId = :receiverId
    AND d.donationStatus = 'DONE'
    """)
    fun findAllWithDonorNameByReceiverId(
        receiverId: Long,
        pageable: Pageable
    ): Page<ReceptionListResponse>

    @Query("SELECT SUM(d.donationAmount) FROM DonationGroup d where d.donationType = 'TO_REDBOX' and d.donationStatus = 'DONE'")
    fun sumDonationAmountInRedbox(): Int?

    @Query("SELECT SUM(d.donationAmount) FROM DonationGroup d WHERE d.donorId = :donorId AND d.donationStatus = 'DONE'")
    fun sumDonationAmountByDonorId(@Param("donorId") donorId: Long): Int?

    @Query("SELECT COUNT(DISTINCT d.receiverId) FROM DonationGroup d WHERE d.donorId = :donorId AND d.receiverId != :redboxId AND d.donationStatus = 'DONE'")
    fun countDistinctReceiverIdByDonorIdAndReceiverIdNot(
        @Param("donorId") donorId: Long,
        @Param("redboxId") redboxId: Long
    ): Int?

    @Query("SELECT MAX(d.donationDate) FROM DonationGroup d WHERE d.donorId = :userId")
    fun findLastDonationDateByDonorId(@Param("userId") userId: Long): Optional<LocalDate>

    fun findAllByReceiverIdAndDonationType(receiverId: Long, donationType: DonationType = DonationType.FUNDING): List<DonationGroup>
}