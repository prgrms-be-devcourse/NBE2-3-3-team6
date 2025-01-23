package com.redbox.domain.donation.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "donation_details")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DonationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "donation_detail_id")
    private Long id;
    private Long donationGroupId;
    private Long redcardId;

    @Builder
    public DonationDetail(Long donationGroupId, Long redcardId) {
        this.donationGroupId = donationGroupId;
        this.redcardId = redcardId;
    }
}
