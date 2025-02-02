package com.redbox.global.entity

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.data.domain.Page

@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val currentPage: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
) {
    constructor(page: Page<T>) : this(
        content = page.content,
        currentPage = page.number + 1, // 0부터 시작하므로 1을 더해줌
        size = page.size,
        totalElements = page.totalElements,
        totalPages = page.totalPages
    )
}