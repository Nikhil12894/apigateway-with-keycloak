package com.nk.base.exception;

public class AuthenticationException extends CustomException {
    public AuthenticationException(String message) {
        super("AUTHENTICATION_FAILED", message);
    }
}
