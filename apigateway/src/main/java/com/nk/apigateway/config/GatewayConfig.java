package com.nk.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {


    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("user-service-route", r -> r.path("/user/**")
                        .filters(f -> f) // Add validation filter
                        .uri("http://localhost:8081/"))
                .route("admin-service-route", r -> r.path("/admin/**")
                        .filters(f -> f)
                        .uri("http://localhost:8082/"))
                .build();
    }

}