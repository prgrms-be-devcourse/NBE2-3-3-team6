package com.redbox.domain.admin.dto;

import com.redbox.domain.attach.dto.AttachFileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDetailResponse {
    private Long id;
    private String title; // 제목
    private String author; // 작성자
    private LocalDate date; // 등록일
    private LocalDate startDate; // 기부 시작일
    private LocalDate endDate; // 기부 종료일
    private int targetAmount; // 목표 수량
    private String status;
    private int views; // 조회수
    private String content; // 내용
    private List<AttachFileResponse> attachFileResponses; // 첨부 파일 리스트
}