package com.redbox.domain.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redbox.domain.auth.dto.CustomUserDetails;
import com.redbox.domain.auth.service.RefreshTokenService;
import com.redbox.domain.auth.util.JWTUtil;
import com.redbox.domain.user.dto.LoginRequest;
import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;
import com.redbox.global.util.error.ErrorResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService; // 변경: RefreshTokenService 사용

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;

        // 필터 경로를 "/auth/login"으로 설정
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            if (email == null || password == null) {
                throw new AuthException(ErrorCode.EMAIL_OR_PASSWORD_MISSING);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new AuthException(ErrorCode.AUTHENTICATION_FAILED);
        } catch (AuthenticationException e) {
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        // 사용자 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();

        // JWT 생성
        String access = jwtUtil.createJwt("access", userId, email, role, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", userId, email, role, 86400000L); // 1일

        // Refresh 토큰 저장
        refreshTokenService.saveRefreshToken(email, refresh, 86400000L); // 변경: 서비스 사용

        // 응답 헤더에 토큰 추가
        response.setHeader("access", access);

        // 쿠키 생성 및 추가
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ErrorResponseUtil.handleAuthenticationError(response, failed);
    }

    // 쿠키 생성 메소드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); // 1일
        cookie.setHttpOnly(true); // JS 접근 불가
        cookie.setPath("/"); // 루트 경로에서 사용 가능
        return cookie;
    }
}
