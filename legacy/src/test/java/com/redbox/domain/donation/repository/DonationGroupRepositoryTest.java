package com.redbox.domain.donation.repository;

import com.redbox.domain.donation.entity.DonationGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DonationGroupRepositoryTest {

    @Autowired
    private DonationGroupRepository donationGroupRepository;

    @DisplayName("save Test")
    @Test
    void saveTest() {
        DonationGroup donationGroup = DonationGroup.builder()
                                                   .donationDate(LocalDate.now())
                                                   .donationAmount(10)
                                                   .donorId(1L)
                                                   .receiverId(0L)
                                                   .build();

        DonationGroup savedDonationGroup = donationGroupRepository.save(donationGroup);

        assertEquals(1L, savedDonationGroup.getDonorId());
    }

}