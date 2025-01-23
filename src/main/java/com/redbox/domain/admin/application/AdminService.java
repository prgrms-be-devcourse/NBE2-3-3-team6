package com.redbox.domain.admin.application;

import com.redbox.domain.admin.dto.AdminApproveRequest;
import com.redbox.domain.admin.dto.AdminDetailResponse;
import com.redbox.domain.admin.dto.AdminListResponse;
import com.redbox.domain.admin.dto.AdminStatsResponse;
import com.redbox.domain.admin.exception.InvalidApproveStatusException;
import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.donation.repository.DonationGroupRepository;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.exception.FundingNotFoundException;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.funding.entity.FundingStatus;
import com.redbox.domain.funding.repository.FundingRepository;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final FundingRepository fundingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RedcardRepository redcardRepository;
    private final DonationGroupRepository donationGroupRepository;

    // 요청 게시글 리스트 조회
    public List<AdminListResponse> getFundings() {
        // 요청중 리스트만 추출
        List<Funding> fundingList = fundingRepository.findByFundingStatus(FundingStatus.REQUEST);
        return fundingList.stream().map(AdminListResponse::new).collect(Collectors.toList());
    }

    // 요청 게시글 승인 or 거절
    @Transactional
    public void approveRequest(Long fundingId, AdminApproveRequest adminApproveRequest) {

        Funding changeFunding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        String approveStatus = adminApproveRequest.getApproveStatus();

        switch (approveStatus) {
            case "승인" :
                changeFunding.approve();
                changeFunding.inProgress();
                break;
            case "거절" :
                changeFunding.reject();
                changeFunding.rejectProgress();
                break;
            default:
                throw new InvalidApproveStatusException();
        }

        fundingRepository.save(changeFunding);
    }

    // 요청 게시글 상세조회
    public AdminDetailResponse getFundingDetails(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        return new AdminDetailResponse(
                funding.getFundingId(),
                funding.getFundingTitle(),
                funding.getUserName(),
                funding.getCreatedAt().toLocalDate(),
                funding.getDonationStartDate(),
                funding.getDonationEndDate(),
                funding.getTargetAmount(),
                funding.getFundingStatus().getText(),
                funding.getFundingHits(),
                funding.getFundingContent(),
                funding.getAttachFiles()
                        .stream().map(AttachFileResponse::new).toList()
        );
    }

    public List<AdminListResponse> getHotFundings() {
        return fundingRepository.findTop5FundingWithLikeCount().stream()
                .map(AdminListResponse::new).toList();
    }

    public List<AdminListResponse> getLikedFundings() {
        Long userId = userService.getCurrentUserId();

        return fundingRepository.findLikedTop5FundingsByUserId(userId).stream()
                .map(AdminListResponse::new).toList();
    }

    public AdminStatsResponse getAdminStats() {
        Integer userCount = userRepository.countActiveUser();
        Integer redcardCountInRedbox = redcardRepository.countAllInRedbox();
        Integer sumDonation = donationGroupRepository.sumDonationAmountInRedbox();
        Integer fundingCount = fundingRepository.countByFundingStatus(FundingStatus.REQUEST);

        return new AdminStatsResponse(
                userCount != null ? userCount : 0,
                redcardCountInRedbox != null ? redcardCountInRedbox : 0,
                sumDonation != null ? sumDonation : 0,
                fundingCount != null ? fundingCount : 0
        );
    }
}
