package com.redbox.domain.admin.application;

import com.redbox.domain.admin.dto.AdminApproveRequest;
import com.redbox.domain.admin.dto.AdminDetailResponse;
import com.redbox.domain.admin.dto.AdminListResponse;
import com.redbox.domain.admin.dto.AdminStatsResponse;
import com.redbox.domain.admin.exception.InvalidApproveStatusException;
import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.donation.repository.DonationGroupRepository;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.entity.RequestStatus;
import com.redbox.domain.request.exception.RequestNotFoundException;
import com.redbox.domain.request.repository.RequestRepository;
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

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RedcardRepository redcardRepository;
    private final DonationGroupRepository donationGroupRepository;

    // 요청 게시글 리스트 조회
    public List<AdminListResponse> getRequests() {
        // 요청중 리스트만 추출
        List<Request> requestList = requestRepository.findByRequestStatus(RequestStatus.REQUEST);
        return requestList.stream().map(AdminListResponse::new).collect(Collectors.toList());
    }

    // 요청 게시글 승인 or 거절
    @Transactional
    public void approveRequest(Long requestId, AdminApproveRequest adminApproveRequest) {

        Request changeRequest = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        String approveStatus = adminApproveRequest.getApproveStatus();

        switch (approveStatus) {
            case "승인" :
                changeRequest.approve();
                changeRequest.inProgress();
                break;
            case "거절" :
                changeRequest.reject();
                changeRequest.rejectProgress();
                break;
            default:
                throw new InvalidApproveStatusException();
        }

        requestRepository.save(changeRequest);
    }

    // 요청 게시글 상세조회
    public AdminDetailResponse getRequestDetails(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        return new AdminDetailResponse(
                request.getRequestId(),
                request.getRequestTitle(),
                request.getUserName(),
                request.getCreatedAt().toLocalDate(),
                request.getDonationStartDate(),
                request.getDonationEndDate(),
                request.getTargetAmount(),
                request.getRequestStatus().getText(),
                request.getRequestHits(),
                request.getRequestContent(),
                request.getAttachFiles()
                        .stream().map(AttachFileResponse::new).toList()
        );
    }

    public List<AdminListResponse> getHotBoards() {
        return requestRepository.findTop5RequestWithLikeCount().stream()
                .map(AdminListResponse::new).toList();
    }

    public List<AdminListResponse> getLikedBoards() {
        Long userId = userService.getCurrentUserId();

        return requestRepository.findLikedTop5RequestsByUserId(userId).stream()
                .map(AdminListResponse::new).toList();
    }

    public AdminStatsResponse getAdminStats() {
        Integer userCount = userRepository.countActiveUser();
        Integer redcardCountInRedbox = redcardRepository.countAllInRedbox();
        Integer sumDonation = donationGroupRepository.sumDonationAmountInRedbox();
        Integer requestCount = requestRepository.countByRequestStatus(RequestStatus.REQUEST);

        return new AdminStatsResponse(
                userCount != null ? userCount : 0,
                redcardCountInRedbox != null ? redcardCountInRedbox : 0,
                sumDonation != null ? sumDonation : 0,
                requestCount != null ? requestCount : 0
        );
    }
}
