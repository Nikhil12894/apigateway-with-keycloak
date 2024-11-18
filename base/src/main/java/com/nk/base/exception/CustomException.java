package com.nk.base.exception;

public class CustomException extends RuntimeException {
    private final String errorCode;
    private final String message;

    public CustomException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
