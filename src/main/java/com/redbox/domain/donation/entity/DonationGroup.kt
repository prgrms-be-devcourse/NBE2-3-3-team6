package com.redbox.domain.donation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "donation_groups")
class DonationGroup(
    donorId: Long,
    receiverId: Long,
    donationType: DonationType,
    donationAmount: Int,
    donationDate: LocalDate,
    donationMessage: String,
    donationStatus: DonationStatus
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_group_id")
    var id: Long? = null
        private set

    @Column(name = "donor_id", nullable = false)
    val donorId: Long = donorId

    @Column(name = "receiver_id", nullable = false)
    val receiverId: Long = receiverId

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_type", nullable = false)
    val donationType: DonationType = donationType

    @Column(name = "donation_amount", nullable = false)
    val donationAmount: Int = donationAmount

    @Column(name = "donation_date", nullable = false)
    val donationDate: LocalDate = donationDate

    @Column(name = "donation_message", nullable = false)
    val donationMessage: String = donationMessage

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_status", nullable = false)
    var donationStatus: DonationStatus = donationStatus

    // 상태 변경 메서드
    fun donateConfirm() {
        this.donationStatus = DonationStatus.DONE
    }

    fun donateCancel() {
        this.donationStatus = DonationStatus.CANCEL
    }
}