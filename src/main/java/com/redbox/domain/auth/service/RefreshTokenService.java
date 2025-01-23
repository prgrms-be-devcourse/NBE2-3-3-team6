package com.redbox.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    // Refresh Token 저장
    public void saveRefreshToken(String email, String refreshToken, long ttl) {
        // Redis에 RefreshToken 저장 (key: RefreshToken, value: email)
        redisTemplate.opsForValue().set(refreshToken, email, ttl, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 존재 여부 확인
    public boolean existsByRefreshToken(String refreshToken) {
        return redisTemplate.hasKey(refreshToken);
    }

    // Refresh Token 삭제
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    // 이메일로 Refresh Token 검색 (역방향은 Redis 기본 설계에서 제공되지 않음. 필요하다면 다른 방법으로 구현 필요)
    public String getRefreshTokenByEmail(String email) {
        throw new UnsupportedOperationException("Redis 기본 설계에서는 역방향 검색이 지원되지 않습니다.");
    }

    // Refresh Token으로 이메일 찾기
    public String getEmailByRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }
}
