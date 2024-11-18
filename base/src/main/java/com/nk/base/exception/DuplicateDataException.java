package com.nk.base.exception;

public class DuplicateDataException extends CustomException {
    public DuplicateDataException(String message) {
        super("DUPLICATE_DATA", message);
    }
}
