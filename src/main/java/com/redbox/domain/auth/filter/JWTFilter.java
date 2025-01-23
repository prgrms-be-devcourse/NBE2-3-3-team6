package com.redbox.domain.auth.filter;

import com.redbox.domain.auth.dto.CustomUserDetails;
import com.redbox.domain.auth.exception.InvalidTokenException;
import com.redbox.domain.auth.util.JWTUtil;
import com.redbox.domain.user.entity.RoleType;
import com.redbox.domain.user.entity.User;
import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.ErrorCode;
import com.redbox.global.util.error.ErrorResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access 키에 담긴 토큰을 가져옴
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 토큰 만료 여부 확인
            jwtUtil.isExpired(accessToken);

            // 토큰의 카테고리가 'access'인지 확인
            String category = jwtUtil.getCategory(accessToken);
            if (!"access".equals(category)) {
                throw new InvalidTokenException();
            }

            // email과 role 값을 토큰에서 가져옴
            Long userId = jwtUtil.getUserId(accessToken);
            String email = jwtUtil.getEmail(accessToken);
            String role = jwtUtil.getRole(accessToken);
            role = role.replace("ROLE_", "");

            User user = User.builder()
                    .id(userId)
                    .email(email)
                    .roleType(RoleType.valueOf(role)) // RoleType 설정
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            // 인증 정보 생성 후 SecurityContext에 저장
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 다음 필터로 전달
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            ErrorResponseUtil.handleException(response, ErrorCode.EXPIRED_TOKEN);
        } catch (AuthException e) {
            ErrorResponseUtil.handleException(response, e.getErrorCode());
        } catch (Exception e) {
            ErrorResponseUtil.handleException(response, ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
