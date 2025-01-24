package com.redbox.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailVerificationCodeRepository {

    private final StringRedisTemplate stringRedisTemplate;

    public void save(String key, String value) {
        ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMinutes(5);

        valueOperations.set(key, value, expireDuration);
    }

    public Optional<String> getVerificationCodeByEmail(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void deleteByEmail(String key) {
        stringRedisTemplate.delete(key);
    }
}
