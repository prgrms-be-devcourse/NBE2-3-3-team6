package com.redbox.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckUserResponse {
    private Long userId;
    private String name;
}
