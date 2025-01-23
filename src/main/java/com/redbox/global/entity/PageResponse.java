package com.redbox.global.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@JsonDeserialize
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int size;
    private long totalElements;
    private int totalPages;

    @JsonCreator
    public PageResponse() {
    }

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber() + 1;  // 0부터 시작하므로 1을 더해줌
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
