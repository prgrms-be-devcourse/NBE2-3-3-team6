package com.redbox.domain.donation.application;

import com.redbox.domain.donation.entity.DonationDetail;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.repository.DonationDetailRepository;
import com.redbox.domain.donation.repository.DonationGroupRepository;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.redcard.service.RedcardService;
import com.redbox.domain.user.service.UserService;

import org.springframework.stereotype.Component;

import lombok.Getter;

import java.util.List;

@Getter
@Component
public class DonationServiceDependencies {
    private final UserService userService;
    private final RedcardRepository redcardRepository;
    private final RedcardService redcardService;
    private final DonationGroupRepository donationGroupRepository;
    private final DonationDetailRepository donationDetailRepository;

    public DonationServiceDependencies(UserService userService, RedcardRepository redcardRepository, RedcardService redcardService, DonationGroupRepository donationGroupRepository, DonationDetailRepository donationDetailRepository) {
        this.userService = userService;
        this.redcardRepository = redcardRepository;
        this.redcardService = redcardService;
        this.donationGroupRepository = donationGroupRepository;
        this.donationDetailRepository = donationDetailRepository;
    }

    public Long getCurrentUserId() {
        return userService.getCurrentUserId();
    }

    public List<Redcard> getUserRedcards(Long userId, RedcardStatus status) {
        return redcardRepository.findByUserIdAndRedcardStatus(userId, status);
    }

    public DonationGroup saveDonationGroup(DonationGroup donationGroup) {
        return donationGroupRepository.save(donationGroup);
    }

    public void saveDonationDetail(DonationDetail donationDetail) {
        donationDetailRepository.save(donationDetail);
    }
}
