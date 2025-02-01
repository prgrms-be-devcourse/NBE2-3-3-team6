package com.redbox.global.auth.service

import com.redbox.global.auth.exception.ExpiredRefreshTokenException
import com.redbox.global.auth.exception.InvalidTokenCategoryException
import com.redbox.global.auth.exception.RefreshTokenNotFoundException
import com.redbox.global.auth.util.JWTUtil
import org.springframework.stereotype.Service

@Service
class ReissueService(
    private val jwtUtil: JWTUtil,
    private val refreshTokenService: RefreshTokenService
) {

    // Refresh Token 유효성 검사
    fun validateRefreshToken(refreshToken: String?) {
        require(!refreshToken.isNullOrBlank()) { throw RefreshTokenNotFoundException() }

        refreshToken.apply {
            when {
                jwtUtil.isExpired(this) -> throw ExpiredRefreshTokenException()
                jwtUtil.getCategory(this) != "refresh" -> throw InvalidTokenCategoryException()
                !refreshTokenService.existsByRefreshToken(this) -> throw RefreshTokenNotFoundException()
            }
        }
    }
}