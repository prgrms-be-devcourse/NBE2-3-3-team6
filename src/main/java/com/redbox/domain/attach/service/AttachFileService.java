package com.redbox.domain.attach.service;

import com.redbox.domain.attach.dto.AttachFileResponse;
import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.attach.exception.AttachFileNotFoundException;
import com.redbox.domain.attach.exception.FileNotBelongException;
import com.redbox.domain.attach.repository.AttachFileRepository;
import com.redbox.domain.attach.strategy.FileAttachStrategy;
import com.redbox.domain.attach.strategy.FileAttachStrategyFactory;
import com.redbox.domain.notice.repository.NoticeRepository;
import com.redbox.global.infra.s3.S3Service;
import com.redbox.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachFileService {

    private final AttachFileRepository attachFileRepository;
    private final S3Service s3Service;
    private final NoticeRepository noticeRepository;
    private final FileAttachStrategyFactory fileAttachStrategyFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String NOTICE_DETAIL_KEY = "notices:detail:%d";    // 공지사항 상세글

    public String getFileDownloadUrl(Long postId, Long fileId) {
        AttachFile attachFile = attachFileRepository.findById(fileId)
                .orElseThrow(AttachFileNotFoundException::new);

        validateFileOwnership(attachFile, postId);

        // PreSignedURL 생성
        return s3Service.generatePresignedUrl(
                attachFile.getCategory(),
                postId,
                attachFile.getNewFilename(),
                attachFile.getOriginalFilename()
        );
    }

    private void validateFileOwnership(AttachFile attachFile, Long postId) {
        if (!attachFile.belongToPost(postId)) {
            throw new FileNotBelongException();
        }
    }

    @Transactional
    public AttachFileResponse addFile(Category category, Long postId, MultipartFile file) {
        // S3에 파일 업로드
        String newFilename = FileUtils.generateNewFilename();
        String extension = FileUtils.getExtension(file);
        String fullFilename = newFilename + "." + extension;
        s3Service.uploadFile(file, category, postId, fullFilename);

        // 카테고리에 맞는 전략 사용
        FileAttachStrategy strategy = fileAttachStrategyFactory.getStrategy(category);
        AttachFile attachFile = strategy.attach(postId, file.getOriginalFilename(), fullFilename);
        attachFileRepository.save(attachFile);

        if (category.equals(Category.NOTICE)) {
            redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, postId));
        }
        return new AttachFileResponse(attachFile);
    }

    @Transactional
    public void removeFile(Category category, Long postId, Long fileId) {
        AttachFile attachFile = attachFileRepository.findById(fileId)
                .orElseThrow(AttachFileNotFoundException::new);

        validateFileOwnership(attachFile, postId);

        s3Service.deleteFile(category, postId, attachFile.getNewFilename());

        if (category.equals(Category.NOTICE)) {
            redisTemplate.delete(String.format(NOTICE_DETAIL_KEY, postId));
        }
        attachFileRepository.delete(attachFile);
    }
}
