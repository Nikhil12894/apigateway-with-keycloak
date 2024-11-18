package com.nk.base.dto;

public record ResponseStatus(String errorCode, String message, String details) {

    public ResponseStatus(ErrorMetadata errorMetadata) {
        this(errorMetadata.errorCode(), errorMetadata.message(), errorMetadata.details());
    }

    public ResponseStatus(String success, String requestProcessedSuccessfully) {
        this(success, requestProcessedSuccessfully, null);
    }
}
