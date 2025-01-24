package com.redbox.domain.funding.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Priority {

    HIGH("상"),MEDIUM("중"),LOW("하");
    private final String text;

}
