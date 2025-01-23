package com.redbox.domain.request.repository;

import com.redbox.domain.request.dto.RequestFilter;
import com.redbox.domain.request.entity.Request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestRepositoryCustom {
    public Page<Request> searchBoards(Long userId, RequestFilter requestFilter, Pageable pageable);
}
