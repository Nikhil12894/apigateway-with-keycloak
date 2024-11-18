package com.nk.base.exception;

public class DataConflictException extends CustomException {
    public DataConflictException(String message) {
        super("DATA_CONFLICT", message);
    }
}
