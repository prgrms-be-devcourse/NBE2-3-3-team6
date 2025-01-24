package com.redbox.global.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import software.amazon.awssdk.services.s3.model.S3Exception

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    // 비즈니스 관련 에러 처리
    @ExceptionHandler(BusinessException::class)
    fun handleCustomException(e: BusinessException): ResponseEntity<ErrorResponse>{
        return ResponseEntity
            .status(e.errorCodes.status)
            .body(
                ErrorResponse(
                    e.errorCodes.message,
                    e.errorCodes.status.toString(),
                )
            )
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(AuthException::class)
    fun handleAuthException(e: AuthException): ResponseEntity<ErrorResponse> {
        val error = e.errorCode
        return ResponseEntity
            .status(error.status)
            .body(ErrorResponse(error.message, error.status.toString()))
    }

    // DB 관련 예외 처리
    @ExceptionHandler(DataAccessException::class)
    protected fun handleDataAccessException(e: DataAccessException?): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    "데이터베이스 오류가 발생했습니다.",
                    HttpStatus.INTERNAL_SERVER_ERROR.toString()
                )
            )
    }

    // @Valid 검증 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    ex.bindingResult
                        .fieldErrors[0]
                        .defaultMessage!!,
                    HttpStatus.BAD_REQUEST.toString()
                )
            )
    }

    // JSON 파싱 실패 시 (잘못된 데이터 형식) 처리
    // enum 값이 잘못된 경우
    // 날짜 형식이 잘못된 경우
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException?): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    "잘못된 형식의 요청입니다.",
                    HttpStatus.BAD_REQUEST.toString()
                )
            )
    }

    // S3 관련 예외 처리
    @ExceptionHandler(S3Exception::class)
    protected fun handleS3Exception(e: S3Exception?): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    "파일 처리 중 오류가 발생했습니다.",
                    HttpStatus.INTERNAL_SERVER_ERROR.toString()
                )
            )
    }

    // 시스템 관련 에러 처리
    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(e: IllegalStateException): ResponseEntity<ErrorResponse> {
        log.error("System Error occurred: ${e.message}")
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    "시스템 오류가 발생했습니다",
                    "SYSTEM_ERROR"
                )
            )
    }
}