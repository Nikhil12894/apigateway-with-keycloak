package com.nk.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nk.base.dto.ErrorMetadata;
import com.nk.base.dto.ResponseDto;
import com.nk.base.dto.ResponseStatus;
import com.nk.base.service.ErrorMetadataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Autowired
    private ErrorMetadataLoader errorMetadataLoader;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED); // Set HTTP status to 401
        response.getHeaders().add("Content-Type", "application/json");
        ErrorMetadata errorMetadata = errorMetadataLoader.getErrorMetadata("AUTHENTICATION_FAILED");
        ResponseStatus responseStatus = new ResponseStatus(errorMetadata);
        ResponseDto<Object> responseDto = new ResponseDto<>(responseStatus); // Create a custom response body (optional>
        // Create a custom response body (optional)
        String body = null;
        try {
            body = objectMapper.writeValueAsString(responseDto);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        byte[] bytes = body!=null?body.getBytes():null;

        // Write the response
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
