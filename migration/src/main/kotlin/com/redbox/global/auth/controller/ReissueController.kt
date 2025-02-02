package com.redbox.domain.auth.controller

import com.redbox.global.auth.service.ReissueService
import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorResponse
import com.redbox.global.util.error.ErrorResponseUtil
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class ReissueController(
    private val reissueService: ReissueService
) {

    @PostMapping("/reissue")
    fun reissue(
        @RequestHeader("Refresh-Token") refreshToken: String,
        response: HttpServletResponse
    ): ResponseEntity<ErrorResponse> {
        return try {
            // 토큰 재발급
            val newAccessToken = reissueService.reissueAccessToken(refreshToken)
            val newRefreshToken = reissueService.reissueRefreshToken(refreshToken)

            // 응답에 토큰 추가
            response.setHeader("Authorization", "Bearer $newAccessToken") // 새 Access Token
            response.setHeader("Refresh-Token", newRefreshToken)         // 새 Refresh Token

            // 성공 응답에 맞는 객체를 생성
            ResponseEntity.ok(ErrorResponse("토큰 재발급 성공", "SUCCESS"))
        } catch (e: AuthException) {
            ErrorResponseUtil.createErrorResponse(e.errorCode) // 그대로 반환
        }
    }
}