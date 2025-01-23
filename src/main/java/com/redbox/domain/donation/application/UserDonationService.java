package com.redbox.domain.donation.application;

import com.redbox.domain.donation.dto.DonationRequest;
import com.redbox.domain.donation.dto.Top5DonorWrapper;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.entity.DonationStatus;
import com.redbox.domain.donation.entity.DonationType;
import com.redbox.domain.donation.exception.DonationAlreadyConfirmedException;
import com.redbox.domain.redcard.entity.OwnerType;
import com.redbox.domain.donation.exception.DonationNotSelfException;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.user.exception.UserNotFoundException;
import com.redbox.domain.user.repository.UserRepository;

import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class UserDonationService extends AbstractDonationService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);       // 30분
    private static final String TOP5_DONOR_KEY = "donors:top5";          // 이달의 기부왕 5명

    public UserDonationService(DonationServiceDependencies dependencies, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate) {
        super(dependencies);
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    // 서버 시작시 캐시 초기화
    // 메인페이지 기능이기 때문에
    @PostConstruct
    public void initializeCache() {
        redisTemplate.delete(TOP5_DONOR_KEY);
        updateTop5DonorsCache();
    }

    @Scheduled(cron = "0 0/30 0 * * *") // 30분 마다 실행
    public void run() {
        updateTop5DonorsCache();
    }

    @Transactional
    @Override
    public void processDonation(DonationRequest donationRequest) {
        // user 에게 기부
        int donationCount = donationRequest.getQuantity();
        long receiverId = donationRequest.getReceiveId();
        long donorId = dependencies.getCurrentUserId();
        validateReceiver(receiverId);
        validateSelfDonate(receiverId, donorId);

        List<Redcard> redcardList = pickDonateRedCardList(donationRequest);
        dependencies.getRedcardService().updateRedCardList(redcardList, receiverId, OwnerType.USER);
        DonationGroup userDonationGroup = createDonationGroup(donorId, receiverId, DonationType.TO_USER, DonationStatus.PENDING, donationCount, donationRequest.getComment());
        Long donationGroupId = userDonationGroup.getId();
        saveDonationDetails(redcardList, donationGroupId);
    }

    @Override
    public void cancelDonation(long receiveId) {
        throw new DonationAlreadyConfirmedException();
    }

    @Override
    public void validateSelfDonate(long receiveId, long donorId) {
        if (receiveId == donorId) {
            throw new DonationNotSelfException();
        }
    }

    @Override
    public void validateDonation(List<Redcard> redcardList, DonationRequest donationRequest) {
        checkDonateAmount(redcardList, donationRequest.getQuantity());
        validateReceiver(donationRequest.getReceiveId());
    }

    @Override
    protected void validateReceiver(long receiverId) {
        boolean exists = userRepository.existsById(receiverId);
        if (!exists) {
            throw new UserNotFoundException();
        }
    }

    public Top5DonorWrapper getTop5DonorsFromDB() {
        return new Top5DonorWrapper(dependencies.getDonationGroupRepository().findTop5DonorsOfTheMonth());
    }

    public Top5DonorWrapper getCachedTop5Donors() {
        try {
            Object cachedObject = redisTemplate.opsForValue().get(TOP5_DONOR_KEY);
            if (cachedObject != null) {
                return (Top5DonorWrapper) cachedObject;
            }

            Top5DonorWrapper wrapper = getTop5DonorsFromDB();
            redisTemplate.opsForValue().set(TOP5_DONOR_KEY, wrapper, CACHE_TTL);
            return wrapper;
        } catch (RedisConnectionException e) {
            log.error("Redis 연결 실패, DB에서 직접 조회합니다", e);
            return getTop5DonorsFromDB();
        }
    }

    // 캐시 갱신 로직
    private void updateTop5DonorsCache() {
        Top5DonorWrapper top5Donors = getTop5DonorsFromDB();
        try {
            redisTemplate.opsForValue().set(TOP5_DONOR_KEY, top5Donors, CACHE_TTL);
        } catch (RedisConnectionException e) {
            log.error("Redis 캐시 갱신 실패", e);
        }
    }
}
