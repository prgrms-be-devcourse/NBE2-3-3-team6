package com.redbox.domain.donation.application;

import com.redbox.domain.donation.dto.DonationRequest;
import com.redbox.domain.donation.entity.DonationDetail;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.entity.DonationStatus;
import com.redbox.domain.donation.entity.DonationType;
import com.redbox.domain.donation.exception.DonationDuplicateException;
import com.redbox.domain.donation.exception.DonationGroupNotFoundException;
import com.redbox.domain.donation.exception.DonationNotSelfException;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.service.RedcardService;
import com.redbox.domain.funding.application.FundingService;
import com.redbox.domain.funding.entity.FundingStatus;
import com.redbox.domain.funding.exception.FundingNotFoundException;
import com.redbox.domain.funding.repository.FundingRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RequestDonationService extends AbstractDonationService {

    private final FundingService fundingService;
    private final RedcardService redcardService;
    private final FundingRepository fundingRepository;

    public RequestDonationService(DonationServiceDependencies dependencies, FundingService fundingService, FundingRepository fundingRepository) {
        super(dependencies);
        this.fundingService = fundingService;
        this.redcardService = dependencies.getRedcardService();
        this.fundingRepository = fundingRepository;
    }

    // 게시글 만료 처리
    @Transactional
    public void updateExpiredRequests() {
        LocalDate today = LocalDate.now();
        List<Funding> expiredFundings = fundingRepository.findByDonationEndDateBeforeAndProgressNot(today, FundingStatus.EXPIRED);
        for (Funding funding : expiredFundings) {
            donationConfirm(funding.getFundingId(), funding.getUserId());
            funding.expired();
        }
        fundingRepository.saveAll(expiredFundings);
    }

    @Transactional
    public void donationConfirm(long requestId, long receiverId) {
        List<DonationGroup> donationGroups = getDonationGroups(requestId, DonationStatus.PENDING);

        for (DonationGroup donationGroup : donationGroups) {
            donationGroup.donateConfirm();
            List<DonationDetail> donationDetails = getDonationDetails(donationGroup.getId());
            donationRedCardConfirm(donationDetails, receiverId);
        }
    }

    public void donationRedCardConfirm(List<DonationDetail> donationDetails, long receiverId) {
        for (DonationDetail donationDetail : donationDetails) {
            redcardService.updateRedCardUser(donationDetail.getRedcardId(), receiverId);
        }
    }

    @Transactional
    public void donationRedCardCancel(List<DonationDetail> donationDetails) {
        for (DonationDetail donationDetail : donationDetails) {
            redcardService.updateRedCardCancel(donationDetail.getRedcardId());
        }
    }

    // 하위 세 메서드들은 다른 곳으로 분리 고려
    public List<DonationDetail> getDonationDetails(long groupId) {
        return dependencies.getDonationDetailRepository().findByDonationGroupId(groupId);
    }

    public List<DonationGroup> getDonationGroups(long requestId, DonationStatus donationStatus) {
        List<DonationGroup> donationGroups = dependencies.getDonationGroupRepository()
                                                         .findByReceiverIdAndDonationStatus(requestId, donationStatus);

        if (donationGroups == null) {
            throw new DonationGroupNotFoundException();
        }

        return donationGroups;
    }

    public DonationGroup getDonationGroup(long userId, long receivedId, DonationStatus donationStatus) {
        DonationGroup donationGroup = dependencies.getDonationGroupRepository()
                                                  .findByDonorIdAndReceiverIdAndDonationStatus(userId, receivedId, donationStatus);

        if (donationGroup == null) {
            throw new DonationGroupNotFoundException();
        }

        return donationGroup;
    }

    @Transactional
    @Override
    public void processDonation(DonationRequest donationRequest) {
        // 게시글에 기부 (대기 상태)
        int donationCount = donationRequest.getQuantity();
        long receiverId = donationRequest.getReceiveId();
        long donorId = dependencies.getCurrentUserId();
        validateReceiver(receiverId);
        validateSelfDonate(receiverId, donorId);
        validateDuplicateDonate(receiverId, donorId);

        List<Redcard> redcardList = pickDonateRedCardList(donationRequest);
        // 기부 취소가 이루어질 수 있으니 RedCard 소유자는 변경되지 않음 -> 기부 확정 시점에 이전
        redcardService.updateRedCardStatusPending(redcardList); // RedCard 상태만 변경

        DonationGroup requestDonationGroup = createDonationGroup(donorId, receiverId, DonationType.TO_REQUEST, DonationStatus.PENDING, donationCount, donationRequest.getComment());
        Long donationGroupId = requestDonationGroup.getId();
        saveDonationDetails(redcardList, donationGroupId);
    }

    public void validateDuplicateDonate(long fundingId, long donorId) {
        boolean exists = fundingRepository.existsByFundingIdAndUserId(fundingId, donorId);

        if (exists) {
            throw new DonationDuplicateException();
        }
    }

    @Transactional
    @Override
    public void cancelDonation(long receiveId) {
        long userId = dependencies.getCurrentUserId();
        DonationGroup donationGroup = getDonationGroup(userId, receiveId, DonationStatus.PENDING);
        donationGroup.donateCancel();

        List<DonationDetail> cancelData = getDonationDetails(donationGroup.getId());
        donationRedCardCancel(cancelData);
//        dependencies.getDonationGroupRepository().save(donationGroup);
//        dependencies.getDonationGroupRepository().flush();
    }

    @Override
    public void validateDonation(List<Redcard> redcardList, DonationRequest donationRequest) {
        checkDonateAmount(redcardList, donationRequest.getQuantity());
        validateReceiver(dependencies.getCurrentUserId());
    }

    @Override
    protected void validateReceiver(long receiverId) {
        boolean exists = fundingService.existsFundingById(receiverId);
        if (!exists) {
            throw new FundingNotFoundException();
        }
    }

    @Override
    public void validateSelfDonate(long receiverId, long donorId ) {
        Funding funding = fundingRepository.findById(receiverId).orElseThrow(FundingNotFoundException::new);

        if (funding.getUserId().equals(donorId)) {
            throw new DonationNotSelfException();
        }
    }

}
