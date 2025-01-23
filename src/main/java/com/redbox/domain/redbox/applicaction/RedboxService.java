package com.redbox.domain.redbox.applicaction;

import com.redbox.domain.donation.application.AbstractDonationService;
import com.redbox.domain.donation.application.DonationServiceDependencies;
import com.redbox.domain.donation.dto.DonationRequest;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.entity.DonationStatus;
import com.redbox.domain.donation.entity.DonationType;
import com.redbox.domain.donation.exception.DonationAlreadyConfirmedException;
import com.redbox.domain.redbox.dto.RedboxStatsResponse;
import com.redbox.domain.redcard.entity.OwnerType;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.request.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RedboxService extends AbstractDonationService {

    private final RequestRepository requestRepository;
    private final RedcardRepository redcardRepository;


    public RedboxService(DonationServiceDependencies dependencies,
                         RequestRepository requestRepository,
                         RedcardRepository redcardRepository
                         ) {
        super(dependencies); // 부모 클래스 생성자 호출

        this.requestRepository = requestRepository;
        this.redcardRepository = redcardRepository;
    }

    public RedboxStatsResponse getRedboxStats() {
        // Redbox 테이블의 totalCount 필드에서 누적 개수를 가져옵니다.
        // 이제 redcard에서 카운팅
        Integer totalDonatedCards = redcardRepository.countAllInRedbox();
        totalDonatedCards = (totalDonatedCards != null) ? totalDonatedCards : 0;

        // 도움받은 환자 수를 DonationGroup 테이블에서 계산합니다.
        Integer helpedPatients = dependencies.getDonationGroupRepository().getHelpedPatientsCount();
        helpedPatients = (helpedPatients != null) ? helpedPatients : 0;

        // 진행 중인 요청 게시글 수 조회
        int inProgressRequests = requestRepository.countAllInProgressRequests();

        return new RedboxStatsResponse(totalDonatedCards, helpedPatients, inProgressRequests);
    }

    @Override
    @Transactional
    public void processDonation(DonationRequest donationRequest) {
        int donationCount = donationRequest.getQuantity();
        long donorId = dependencies.getCurrentUserId();
        long receiverId = 0L;  // redbox 일 경우 0

        List<Redcard> redcardList = pickDonateRedCardList(donationRequest);
        // 헌혈증 보유자 수정
        dependencies.getRedcardService().updateRedCardList(redcardList, receiverId, OwnerType.REDBOX);
        // 레드박스 기부 기록 생성 & 저장
        DonationGroup redboxDonationGroup = createDonationGroup(donorId, receiverId, DonationType.TO_USER, DonationStatus.DONE,donationCount, donationRequest.getComment());
        // 레드박스 디테일 생성 & 저장
        Long donationGroupId = redboxDonationGroup.getId();
        saveDonationDetails(redcardList, donationGroupId);
        // 레드박스 보유량 수정
        // -> 이제 redcard를 카운팅
    }

    @Override
    public void cancelDonation(long receiveId) {
        throw new DonationAlreadyConfirmedException();
    }

    @Override
    public void validateSelfDonate(long receiveId, long donorId) {
    }

    @Override
    public void validateDonation(List<Redcard> redcardList, DonationRequest donationRequest) {
        checkDonateAmount(redcardList, donationRequest.getQuantity());
    }

    @Override
    protected void validateReceiver(long receiverId) {
    }
}