package com.nk.apigateway.filters;

//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClientRequestException;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.concurrent.TimeoutException;
//import java.util.logging.Logger;
//
//@Component
//public class ServiceUnavailableFilter implements GlobalFilter {
//    Logger logger = Logger.getLogger(ServiceUnavailableFilter.class.getName());
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return chain.filter(exchange)
//                .onErrorResume(e -> {
//                    if (e instanceof WebClientRequestException || e instanceof TimeoutException || e instanceof WebClientRequestException) {
//                        // Service is unreachable
//                        logger.info("Service is unreachable: " + e.getMessage() +" "+ exchange.getRequest().getPath());
//                        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
//                    } else {
//                        // Other unexpected errors
//                        logger.info("Service is unreachable: " + e.getMessage() +" "+ exchange.getRequest().getPath());
//                        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//                    }
//                    return exchange.getResponse().setComplete();
//                });
//    }
//}
