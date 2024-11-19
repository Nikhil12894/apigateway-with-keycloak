package com.nk.common.controlleradvice;

import com.nk.base.constants.ErrorCodeCommon;
import com.nk.base.dto.ErrorMetadata;
import com.nk.base.dto.ResponseDto;
import com.nk.base.dto.ResponseStatus;
import com.nk.base.exception.CustomException;
import com.nk.base.service.ErrorMetadataLoader;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorMetadataLoader errorMetadataLoader;

    public GlobalExceptionHandler(ErrorMetadataLoader errorMetadataLoader) {
        this.errorMetadataLoader = errorMetadataLoader;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Object>> handleCustomException(CustomException ex) {
        ErrorMetadata errorMetadata = errorMetadataLoader.getErrorMetadata(ex.getErrorCode());
        ResponseStatus responseStatus = new ResponseStatus(errorMetadata);
        return ResponseEntity.status(errorMetadata.httpStatus())
                .body(new ResponseDto<>(responseStatus));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("Unknown error");
        ResponseStatus errorResponse = new ResponseStatus(ErrorCodeCommon.BAD_REQUEST, errorMessage);
        ResponseDto<Object> response = new ResponseDto<>(errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDto<Object>> handleBindExceptions(BindException ex) {
        String errorMessage = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("Unknown error");
        ResponseStatus errorResponse = new ResponseStatus(ErrorCodeCommon.BAD_REQUEST, errorMessage);
        ResponseDto<Object> response = new ResponseDto<>(errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGeneralException(Exception ex) {
        ResponseStatus errorResponse = new ResponseStatus(ErrorCodeCommon.UNKNOWN_ERROR, ex.getMessage());
        ResponseDto<Object> response = new ResponseDto<>(errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
