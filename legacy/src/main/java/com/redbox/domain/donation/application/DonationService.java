package com.redbox.domain.donation.application;

import com.redbox.domain.donation.dto.DonationRequest;

public interface DonationService {
    void processDonation(DonationRequest donationRequest);

    void cancelDonation(long receiveId);
    // 현재 요구사항에는 게시글에 대한 기부만 취소가 가능하여 나머지 기부들에 대해 cancel 구현하지 않았으나, 추후 확장성을 고려하여 interface 에 취소 메서드 선언하였음

    void validateSelfDonate(long receiveId, long donorId);

}
