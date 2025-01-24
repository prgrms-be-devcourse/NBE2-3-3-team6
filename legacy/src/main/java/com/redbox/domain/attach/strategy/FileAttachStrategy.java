package com.redbox.domain.attach.strategy;

import com.redbox.domain.attach.entity.AttachFile;

public interface FileAttachStrategy {
    AttachFile attach(Long postId, String originalFilename, String newFilename);
}
