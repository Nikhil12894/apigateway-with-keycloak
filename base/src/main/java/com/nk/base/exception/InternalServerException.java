package com.nk.base.exception;

public class InternalServerException extends CustomException {
    public InternalServerException(String message) {
        super("INTERNAL_SERVER_ERROR", message);
    }
}
