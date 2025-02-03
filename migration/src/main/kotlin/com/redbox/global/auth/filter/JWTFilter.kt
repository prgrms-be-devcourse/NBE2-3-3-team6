package com.redbox.global.auth.filter

import com.redbox.global.auth.util.JWTUtil
import com.redbox.domain.auth.dto.CustomUserDetails
import com.redbox.domain.user.user.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.security.SignatureException

@Component
class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = getTokenFromRequest(request)

        if (token != null) {
            try {
                if (!jwtUtil.isExpired(token)) {
                    val email = jwtUtil.getEmail(token)
                    val user = userRepository.findByEmail(email)

                    user?.let {
                        val userDetails: UserDetails = CustomUserDetails(it)
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (e: ExpiredJwtException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었습니다.")
                return
            } catch (e: SignatureException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 서명이 유효하지 않습니다.")
                return
            } catch (e: MalformedJwtException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "잘못된 JWT 형식입니다.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}
