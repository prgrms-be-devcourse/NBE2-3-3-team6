package com.redbox.global.oauth2.dto;

import com.redbox.domain.user.entity.RoleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialUserDTO {
    private RoleType role;
    private String email;
    private String name;
}
