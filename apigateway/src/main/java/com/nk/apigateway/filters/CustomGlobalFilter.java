package com.nk.apigateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class CustomGlobalFilter implements GlobalFilter {
    Logger logger = Logger.getLogger(CustomGlobalFilter.class.getName());

    private final ObjectMapper objectMapper;

    public CustomGlobalFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authToken == null || authToken.isEmpty()) {
            return respondWithError(exchange, "UNAUTHORIZED", "Authentication token is missing", null, HttpStatus.UNAUTHORIZED);
        }

        // Call the resource server for token validation
        return WebClient.create("http://localhost:1010")
                .get()
                .uri("/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .retrieve()
                .toBodilessEntity()
                .flatMap(response -> chain.filter(exchange)) // Proceed if valid
                .onErrorResume(e -> {
                    // Handle resource server errors
                    logger.info("Unable to validate token: " + e.getMessage() +" "+ exchange.getRequest().getPath());
                    return respondWithError(exchange, "AUTHENTICATION_FAILED", "Authentication failed", e.getMessage(), HttpStatus.UNAUTHORIZED);
                })
                .onErrorResume(WebClientRequestException.class, e -> {
                    // If the downstream service is unreachable, respond with 404
                    logger.info("Service is unreachable: WebClientRequestException" + e.getMessage() +" "+ exchange.getRequest().getPath());
                    return respondWithError(exchange, "SERVICE_UNAVAILABLE", "Service is unreachable", e.getMessage(), HttpStatus.NOT_FOUND);
                })
                .onErrorResume(WebClientResponseException.class, e -> {
                    // If the downstream service is unreachable, respond with 404
                    logger.info("Service is unreachable: WebClientResponseException" + e.getMessage() +" "+ exchange.getRequest().getPath());
                    return respondWithError(exchange, "SERVICE_UNAVAILABLE", "Service is unreachable", e.getMessage(), HttpStatus.NOT_FOUND);
                });
    }

    private Mono<Void> respondWithError(ServerWebExchange exchange, String errorCode, String message, String details, HttpStatus status) {
        // Prepare the error response
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> statusNode = new HashMap<>();
        statusNode.put("errorCode", errorCode);
        statusNode.put("message", message);
        statusNode.put("details", details);

        errorResponse.put("status", statusNode);
        errorResponse.put("data", null);

        // Write the error response as JSON
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}
