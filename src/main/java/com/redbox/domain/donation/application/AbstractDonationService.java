package com.redbox.domain.donation.application;

import com.redbox.domain.donation.dto.DonationRequest;
import com.redbox.domain.donation.entity.DonationDetail;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.entity.DonationStatus;
import com.redbox.domain.donation.entity.DonationType;
import com.redbox.domain.donation.exception.DonationAmountExceededException;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractDonationService implements DonationService {
    protected final DonationServiceDependencies dependencies;

    public abstract void processDonation(DonationRequest donationRequest);

    public abstract void cancelDonation(long receiveId);

    public abstract void validateDonation(List<Redcard> redcardList, DonationRequest donationRequest);

    protected abstract void validateReceiver(long receiverId);

    protected List<Redcard> getUsersRedCardList() {
        Long donateUserId = dependencies.getCurrentUserId();

        return dependencies.getUserRedcards(donateUserId, RedcardStatus.AVAILABLE);
    }

    protected List<Redcard> pickDonateRedCardList(DonationRequest donationRequest) {
        List<Redcard> redcardList = getUsersRedCardList();
        validateDonation(redcardList, donationRequest);
        return redcardList.subList(0, donationRequest.getQuantity());
    }

    protected DonationGroup createDonationGroup(long donationUserId, long
            receiverId, DonationType donationType, DonationStatus donationStatus, int donationCount, String donationMessage) {
        DonationGroup redboxDonationGroup = DonationGroup.builder()
                                                         .donorId(donationUserId)
                                                         .receiverId(receiverId)
                                                         .donationType(donationType)
                                                         .donationStatus(donationStatus)
                                                         .donationAmount(donationCount)
                                                         .donationDate(LocalDate.now())
                                                         .donationMessage(donationMessage)
                                                         .build();

        return dependencies.saveDonationGroup(redboxDonationGroup);
    }

    protected void saveDonationDetails(List<Redcard> redcardList, Long donationGroupId) {
        for (Redcard redcard : redcardList) {
            DonationDetail donationDetail = DonationDetail.builder().
                                                          donationGroupId(donationGroupId).
                                                          redcardId(redcard.getId()).
                                                          build();

            dependencies.saveDonationDetail(donationDetail);
        }
    }

    public void checkDonateAmount(List<Redcard> redcardList, int count) {
        if (redcardList.size() < count) {
            throw new DonationAmountExceededException();
        }
    }
}

