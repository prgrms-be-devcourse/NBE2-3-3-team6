package com.redbox.global.util.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redbox.global.exception.AuthException;
import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;
import com.redbox.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class ErrorResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ResponseEntity를 반환하는 메서드
    public static ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode) {
        ErrorResponse errorResponse = new ErrorResponse(errorCode.getMessage(), errorCode.name());
        return ResponseEntity.status(errorCode.getStatus().value()).body(errorResponse);
    }

    // 일반적인 예외 처리 메서드
    public static void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode.getMessage(), errorCode.name());
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    // 인증 실패 예외 처리 메서드
    public static void handleAuthenticationError(HttpServletResponse response, Exception exception) throws IOException {
        ErrorResponse errorResponse;

        if (exception.getCause() instanceof AuthException) {
            AuthException authException = (AuthException) exception.getCause();
            ErrorCode errorCode = authException.getErrorCode(); // 수정된 부분

            errorResponse = new ErrorResponse(errorCode.getMessage(), errorCode.name());
        } else {
            errorResponse = new ErrorResponse("Authentication failed", "UNAUTHORIZED");
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
