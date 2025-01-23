package com.redbox.domain.funding.application;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.funding.dto.DetailResponse;
import com.redbox.domain.funding.dto.WriteFunding;
import com.redbox.domain.funding.dto.FundingFilter;
import com.redbox.domain.funding.dto.ListResponse;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.entity.Like;
import com.redbox.domain.funding.entity.Priority;
import com.redbox.domain.funding.entity.FundingStatus;
import com.redbox.domain.funding.exception.FundingNotFoundException;
import com.redbox.domain.funding.exception.UnauthorizedAccessException;
import com.redbox.domain.funding.exception.UserNotFoundException;
import com.redbox.domain.funding.repository.LikesRepository;
import com.redbox.domain.funding.repository.FundingRepository;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.repository.UserRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundingService {

    private final S3Service s3Service;
    private final FundingRepository fundingRepository;
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
    public PageResponse<ListResponse> getFundings(int page, int size, FundingFilter funding) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdAt").descending());
        Long userId = getCurrentUserId();

        Page<Funding> boardPage = fundingRepository.searchBoards(userId, funding, pageable);
        Page<ListResponse> responsePage = boardPage.map(ListResponse::new);
        return new PageResponse<>(responsePage);
    }

    // 게시글 등록
    @Transactional
    public DetailResponse createFunding(WriteFunding writeFunding, List<MultipartFile> files) {
        String name = userRepository.findNameById(getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        // 빌더 패턴을 사용하여 Funding 객체 생성
        Funding funding = Funding.builder()
                .userId(getCurrentUserId())
                .userName(name)
                .fundingTitle(writeFunding.getFundingTitle())
                .fundingContent(writeFunding.getFundingContent())
                .targetAmount(writeFunding.getTargetAmount())
                .fundingStatus(FundingStatus.REQUEST)
                .progress(FundingStatus.REQUEST)
                .donationStartDate(writeFunding.getDonationStartDate())
                .donationEndDate(writeFunding.getDonationEndDate())
                .priority(Priority.MEDIUM) // 초기 중요도
                .build();

        fundingRepository.save(funding);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // S3에 파일 업로드
                String newFilename = FileUtils.generateNewFilename();
                String extension = FileUtils.getExtension(file);
                String fullFilename = newFilename + "." + extension;
                s3Service.uploadFile(file, Category.FUNDING, funding.getFundingId(), fullFilename);

                // 파일 데이터 저장
                AttachFile attachFile = AttachFile.builder()
                        .category(Category.FUNDING)
                        .funding(funding)
                        .originalFilename(file.getOriginalFilename())
                        .newFilename(fullFilename)
                        .build();

                funding.addAttachFiles(attachFile);
            }
        }

        return getFundingDetail(funding.getFundingId());
    }

    // 게시글
    public DetailResponse getFundingDetail(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);

        Long userId = getCurrentUserId();

        // 좋아요 여부 조회
        Like like = likesRepository.findByUserIdAndFundingId(userId, fundingId);
        boolean isLiked = like != null && like.isLiked();

        return new DetailResponse(funding, isLiked);
    }

    // 게시글 상세조회 - 조회수 증가
    @Transactional
    public DetailResponse viewFunding(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        funding.incrementHits();
        return getFundingDetail(funding.getFundingId());
    }

    // 좋아요 상태 변경
    @Transactional
    public void likeRequest(Long fundingId) {

        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        Long userId = getCurrentUserId();

        // 좋아요 로직
        Like like = likesRepository.findByUserIdAndFundingId(userId, fundingId);
        if (like != null) {
            // 존재하면 isLiked 상태 변경
            if(like.isLiked()) {
                like.falseLike();
                funding.decrementLikes();
            } else {
                like.trueLike();
                funding.incrementLikes();
            }
            likesRepository.save(like);
            fundingRepository.save(funding);
            return;
        }

        // 존재하지 않으면 새로운 Like 엔티티 생성
        Like newLike = Like.builder()
                .userId(userId)
                .fundingId(fundingId)
                .isLiked(true)
                .build();
        funding.incrementLikes();
        likesRepository.save(newLike);
        fundingRepository.save(funding);
    }

    // 게시글 수정
    @Transactional
    public DetailResponse modifyFunding(Long fundingId, WriteFunding writeFunding) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);

        funding.updateFunding(
                writeFunding.getFundingTitle(),
                writeFunding.getFundingContent(),
                writeFunding.getDonationStartDate(),
                writeFunding.getDonationEndDate(),
                writeFunding.getTargetAmount()
        );

        Funding modifyFunding = fundingRepository.save(funding);
        return getFundingDetail(modifyFunding.getFundingId());
    }

    // 수정 권한 확인 로직
    public void modifyAuthorize(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        Long userId = getCurrentUserId();
        if(!funding.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }
    }

    public boolean existsFundingById(long fundingId) {
        return fundingRepository.existsById(fundingId);
    }

    // 게시글 삭제
    public void deleteFunding(Long fundingId) {
        Funding funding = fundingRepository.findById(fundingId).orElseThrow(FundingNotFoundException::new);
        funding.drop();
        funding.dropProgress();
        fundingRepository.save(funding);
    }
}
