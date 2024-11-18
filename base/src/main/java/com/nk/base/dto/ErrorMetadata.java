package com.nk.base.dto;

public record ErrorMetadata(String errorCode, String message, int httpStatus, String details) {}