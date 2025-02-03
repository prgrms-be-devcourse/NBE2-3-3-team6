package com.redbox.global.auth.service

import com.redbox.domain.auth.dto.CustomUserDetails
import com.redbox.domain.community.funding.exception.UserNotFoundException
import com.redbox.domain.user.user.entity.User
import com.redbox.domain.user.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userRepository: UserRepository
) {
    fun getCurrentUser(): User {
        val userDetails = getCustomUserDetails()
        return userRepository.findByEmail(userDetails.username)
            ?: throw UserNotFoundException()
    }

    fun getCurrentUserId(): Long {
        return getCustomUserDetails().getUserId()
    }

    private fun getCustomUserDetails(): CustomUserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.principal as CustomUserDetails
    }
}