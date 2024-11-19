package com.nk.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method sets up the security configuration for the application, including
     * disabling CSRF protection, configuring the OAuth2 resource server, disabling
     * session creation, handling authentication exceptions, and configuring
     * authorization rules.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Primary
    public SecurityWebFilterChain apiFilterChain(ServerHttpSecurity http) {

        // Disable CSRF protection as we are using JWTs
        log.debug("Disabling CSRF protection");
        http.csrf(csrf -> csrf.disable());

        // Configure OAuth2 resource server
        log.debug("Configuring OAuth2 resource server");
        http.oauth2ResourceServer(oauth2 -> oauth2

                // Set a custom authentication entry point that will be used when
                // authentication fails
                .authenticationEntryPoint(customAuthenticationEntryPoint())

                // Set a custom JWT authentication converter that will be used to
                // convert the JWT to an Authentication object
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())));
        // Configure authorization rules
        log.debug("Configuring authorization rules");
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/public/**","/actuator/**","/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll() // Public endpoints
                .anyExchange().authenticated() // Secure all other endpoints
        );

        return http.build();
    }

}
