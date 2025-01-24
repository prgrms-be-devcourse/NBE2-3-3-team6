package com.redbox.global.util.error

import com.fasterxml.jackson.databind.ObjectMapper
import com.redbox.global.exception.AuthException
import com.redbox.global.exception.ErrorCode
import com.redbox.global.exception.ErrorResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import java.io.IOException

class ErrorResponseUtil {
    companion object {
        private val objectMapper = ObjectMapper()

        fun createErrorResponse(errorCode: ErrorCode): ResponseEntity<ErrorResponse> {
            val errorResponse = ErrorResponse(errorCode.message, errorCode.name)
            return ResponseEntity.status(errorCode.status.value()).body(errorResponse)
        }

        @Throws(IOException::class)
        fun handleException(response: HttpServletResponse, errorCode: ErrorCode) {
            response.status = errorCode.status.value()
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"

            val errorResponse = ErrorResponse(errorCode.message, errorCode.name)
            val jsonResponse = objectMapper.writeValueAsString(errorResponse)
            response.writer.write(jsonResponse)
        }

        @Throws(IOException::class)
        fun handleAuthenticationError(response: HttpServletResponse, exception: Exception) {
            val errorResponse = if (exception.cause is AuthException) {
                val authException = exception.cause as AuthException
                val errorCode = authException.errorCode
                ErrorResponse(errorCode.message, errorCode.name)
            } else {
                ErrorResponse("Authentication failed", "UNAUTHORIZED")
            }

            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"

            val jsonResponse = objectMapper.writeValueAsString(errorResponse)
            response.writer.write(jsonResponse)
        }
    }
}