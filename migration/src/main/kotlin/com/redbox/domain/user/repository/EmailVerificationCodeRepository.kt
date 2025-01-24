package com.redbox.domain.user.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class EmailVerificationCodeRepository(
    private val stringRedisTemplate: StringRedisTemplate
) {
    fun save(key: String, value: String) {
        val valueOperations: ValueOperations<String, String> = stringRedisTemplate.opsForValue()
        val expireDuration: Duration = Duration.ofMinutes(5)

        valueOperations.set(key, value, expireDuration)
    }
}