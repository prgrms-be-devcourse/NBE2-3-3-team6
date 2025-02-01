package com.redbox.domain.user.user.facade

import com.redbox.domain.user.user.service.UserService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService
) {
    fun getActiveUserCount(): Int? {
        return userService.getActiveUserCount()
    }
}