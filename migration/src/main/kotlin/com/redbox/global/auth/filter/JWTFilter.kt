package com.redbox.global.auth.filter

import com.redbox.global.auth.dto.CustomUserDetails
import com.redbox.global.auth.util.JWTUtil
import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode
import com.redbox.global.util.error.ErrorResponseUtil
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JWTFilter(
    private val jwtUtil: JWTUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val accessToken = request.getHeader("access").orEmpty()

        if (accessToken.isBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        runCatching {
            // 토큰 검증
            require(jwtUtil.getCategory(accessToken) == "access") { throw AuthException(ErrorCode.INVALID_TOKEN) }
            require(!jwtUtil.isExpired(accessToken)) { throw AuthException(ErrorCode.EXPIRED_TOKEN) }

            val customUserDetails = CustomUserDetails(
                userId = jwtUtil.getUserId(accessToken),
                email = jwtUtil.getEmail(accessToken),
                role = jwtUtil.getRole(accessToken).replace("ROLE_", "")
            )

            val authToken: Authentication = UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.authorities
            )

            SecurityContextHolder.getContext().apply {
                authentication = authToken
            }

            filterChain.doFilter(request, response)

        }.onFailure { e ->
            when (e) {
                is ExpiredJwtException -> ErrorResponseUtil.handleException(response, ErrorCode.EXPIRED_TOKEN)
                is AuthException -> ErrorResponseUtil.handleException(response, e.errorCode)
                else -> ErrorResponseUtil.handleException(response, ErrorCode.INTERNAL_SERVER_ERROR)
            }
        }
    }
}
