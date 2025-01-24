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

    val donorId: Long,
    val receiverId: Long,
    val donationAmount: Int,
    val donationDate: LocalDate,
    val donationMessage: String,
    donationType: DonationType,
    donationStatus: DonationStatus,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_group_id")
    val id: Long? = null,
) {
    @Enumerated(EnumType.STRING)
    var donationType: DonationType = donationType
        protected set

    @Enumerated(EnumType.STRING)
    var donationStatus: DonationStatus = donationStatus
        protected set

    fun donationConfirm() {
        donationStatus = DonationStatus.DONE
    }

    fun donationCancel() {
        donationStatus = DonationStatus.CANCEL
    }
}