package com.redbox.domain.attach.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.redbox.domain.attach.entity.AttachFile;
import lombok.Getter;

@Getter
@JsonDeserialize
public class AttachFileResponse {
    private Long fileNo;
    private String originFilename;
    private String filename;

    @JsonCreator
    public AttachFileResponse() {
    }

    public AttachFileResponse(AttachFile file) {
        this.fileNo = file.getId();
        this.originFilename = file.getOriginalFilename();
        this.filename = file.getNewFilename();
    }
}
