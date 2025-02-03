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
    fun reissueAccessToken(refreshToken: String): String {
        validateRefreshToken(refreshToken)

        val userId = jwtUtil.getUserId(refreshToken)
        val email = jwtUtil.getEmail(refreshToken)
        val role = jwtUtil.getRole(refreshToken)

        // Access Token 생성 (유효 시간: 10분)
        return jwtUtil.createJwt("access", userId, email, role, 600_000L)
    }

    fun reissueRefreshToken(refreshToken: String): String {
        validateRefreshToken(refreshToken)

        val userId = jwtUtil.getUserId(refreshToken)
        val email = jwtUtil.getEmail(refreshToken)
        val role = jwtUtil.getRole(refreshToken)

        // 새 Refresh Token 생성 (유효 시간: 1일)
        val newRefreshToken = jwtUtil.createJwt("refresh", userId, email, role, 86_400_000L)

        // 기존 Refresh Token 삭제 후 새로운 Refresh Token 저장
        refreshTokenService.deleteRefreshToken(refreshToken)
        refreshTokenService.saveRefreshToken(email, newRefreshToken, 86_400_000L)

        return newRefreshToken
    }

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