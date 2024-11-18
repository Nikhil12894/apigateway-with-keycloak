package com.nk.base.exception;

public class IllegalArgumentException extends CustomException {
    public IllegalArgumentException(String message) {
        super("ILLEGAL_ARGUMENT", message);
    }
}
