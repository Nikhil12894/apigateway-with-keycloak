package com.nk.base.dto;

public class ResponseDto<T> {
    private ResponseStatus status; // ResponseStatus object containing success details
    private T data;               // Generic data field for the actual response

    // Constructor for successful/error response (without data)
    public ResponseDto(ResponseStatus status) {
        this.status = status;
        this.data = null; // No data in case of error
    }

    // Constructor for successful response with data
    public ResponseDto(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    // Getters and Setters
    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
