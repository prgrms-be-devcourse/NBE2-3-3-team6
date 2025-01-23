package com.redbox.domain.donation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "donation_details")
class DonationDetail(
    donationGroupId: Long,
    redcardId: Long
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_detail_id")
    var id: Long? = null
        private set

    @Column(name = "donation_group_id", nullable = false)
    var donationGroupId: Long = donationGroupId
        private set

    @Column(name = "redcard_id", nullable = false)
    var redcardId: Long = redcardId
        private set
}