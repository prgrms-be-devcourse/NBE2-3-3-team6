package com.redbox.global.auth.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(
    @Value("\${jwt_secret}") secret: String
) {
    private val secretKey: SecretKey =
        SecretKeySpec(
            secret.toByteArray(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.jcaName
        )

    // userId 추출 메소드
    fun getUserId(token: String): Long {
        val claims = parseToken(token)
        val userId = claims["userId"]

        return when (userId) {
            is Int -> {
                userId.toLong() // Integer로 저장된 경우 Long으로 변환
            }
            else -> {
                userId as Long // 원래 Long이면 그대로 반환
            }
        }
    }


    // email 추출
    fun getEmail(token: String): String {
        val claims = parseToken(token)
        return claims["email", String::class.java]
    }

    // role 추출
    fun getRole(token: String): String {
        val claims = parseToken(token)
        return claims["role", String::class.java]
    }

    // category 추출 (access / refresh 구분)
    fun getCategory(token: String): String {
        val claims = parseToken(token)
        return claims["category", String::class.java]
    }

    // 토큰 만료 여부 확인
    fun isExpired(token: String): Boolean {
        val claims = parseToken(token)
        return claims.expiration.before(Date())
    }

    // JWT 생성
    fun createJwt(
        category: String,
        userId: Long,
        email: String,
        role: String,
        expiredMs: Long
    ): String {
        return Jwts.builder().apply {
            claim("category", category)
            claim("userId", userId)
            claim("email", email)
            claim("role", role)
            setIssuedAt(Date(System.currentTimeMillis()))
            setExpiration(Date(System.currentTimeMillis() + expiredMs))
            signWith(secretKey, SignatureAlgorithm.HS256)
        }.compact()
    }

    // 토큰 파싱
    private fun parseToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
