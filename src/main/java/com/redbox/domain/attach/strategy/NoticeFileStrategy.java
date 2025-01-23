package com.redbox.domain.attach.strategy;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.notice.entity.Notice;
import com.redbox.domain.notice.exception.NoticeNotFoundException;
import com.redbox.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeFileStrategy implements FileAttachStrategy {

    private final NoticeRepository noticeRepository;

    @Override
    public AttachFile attach(Long postId, String originalFilename, String newFilename) {
        Notice notice = noticeRepository.findById(postId)
                .orElseThrow(NoticeNotFoundException::new);

        return AttachFile.builder()
                .category(Category.NOTICE)
                .notice(notice)
                .originalFilename(originalFilename)
                .newFilename(newFilename)
                .build();
    }
}
