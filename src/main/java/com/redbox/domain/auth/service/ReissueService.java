package com.redbox.domain.auth.service;

import com.redbox.domain.auth.exception.ExpiredRefreshTokenException;
import com.redbox.domain.auth.exception.RefreshTokenNotFoundException;
import com.redbox.domain.auth.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public String reissueAccessToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long userId = jwtUtil.getUserId(refreshToken);
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        return jwtUtil.createJwt("access", userId, email, role, 600_000L); // 10분
    }

    public String reissueRefreshToken(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long userId = jwtUtil.getUserId(refreshToken);
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String newRefreshToken = jwtUtil.createJwt("refresh", userId, email, role, 86_400_000L); // 1일
        refreshTokenService.deleteRefreshToken(refreshToken); // 기존 토큰 삭제
        refreshTokenService.saveRefreshToken(email, newRefreshToken, 86_400_000L); // 새 토큰 저장

        return newRefreshToken;
    }

    private void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new RefreshTokenNotFoundException(); // 커스텀 예외
        }

        if (jwtUtil.isExpired(refreshToken)) {
            throw new ExpiredRefreshTokenException(); // 커스텀 예외
        }

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
            throw new IllegalArgumentException("Invalid refresh token category");
        }

        if (!refreshTokenService.existsByRefreshToken(refreshToken)) {
            throw new RefreshTokenNotFoundException(); // 커스텀 예외
        }
    }
}

