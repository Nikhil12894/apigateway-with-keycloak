package com.nk.base.exception;

public class NotFoundException extends CustomException {
    public NotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
