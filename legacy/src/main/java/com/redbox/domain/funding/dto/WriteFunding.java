package com.redbox.domain.funding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WriteFunding {

    @NotBlank(message = "제목을 입력해주세요")
    private String fundingTitle; // 게시글 제목

    @NotBlank(message = "내용을 입력해주세요")
    private String fundingContent; // 게시글 내용

    @NotNull(message = "필요한 헌혈증 개수를 입력해주세요")
    private Integer targetAmount; // 목표 개수

    @NotNull(message = "시작 일자를 입력해주세요")
    private LocalDate donationStartDate; // 기부 시작 일자

    @NotNull(message = "종료 일자를 입력해주세요")
    private LocalDate donationEndDate; // 기부 종료 일자

}
