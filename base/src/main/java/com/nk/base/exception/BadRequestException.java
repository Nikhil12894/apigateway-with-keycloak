package com.nk.base.exception;

public class BadRequestException extends CustomException {
    public BadRequestException(String message) {
        super("BAD_REQUEST", message);
    }
}
