package com.nk.base.exception;

public class IllegalStateException extends CustomException {
    public IllegalStateException(String message) {
        super("ILLEGAL_STATE", message);
    }
}
