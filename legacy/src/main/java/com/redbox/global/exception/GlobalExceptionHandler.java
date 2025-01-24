package com.redbox.global.exception;

import com.redbox.global.infra.s3.S3Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 관련 예러 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessException e) {
        return ResponseEntity
                .status(e.getErrorCodes().getStatus())
                .body(new ErrorResponse(
                        e.getErrorCodes().getMessage(),
                        e.getErrorCodes().getStatus().toString()
                ));
    }

    // 인증 관련 예외 처리
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException e) {
        ErrorCode error = e.getErrorCode();
        return ResponseEntity
                .status(error.getStatus())
                .body(new ErrorResponse(error.getMessage(), error.getStatus().toString()));
    }

    // DB 관련 예외 처리
    @ExceptionHandler(DataAccessException.class)
    protected ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("데이터베이스 오류가 발생했습니다."
                        , HttpStatus.INTERNAL_SERVER_ERROR.toString()
                ));
    }

    // @Valid 검증 실패 시 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        ex.getBindingResult()
                                .getFieldErrors()
                                .get(0)
                                .getDefaultMessage(),
                        HttpStatus.BAD_REQUEST.toString()
                ));
    }

    // JSON 파싱 실패 시 (잘못된 데이터 형식) 처리
    // enum 값이 잘못된 경우
    // 날짜 형식이 잘못된 경우
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "잘못된 형식의 요청입니다.",
                        HttpStatus.BAD_REQUEST.toString()
                ));
    }

    // S3 관련 예외 처리
    @ExceptionHandler(S3Exception.class)
    protected ResponseEntity<ErrorResponse> handleS3Exception(S3Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "파일 처리 중 오류가 발생했습니다.",
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                ));
    }

    // 시스템 관련 에러 처리
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("System Error occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "시스템 오류가 발생했습니다",
                        "SYSTEM_ERROR"));
    }
}
