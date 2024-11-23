package com.nk.apigateway.filters;

//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientRequestException;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import org.springframework.web.server.ResponseStatusException;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.logging.Logger;
//
//@Component
//public class ValidateTokenFilter implements GatewayFilter {
//    Logger logger = Logger.getLogger(ValidateTokenFilter.class.getName());
//
//    public static final String BEARER = "Bearer ";
//    private final WebClient webClient;
//
//    public ValidateTokenFilter(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.baseUrl("http://localhost:1010").build();  // Resource Server URL
//    }
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        String authToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//        if (authToken == null || !authToken.startsWith(BEARER)) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//
//        // Forward the token to the Resource Server for validation
//        return webClient.get()
//            .uri("/auth/validate")
//            .header(HttpHeaders.AUTHORIZATION, authToken)
//            .retrieve()
//            .toBodilessEntity()
//            .flatMap(response -> chain.filter(exchange))  // Proceed if valid
//            .onErrorResume(e -> {
//                if (e instanceof WebClientRequestException || e instanceof WebClientResponseException) {
//                    // Service is unreachable (e.g., connection timeout, DNS failure)
//                    logger.info("Service is unreachable WebClientRequestException/WebClientResponseException : " + e.getMessage() +" "+ exchange.getRequest().getPath());
//                    exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
//                } else if (e instanceof ResponseStatusException) {
//                    // Forward the exact status code from the auth service
//                    logger.info("Service is unreachable ResponseStatusException: " + e.getMessage() +" "+ exchange.getRequest().getPath());
//                    HttpStatusCode status = ((ResponseStatusException) e).getStatusCode();
//                    exchange.getResponse().setStatusCode(status);
//                } else {
//                    // Fallback to internal server error for unexpected exceptions
//                    logger.info("Service is unreachable: " + e.getMessage() +" "+ exchange.getRequest().getPath());
//                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//                return exchange.getResponse().setComplete();
//            });
//    }
//
//
//}
