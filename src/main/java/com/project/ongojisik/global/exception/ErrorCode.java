package com.project.ongojisik.global.exception;

public enum ErrorCode {

    // 400 오류
    INVALID_REQUEST(400, "INVALID_REQUEST", "요청이 올바르지 않습니다."),
    MISSING_REQUIRED_FIELD(400, "MISSING_REQUIRED_FIELD", "필수 입력값이 누락되었습니다."),
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "입력값이 유효하지 않습니다."),
    VALIDATION_FAILED(400, "VALIDATION_FAILED", "요청 데이터가 유효하지 않습니다."),
    DUPLICATED_USER(400, "DUPLICATED_USER", "이미 등록된 사용자입니다."),

    // 401 오류
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    INVALID_CREDENTIALS(401, "INVALID_CREDENTIALS", "인증 정보가 올바르지 않습니다."),
    TOKEN_EXTRACTION_FAILED(401, "TOKEN_EXTRACTION_FAILED", "토큰 추출에 실패했습니다."),
    TOKEN_INVALID_TYPE(401, "TOKEN_INVALID_TYPE", "토큰 타입이 올바르지 않습니다."),
    TOKEN_INVALID(401, "TOKEN_INVALID", "토큰이 유효하지 않습니다."),
    ACCESS_TOKEN_INVALID(401, "ACCESS_TOKEN_INVALID", "액세스 토큰이 유효하지 않습니다."),
    REFRESH_TOKEN_INVALID(401, "REFRESH_TOKEN_INVALID", "리프레시 토큰이 유효하지 않습니다."),
    GOOGLE_INVALID_TOKEN(401, "GOOGLE_INVALID_TOKEN", "유효하지 않은 구글 ID 토큰입니다."),
    GOOGLE_ACCOUNT_INFO_INVALID(401, "GOOGLE_ACCOUNT_INFO_INVALID", "구글 계정 정보가 유효하지 않습니다."),

    // 403 오류
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),
    ACCESS_DENIED(403, "ACCESS_DENIED", "이 기능에 접근할 수 없습니다."),

    // 404 오류
    NOT_FOUND(404, "NOT_FOUND", "요청한 자원이 존재하지 않습니다."),
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "해당 사용자를 찾을 수 없습니다."),
    BOARD_NOT_FOUND(404, "BOARD_NOT_FOUND", "게시글을 찾을 수 없습니다."),

    // 403 오류
    BOARD_FORBIDDEN(403, "BOARD_FORBIDDEN", "게시글을 수정하거나 삭제할 권한이 없습니다."),

    //405 오류
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),

    //500 오류
    GOOGLE_CLIENT_ID_NOT_CONFIGURED(500, "GOOGLE_CLIENT_ID_NOT_CONFIGURED", "구글 OAuth 클라이언트 ID가 설정되지 않았습니다."),
    GOOGLE_TOKEN_VERIFICATION_FAILED(500, "GOOGLE_TOKEN_VERIFICATION_FAILED", "구글 토큰 검증 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    UNKNOWN_ERROR(500, "UNKNOWN_ERROR", "예기치 못한 오류가 발생했습니다.");

    private final Integer customStatusCode;
    private final String code;
    private final String message;

    ErrorCode(Integer customStatusCode, String code, String message) {
        this.customStatusCode = customStatusCode;
        this.code = code;
        this.message = message;
    }

    public Integer getActualStatusCode() {
        return customStatusCode;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
