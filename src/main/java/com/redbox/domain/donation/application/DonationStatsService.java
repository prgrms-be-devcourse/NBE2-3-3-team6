package com.redbox.domain.donation.application;

import com.redbox.domain.donation.exception.DonationStatsException;
import com.redbox.domain.donation.repository.DonationGroupRepository;
import com.redbox.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DonationStatsService {

    private final DonationGroupRepository donationGroupRepository;

    public int getTotalDonatedCards(Long userId) {
        try {
            Integer total = donationGroupRepository.sumDonationAmountByDonorId(userId);
            return (total != null) ? total : 0;
        } catch (Exception e) {
            throw new DonationStatsException(ErrorCode.STATS_CALCULATION_FAILED);
        }
    }

    public int getPatientsHelped(Long userId) {
        try {
            Integer patients = donationGroupRepository.countDistinctReceiverIdByDonorIdAndReceiverIdNot(userId, 0L);
            return (patients != null) ? patients : 0;
        } catch (Exception e) {
            throw new DonationStatsException(ErrorCode.STATS_CALCULATION_FAILED);
        }
    }

    // 최근 기부 일자 조회
    public LocalDate getLastDonationDate(Long userId) {
        return donationGroupRepository.findLastDonationDateByDonorId(userId)
                .orElse(null);
    }

}
