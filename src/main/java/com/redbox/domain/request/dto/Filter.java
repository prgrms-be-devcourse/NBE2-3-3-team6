package com.redbox.domain.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Filter {
    LIKED("관심글"), NEW("최신순"), END("만료순"), HOT("인기순") ;
    private final String filter;
}
