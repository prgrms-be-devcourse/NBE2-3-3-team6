package com.redbox.domain.request.application;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.donation.application.RequestDonationService;
import com.redbox.domain.request.dto.DetailResponse;
import com.redbox.domain.request.dto.WriteRequest;
import com.redbox.domain.request.dto.RequestFilter;
import com.redbox.domain.request.dto.ListResponse;
import com.redbox.domain.request.entity.Like;
import com.redbox.domain.request.entity.Priority;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.entity.RequestStatus;
import com.redbox.domain.request.exception.RequestNotFoundException;
import com.redbox.domain.request.exception.UnauthorizedAccessException;
import com.redbox.domain.request.exception.UserNotFoundException;
import com.redbox.domain.request.repository.LikesRepository;
import com.redbox.domain.request.repository.RequestRepository;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import com.redbox.global.infra.s3.S3Service;
import com.redbox.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final S3Service s3Service;
    private final RequestRepository requestRepository;
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;

    // 현재 로그인한 사용자 정보 가져오기
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 정보가 없거나 익명 사용자일 경우 null
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user.getId();
    }

    // 페이지 처리
    public PageResponse<ListResponse> getRequests(int page, int size, RequestFilter request) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdAt").descending());
        Long userId = getCurrentUserId();

        Page<Request> boardPage = requestRepository.searchBoards(userId, request, pageable);
        Page<ListResponse> responsePage = boardPage.map(ListResponse::new);
        return new PageResponse<>(responsePage);
    }

//    // 게시글 만료 처리
//    @Transactional
//    public void updateExpiredRequests() {
//        LocalDate today = LocalDate.now();
//        List<Request> expiredRequests = requestRepository.findByDonationEndDateBeforeAndProgressNot(today, RequestStatus.EXPIRED);
//        for (Request request : expiredRequests) {
////            requestDonationService.donationConfirm(request.getRequestId(), request.getUserId());
//            request.expired();
//        }
//        requestRepository.saveAll(expiredRequests);
//    }

    // 게시글 등록
    @Transactional
    public DetailResponse createRequest(WriteRequest writeRequest, List<MultipartFile> files) {
        String name = userRepository.findNameById(getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        // 빌더 패턴을 사용하여 Request 객체 생성
        Request request = Request.builder()
                .userId(getCurrentUserId())
                .userName(name)
                .requestTitle(writeRequest.getRequestTitle())
                .requestContent(writeRequest.getRequestContent())
                .targetAmount(writeRequest.getTargetAmount())
                .requestStatus(RequestStatus.REQUEST)
                .progress(RequestStatus.REQUEST)
                .donationStartDate(writeRequest.getDonationStartDate())
                .donationEndDate(writeRequest.getDonationEndDate())
                .priority(Priority.MEDIUM) // 초기 중요도
                .build();

        requestRepository.save(request);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // S3에 파일 업로드
                String newFilename = FileUtils.generateNewFilename();
                String extension = FileUtils.getExtension(file);
                String fullFilename = newFilename + "." + extension;
                s3Service.uploadFile(file, Category.REQUEST, request.getRequestId(), fullFilename);

                // 파일 데이터 저장
                AttachFile attachFile = AttachFile.builder()
                        .category(Category.REQUEST)
                        .request(request)
                        .originalFilename(file.getOriginalFilename())
                        .newFilename(fullFilename)
                        .build();

                request.addAttachFiles(attachFile);
            }
        }

        return getRequestDetail(request.getRequestId());
    }

    // 게시글
    public DetailResponse getRequestDetail(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);

        Long userId = getCurrentUserId();

        // 좋아요 여부 조회
        Like like = likesRepository.findByUserIdAndRequestId(userId, requestId);
        boolean isLiked = like != null && like.isLiked();

        return new DetailResponse(request, isLiked);
    }

    // 게시글 상세조회 - 조회수 증가
    @Transactional
    public DetailResponse viewRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        request.incrementHits();
        return getRequestDetail(request.getRequestId());
    }

    // 좋아요 상태 변경
    @Transactional
    public void likeRequest(Long requestId) {

        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        Long userId = getCurrentUserId();

        // 좋아요 로직
        Like like = likesRepository.findByUserIdAndRequestId(userId, requestId);
        if (like != null) {
            // 존재하면 isLiked 상태 변경
            if(like.isLiked()) {
                like.falseLike();
                request.decrementLikes();
            } else {
                like.trueLike();
                request.incrementLikes();
            }
            likesRepository.save(like);
            requestRepository.save(request);
            return;
        }

        // 존재하지 않으면 새로운 Like 엔티티 생성
        Like newLike = Like.builder()
                .userId(userId)
                .requestId(requestId)
                .isLiked(true)
                .build();
        request.incrementLikes();
        likesRepository.save(newLike);
        requestRepository.save(request);
    }

    // 게시글 수정
    @Transactional
    public DetailResponse modifyRequest(Long requestId, WriteRequest writeRequest) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);

        request.updateRequest(
                writeRequest.getRequestTitle(),
                writeRequest.getRequestContent(),
                writeRequest.getDonationStartDate(),
                writeRequest.getDonationEndDate(),
                writeRequest.getTargetAmount()
        );

        Request modifyRequest = requestRepository.save(request);
        return getRequestDetail(modifyRequest.getRequestId());
    }

    // 수정 권한 확인 로직
    public void modifyAuthorize(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        Long userId = getCurrentUserId();
        if(!request.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }
    }

    public boolean existsRequestById(long requestId) {
        return requestRepository.existsById(requestId);
    }

    // 게시글 삭제
    public void deleteRequest(Long requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(RequestNotFoundException::new);
        request.drop();
        request.dropProgress();
        requestRepository.save(request);
    }
}
