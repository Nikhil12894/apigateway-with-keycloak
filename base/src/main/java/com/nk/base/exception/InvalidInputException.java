package com.nk.base.exception;

public class InvalidInputException extends CustomException {
    public InvalidInputException(String message) {
        super("INVALID_INPUT", message);
    }
}
