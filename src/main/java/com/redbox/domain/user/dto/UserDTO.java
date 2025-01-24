package com.redbox.domain.user.dto;

import com.redbox.domain.user.entity.RoleType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDTO {
    private String email;
    private String name;
    private RoleType role;

    @Builder
    public UserDTO(String name, String email, RoleType role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getRoleName() {
        return role.name();
    }
}
