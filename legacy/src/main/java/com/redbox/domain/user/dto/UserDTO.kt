package com.redbox.domain.user.dto

import com.redbox.domain.user.entity.RoleType

data class UserDTO(
    val name: String,
    val email: String,
    val role: RoleType
) {
    val roleName: String
        get() = role.name
}
