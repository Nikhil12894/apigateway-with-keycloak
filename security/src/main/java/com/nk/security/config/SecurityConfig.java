package com.nk.security.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private static final String ROLES = "roles";
    private final CustomAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final SecurityProperties securityProperties;

    public interface Jwt2AuthoritiesConverter extends Converter<Jwt, Collection<? extends GrantedAuthority>> {
    }

    /**
     * Creates a converter for extracting roles from a JWT and mapping them into
     * {@link SimpleGrantedAuthority} instances.
     *
     * <p>
     * This converter extracts roles from two locations within the JWT claims:
     * <ul>
     * <li>`realm_access.roles`: Roles associated with the realm access.</li>
     * <li>`resource_access.{client}.roles`: Roles associated with each client in
     * the resource access.</li>
     * </ul>
     *
     * <p>
     * The roles extracted from these claims are mapped into instances of
     * {@link SimpleGrantedAuthority} and returned as a set.
     *
     * @return a {@link Jwt2AuthoritiesConverter} for extracting roles from a JWT
     */
    @Bean
    public Jwt2AuthoritiesConverter authoritiesConverter() {
        // This is a converter for roles as embedded in the JWT by a Keycloak server
        // Roles are taken from both realm_access.roles & resource_access.{client}.roles
        return jwt -> getGrantedAuthorities(jwt);
    }

    /**
     * Extracts roles from the given JWT and converts them into a set of
     * {@link SimpleGrantedAuthority}.
     *
     * <p>
     * The method extracts roles from two locations within the JWT claims:
     * <ul>
     * <li>`realm_access.roles`: Roles associated with the realm access.</li>
     * <li>`resource_access.{client}.roles`: Roles associated with each client in
     * the resource access.</li>
     * </ul>
     *
     * <p>
     * The roles extracted from these claims are mapped into instances of
     * {@link SimpleGrantedAuthority} and returned as a set.
     *
     * @param jwt the JWT from which to extract roles
     * @return a set of {@link SimpleGrantedAuthority} representing the roles found
     *         in the JWT
     */
    @SuppressWarnings("unchecked")
    private static Set<SimpleGrantedAuthority> getGrantedAuthorities(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // Extract roles from `realm_access.roles`
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            Optional.ofNullable(realmAccess.get(ROLES))
                    .filter(Collection.class::isInstance)
                    .map(obj -> (Collection<String>) obj)
                    .ifPresent(roles::addAll);
        }

        // Extract roles from `resource_access.{client}.roles`
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Map<String, Object> clientAccess = (Map<String, Object>) entry.getValue();
                if (clientAccess.containsKey(ROLES)) {
                    roles.addAll((Collection<String>) clientAccess.get(ROLES));
                }
            }
        }

        // Map roles to GrantedAuthority
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public interface Jwt2AuthenticationConverter extends Converter<Jwt, AbstractAuthenticationToken> {
    }

    /**
     * Creates a converter for extracting authentication details from a JWT and
     * mapping them into an {@link AbstractAuthenticationToken}.
     *
     * <p>
     * This converter takes a JWT and extracts the authorities from it using
     * the {@link Jwt2AuthoritiesConverter} provided. It then creates a new
     * {@link JwtAuthenticationToken} with the JWT and the extracted authorities.
     *
     * @param authoritiesConverter the converter to use for extracting authorities
     *                             from the JWT
     * @return a converter for extracting authentication details from a JWT
     */
    @Bean
    public Jwt2AuthenticationConverter authenticationConverter(Jwt2AuthoritiesConverter authoritiesConverter) {
        return jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt));
    }

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method sets up the security configuration for the application, including
     * disabling CSRF protection, configuring the OAuth2 resource server, disabling
     * session creation, handling authentication exceptions, and configuring
     * authorization rules.
     *
     * @param http                    the HttpSecurity object to configure
     * @param authenticationConverter the converter to use for JWT authentication
     * @param serverProperties        the server properties to use for configuration
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @Primary
    public SecurityFilterChain apiFilterChain(HttpSecurity http,
            Converter<Jwt, ? extends AbstractAuthenticationToken> authenticationConverter,
            ServerProperties serverProperties)
            throws Exception {

        // Disable CSRF protection as we are using JWTs
        log.debug("Disabling CSRF protection");
        http.csrf(csrf -> csrf.disable());

        // Configure OAuth2 resource server
        log.debug("Configuring OAuth2 resource server");
        http.oauth2ResourceServer(oauth2 -> oauth2

                // Set a custom authentication entry point that will be used when
                // authentication fails
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                // Set a custom JWT authentication converter that will be used to
                // convert the JWT to an Authentication object
                .jwt(jwt -> jwt
                        .jwtAuthenticationConverter(authenticationConverter)));

        // Disable session creation
        log.debug("Disabling session creation");
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Handle authentication exceptions by returning a 401 unauthorized response
        log.debug("Configuring authentication exception handling");
        http.exceptionHandling(handling -> handling.authenticationEntryPoint((request, response, authException) -> {
            log.debug("Authentication failed for request: {}", request.getRequestURI());
            log.debug("Authentication exception: {}", authException.getMessage());
            response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Restricted Content\"");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }));

        // If SSL is enabled, require HTTPS for all requests
        if (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) {
            log.debug("Requiring HTTPS for all requests since SSL is enabled");
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }

        // Configure authorization rules
        log.debug("Configuring authorization rules");
        http.authorizeHttpRequests(authz -> {
            if (!this.securityProperties.getRoles().isEmpty()) {
                this.securityProperties.getRoles().entrySet().forEach(entry -> {
                    String role = entry.getKey();
                    String[] urls = entry.getValue().split(",");
                    log.debug("Protected Roles: {} URLs: {} ", entry.getKey(), urls);
                    // Protect all endpoints that match the protected URLs
                    authz.requestMatchers(urls)
                            .hasAnyAuthority(role);
                });
            }

            if (this.securityProperties.getRequestPermitAllPatterns() != null) {
                log.debug("Permit all URLs: {}", List.of(this.securityProperties.getRequestPermitAllPatterns()));
                // Permit all endpoints that match the permit all URLs
                authz.requestMatchers(this.securityProperties.getRequestPermitAllPatterns()).permitAll();
            }

            // Handle pre-flight requests (OPTIONS)
            authz.requestMatchers(HttpMethod.OPTIONS, "**").permitAll();

            // Protect all other endpoints
            authz.anyRequest().authenticated();
        });
        return http.build();
    }

}
