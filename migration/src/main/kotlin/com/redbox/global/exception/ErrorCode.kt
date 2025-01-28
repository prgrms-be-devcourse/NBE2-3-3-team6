package com.redbox.global.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    // 요청 게시판 관련
    FAIL_TO_FIND_FUNDING(HttpStatus.NOT_FOUND,"해당 게시판을 찾을 수 없습니다."),

    // 현재 로그인한 회원 찾기
    FAIL_TO_FIND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다"),

    // 레드카드 관련
    DUPLICATE_SERIAL_NUMBER(HttpStatus.BAD_REQUEST, "이미 등록된 헌혈증입니다."),
    NOT_BELONG_TO_REDCARD(HttpStatus.BAD_REQUEST, "자신이 소유한 헌혈증이 아닙니다."),
    PENDING_REDCARD(HttpStatus.BAD_REQUEST, "기부 진행중인 헌혈증입니다."),
    INVALID_REDCARD_STATUS(HttpStatus.BAD_REQUEST, "올바른 헌혈증 상태가 아닙니다."),
    NOT_FOUND_REDCARD(HttpStatus.NOT_FOUND, "헌혈증을 찾을 수 없습니다"),

    // 공지사항 관련
    FAIL_TO_FIND_NOTICE(HttpStatus.NOT_FOUND,"해당 공지사항을 찾을 수 없습니다."),

    // 첨부파일 관련
    INVALID_ATTACHFILE(HttpStatus.BAD_REQUEST, "첨부파일이 비어있습니다."),
    FAIL_TO_FIND_ATTACHFILE(HttpStatus.NOT_FOUND,"첨부파일을 찾을 수 없습니다."),
    NOT_BELONG_TO_FILE(HttpStatus.BAD_REQUEST, "파일이 해당 게시글에 속하지 않습니다"),

    // 헌혈 기사 관련
    FAIL_TO_FIND_ARTICLE(HttpStatus.NOT_FOUND,"해당 기사를 찾을 수 없습니다."),

    // 회원 가입 관련
    UNVERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),

    // Email 관련 에러
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    FAIL_TO_CREATE_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 메시지 생성 중 오류가 발생했습니다."),
    FAIL_TO_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다."),
    EMAIL_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 메시지 생성 중 오류가 발생했습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송 중 오류가 발생했습니다."),

    // 수정 권한 확인
    FAIL_TO_ACCESS(HttpStatus.FORBIDDEN, "수정 권한이 없습니다"),

    // 관리자 승인 요청 게시글
    FAIL_TO_APPROVAL_STATUS(HttpStatus.NOT_FOUND, "해당 승인 상태값이 존재하지 않습니다"),

    // 비밀번호 관련 에러
    EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 비어있을 수 없습니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

    // 사용자 없음 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),

    // 사용자 정보가 잘못된 경우
    INVALID_USER_INFO(HttpStatus.BAD_REQUEST, "사용자 이름과 전화번호를 모두 입력해야 합니다."),

    // 로그인 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다."),
    EMAIL_OR_PASSWORD_MISSING(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 누락되었습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    // 탈퇴 관련
    PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다"),

    // 토큰
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "Refresh 토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // 레드박스
    REDBOX_NOT_FOUND(HttpStatus.NOT_FOUND, "Redbox 정보가 존재하지 않습니다."),
    INVALID_REDCARD_COUNT(HttpStatus.BAD_REQUEST, "보유 수량은 0보다 작을 수 없습니다."),

    // 기부
    NOT_FOUND_DONATION_TYPE(HttpStatus.NOT_FOUND, "기부 타입을 찾을 수 없습니다."),
    INVALID_DONATION_AMOUNT(HttpStatus.BAD_REQUEST, "보유한 헌혈증이 부족합니다."),
    DONATION_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "기부가 확정되어 취소할 수 없습니다."),
    NOT_FOUND_DONATION_GROUP(HttpStatus.NOT_FOUND, "해당 게시글에 대한 기부 이력이 없습니다"),
    DONATION_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 기부한 게시글입니다."),
    DONATION_NOT_SELF(HttpStatus.BAD_REQUEST, "스스로에게 기부할 수 없습니다."),

    // 기부 통계 관련 에러코드
    STATS_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기부 통계 계산 중 오류가 발생했습니다.");
}