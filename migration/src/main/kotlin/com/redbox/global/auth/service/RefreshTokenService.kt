package com.redbox.global.auth.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RefreshTokenService(
    private val redisTemplate: StringRedisTemplate
) {

    // Refresh Token 저장
    fun saveRefreshToken(email: String, refreshToken: String, ttl: Long) {
        // Redis에 RefreshToken 저장 (key: RefreshToken, value: email)
        redisTemplate.opsForValue().set(refreshToken, email, ttl, TimeUnit.MILLISECONDS)
    }

}
