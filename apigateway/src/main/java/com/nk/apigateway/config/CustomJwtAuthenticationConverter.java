package com.nk.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<? extends AbstractAuthenticationToken>> {

    @Override
    public Mono<? extends AbstractAuthenticationToken> convert(Jwt jwt) {
        // Extract authorities from JWT claims
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);

        // Create an authentication token with extracted authorities
        AbstractAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);

        return Mono.just(authenticationToken);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
//        Collection<GrantedAuthority> authorities = new JwtGrantedAuthoritiesConverter().convert(jwt);
//        // Your logic to map JWT claims to authorities
//        // For example, extract roles or scopes and convert them to GrantedAuthority
//        log.info("Extracted authorities: {}", authorities);

        Collection<GrantedAuthority> customAuthorities = getGrantedAuthorities(jwt);
        log.info("Custom authorities: {}", customAuthorities);
        return customAuthorities;
    }

    private static Set<GrantedAuthority> getGrantedAuthorities(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        // Extract roles from `realm_access.roles`
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            roles.addAll((Collection<String>) realmAccess.get("roles"));
        }

        // Extract roles from `resource_access.{client}.roles`
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                Map<String, Object> clientAccess = (Map<String, Object>) entry.getValue();
                if (clientAccess.containsKey("roles")) {
                    roles.addAll((Collection<String>) clientAccess.get("roles"));
                }
            }
        }

        // Map roles to GrantedAuthority
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toSet());
    }
}
