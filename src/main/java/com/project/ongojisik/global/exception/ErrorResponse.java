package com.project.ongojisik.global.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String code,
        String message,
        String path,
        LocalDateTime timestamp
) {

    public static ErrorResponse from(ErrorCode errorCode, String path) {
        return new ErrorResponse(
                errorCode.getActualStatusCode(),
                errorCode.getCode(),
                errorCode.getMessage(),
                path,
                LocalDateTime.now()
        );
    }
}
