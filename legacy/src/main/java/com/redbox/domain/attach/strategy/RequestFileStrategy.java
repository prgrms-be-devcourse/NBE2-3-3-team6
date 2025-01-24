package com.redbox.domain.attach.strategy;

import com.redbox.domain.attach.entity.AttachFile;
import com.redbox.domain.attach.entity.Category;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.exception.FundingNotFoundException;
import com.redbox.domain.funding.repository.FundingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestFileStrategy implements FileAttachStrategy {

    private final FundingRepository fundingRepository;

    @Override
    public AttachFile attach(Long postId, String originalFilename, String newFilename) {
        Funding funding = fundingRepository.findById(postId)
                .orElseThrow(FundingNotFoundException::new);

        return AttachFile.builder()
                .category(Category.FUNDING)
                .funding(funding)
                .originalFilename(originalFilename)
                .newFilename(newFilename)
                .build();
    }
}
