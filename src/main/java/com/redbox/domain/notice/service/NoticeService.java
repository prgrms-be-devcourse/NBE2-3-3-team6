package com.redbox.domain.notice.service;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.notice.dto.*;
import com.redbox.domain.notice.entity.Notice;
import com.redbox.domain.notice.exception.NoticeNotFoundException;
import com.redbox.domain.notice.repository.NoticeQueryRepository;
import com.redbox.domain.notice.repository.NoticeRepository;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import com.redbox.global.infra.s3.S3Service;
import com.redbox.global.util.FileUtils;
import io.lettuce.core.RedisConnectionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeQueryRepository noticeQueryRepository;
    private final S3Service s3Service;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);       // 30분
    private static final String TOP5_NOTICES_KEY = "notices:top5";          // 공지사항 최근 게시물 리스트 5개
    private static final String NOTICE_PAGE_KEY = "notices:page:%d";        // 공지사항 1페이지 리스트
    private static final String NOTICE_HIT_KEY = "notices:hit:%d";          // 공지사항 상세글에 쌓인 조회수
    private static final String NOTICE_DETAIL_KEY = "notices:detail:%d";    // 공지사항 상세글

    // 서버 시작시 캐시 초기화
    // 메인페이지 기능이기 때문에
    @PostConstruct
    public void initializeCache() {
        // 기존 캐시 삭제
        redisTemplate.delete(TOP5_NOTICES_KEY);

        // 새로운 캐시 생성
        updateTop5NoticesCache();
    }

    private void deleteNoticeCaches(Long noticeId) {
        redisTemplate.delete(TOP5_NOTICES_KEY);                             // 공지사항 탑 5개의 글에 대한 캐시
        redisTemplate.delete(String.format(NOTICE_PAGE_KEY, 1));            // 공지사항 첫페이지에 대한 캐시
        redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, noticeId));   // 공지사항 게시글에 대한 캐시
    }

    private void deleteNoticeAllCaches(Long noticeId) {
        deleteNoticeCaches(noticeId);
        redisTemplate.delete(String.format(NOTICE_HIT_KEY, noticeId));      // 공지사항 게시글의 조회수에 대한 캐시
    }

    private void incrementHitCount(Long noticeId) {
        String hitKey = String.format(NOTICE_HIT_KEY, noticeId);
        redisTemplate.opsForValue().increment(hitKey, 1);
    }

    private void handleAttachFiles(Notice notice, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String newFilename = FileUtils.generateNewFilename();
                String extension = FileUtils.getExtension(file);
                String fullFilename = newFilename + "." + extension;
                s3Service.uploadFile(file, Category.NOTICE, notice.getId(), fullFilename);

                AttachFile attachFile = AttachFile.builder()
                        .category(Category.NOTICE)
                        .notice(notice)
                        .originalFilename(file.getOriginalFilename())
                        .newFilename(fullFilename)
                        .build();

                notice.addAttachFiles(attachFile);
            }
        }
    }

    private void deleteAttachFiles(Notice notice) {
        for (AttachFile attachFile : notice.getAttachFiles()) {
            s3Service.deleteFile(attachFile.getCategory(), notice.getId(), attachFile.getNewFilename());
        }
    }

    // Redis에서 캐시된 데이터 조회
    public NoticeListWrapper getCachedTop5Notices() {
        try {
            Object cachedObject = redisTemplate.opsForValue().get(TOP5_NOTICES_KEY);

            if (cachedObject != null) {
                return (NoticeListWrapper) cachedObject;
            }

            NoticeListWrapper notices = getTop5NoticesFromDB();
            redisTemplate.opsForValue().set(TOP5_NOTICES_KEY, notices, CACHE_TTL);
            return notices;
        } catch (RedisConnectionException e) {
            log.error("Redis 연결 실패, DB에서 직접 조회합니다", e);
            return getTop5NoticesFromDB();
        }
    }

    // 캐시 갱신 로직
    private void updateTop5NoticesCache() {
        NoticeListWrapper top5Notices = getTop5NoticesFromDB();
        try {
            redisTemplate.opsForValue().set(TOP5_NOTICES_KEY, top5Notices, CACHE_TTL);
        } catch (RedisConnectionException e) {
            log.error("Redis 캐시 갱신 실패", e);
        }
    }

    // 최신순 공지사항 5개 조회
    // DB에서 직접 조회
    public NoticeListWrapper getTop5NoticesFromDB() {
        return new NoticeListWrapper(noticeRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(notice -> new RecentNoticeResponse(
                        notice.getId(),
                        notice.getNoticeTitle(),
                        notice.getCreatedAt().toLocalDate()
                ))
                .toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<NoticeListResponse> getNotices(int page, int size) {
        // 첫페이지만 캐싱
        if (page == 1) {
            String cacheKey = String.format(NOTICE_PAGE_KEY, page);
            try {
                Object cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return (PageResponse<NoticeListResponse>) cached;
                }

                PageResponse<NoticeListResponse> response = new PageResponse<>(
                        noticeQueryRepository.findNotices(PageRequest.of(page - 1, size))
                );
                redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL);
                return response;
            } catch (RedisConnectionException e) {
                log.error("Redis 연결 실패", e);
                return new PageResponse<>(noticeQueryRepository.findNotices(PageRequest.of(page - 1, size)));
            }
        }

        // 첫페이지 외에는 DB로 가져옴
        return new PageResponse<>(noticeQueryRepository.findNotices(PageRequest.of(page - 1, size)));
    }

    @Transactional
    public NoticeResponse getNotice(Long noticeId) {
        // 조회수 레디스에 증가
        incrementHitCount(noticeId);

        // 캐시된 상세 정보 조회
        String detailKey = String.format(NOTICE_DETAIL_KEY, noticeId);
        try {
            Object cached = redisTemplate.opsForValue().get(detailKey);
            if (cached != null) {
                return (NoticeResponse) cached;
            }

            Notice notice = noticeRepository.findForDetail(noticeId)
                    .orElseThrow(NoticeNotFoundException::new);
            NoticeResponse response = new NoticeResponse(notice);

            // 조회된 공지사항 5분간 캐시
            redisTemplate.opsForValue().set(detailKey, response, CACHE_TTL);

            return response;
        } catch (RedisConnectionException e) {
            log.error("Redis 연결 실패", e);
            Notice notice = noticeRepository.findForDetail(noticeId)
                    .orElseThrow(NoticeNotFoundException::new);
            return new NoticeResponse(notice);
        }
    }

    // 30분마다 DB에 조회수 반영
    @Transactional
    @Scheduled(fixedRate = 1800000)
    public void syncHitCount() {
        Set<String> keys = redisTemplate.keys(NOTICE_HIT_KEY.replace("%d", "*"));
        if (keys == null) return;

        for (String key : keys) {
            Long noticeId = Long.parseLong(key.split(":")[2]);
            Object hitsObj = redisTemplate.opsForValue().get(key);

            if (hitsObj != null) {
                Long hits = hitsObj instanceof Integer ?
                        ((Integer) hitsObj).longValue() :
                        (Long) hitsObj;

                noticeRepository.bulkUpdateHit(noticeId, hits);
                redisTemplate.delete(key);
            }
        }
    }

    @Transactional
    public NoticeResponse createNotice(CreateNoticeRequest request, List<MultipartFile> files) {
        Notice notice = Notice.builder()
                .user(userService.getCurrentUser())
                .noticeTitle(request.getTitle())
                .noticeContent(request.getContent())
                .build();

        noticeRepository.save(notice);

        handleAttachFiles(notice, files);

        // 기존 캐시 삭제
        deleteNoticeCaches(notice.getId());
        return new NoticeResponse(notice);
    }

    @Transactional
    public NoticeResponse updateNotice(Long noticeId, UpdateNoticeRequest request) {
        Notice notice = noticeRepository.findForUpdate(noticeId)
                .orElseThrow(NoticeNotFoundException::new);
        notice.updateNotice(request);

        // 기존 캐시 삭제
        deleteNoticeCaches(noticeId);
        return new NoticeResponse(notice);
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findForDelete(noticeId)
                .orElseThrow(NoticeNotFoundException::new);

        // 파일 삭제
        deleteAttachFiles(notice);

        noticeRepository.delete(notice);
        // 기존 캐시 삭제
        deleteNoticeAllCaches(noticeId);
    }

}
