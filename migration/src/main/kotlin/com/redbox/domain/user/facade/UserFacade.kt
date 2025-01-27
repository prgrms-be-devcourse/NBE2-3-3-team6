package com.redbox.domain.user.facade

import com.redbox.domain.user.entity.User
import com.redbox.domain.user.service.UserService
import org.springframework.stereotype.Component

@Component
class UserFacade(
    private val userService: UserService
) {
    // 현재 로그인한 사용자의 전체 정보 조회
    fun getCurrentUser(): User {
        return userService.getCurrentUser()
    }

    // 현재 로그인한 user_id
    fun getCurrentUserId(): Long {
        return userService.getCurrentUserId()
    }
}