package com.project.ongojisik.global.exception;

public class APIException extends CustomException {

    public APIException(ErrorCode errorCode) {
        super(errorCode);
    }

    public APIException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
