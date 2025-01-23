package com.redbox.domain.attach.strategy;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.notice.entity.Notice;
import com.redbox.domain.notice.exception.NoticeNotFoundException;
import com.redbox.domain.notice.repository.NoticeRepository;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.exception.RequestNotFoundException;
import com.redbox.domain.request.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestFileStrategy implements FileAttachStrategy {

    private final RequestRepository requestRepository;

    @Override
    public AttachFile attach(Long postId, String originalFilename, String newFilename) {
        Request request = requestRepository.findById(postId)
                .orElseThrow(RequestNotFoundException::new);

        return AttachFile.builder()
                .category(Category.REQUEST)
                .request(request)
                .originalFilename(originalFilename)
                .newFilename(newFilename)
                .build();
    }
}
