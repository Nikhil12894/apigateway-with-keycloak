package com.nk.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nk.base.constants.ErrorCodeCommon;
import com.nk.base.dto.ErrorMetadata;
import com.nk.base.dto.ResponseDto;
import com.nk.base.dto.ResponseStatus;
import com.nk.base.service.ErrorMetadataLoader;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ErrorMetadataLoader errorMetadataLoader;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper, ErrorMetadataLoader errorMetadataLoader) {
        this.errorMetadataLoader = errorMetadataLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorMetadata errorMetadata = this.errorMetadataLoader.getErrorMetadata(ErrorCodeCommon.UNAUTHORIZED);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResponseStatus errorResponse = new ResponseStatus(errorMetadata);
        ResponseDto<String> responseDto = new ResponseDto<>(errorResponse);
        response.getWriter().write(objectMapper.writeValueAsString(responseDto));
    }
}
