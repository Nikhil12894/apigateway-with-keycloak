Creating an API Gateway using Spring Boot can be achieved with **Spring Cloud Gateway**. It acts as a central entry point for all incoming requests, handles routing, rate limiting, and integrates authentication mechanisms.

---

## Step 1: Set Up a Spring Boot Project

1. Create a new Spring Boot project using [Spring Initializr](https://start.spring.io/).
   - **Dependencies**:
     - Spring Boot Starter Web
     - Spring Cloud Gateway
     - Spring Boot Starter Security
     - Resilience4j (for rate limiting)
     - Spring Boot Actuator (optional for monitoring)

---

## Step 2: Add Dependencies to `pom.xml`

Include the required dependencies for Spring Cloud Gateway and related features:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot2</artifactId>
        <version>1.7.1</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

---

## Step 3: Define Routes in `application.yml`

Spring Cloud Gateway uses a **route configuration** model to handle routing.

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://localhost:8081
          predicates:
            - Path=/users/**
          filters:
            - StripPrefix=1
        - id: blog-service
          uri: http://localhost:8082
          predicates:
            - Path=/blogs/**
            - Method=GET
          filters:
            - RewritePath=/blogs/(?<segment>.*), /$\\{segment}

      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Origin

```

- **Route Configuration**:
  - The `user-service` routes `/users/**` to a downstream service running on `http://localhost:8081`.
  - The `blog-service` routes `/blogs/**` to a service running on `http://localhost:8082`.

---

## Step 4: Handle Authentication with JWT (Spring Security)

Spring Security can validate JWT tokens and ensure only authenticated requests pass through the gateway.

### Configure `SecurityConfig`:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/auth/**").permitAll() // Allow auth endpoints
            .anyExchange().authenticated() // Secure all other routes
            .and().oauth2ResourceServer().jwt(); // Validate JWT tokens

        return http.build();
    }
}
```

- **Key Points**:
  - `/auth/**` routes are open (for login/signup).
  - All other routes require a valid JWT token.

---

### Validate JWT Tokens with Keycloak

Add Keycloak's public key or issuer in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://<your-keycloak-server>/realms/<realm-name>
```

---

## Step 5: Implement Rate Limiting with Resilience4j

Add a rate limiter configuration to control request limits.

### Add Configuration:

```java
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {
    @RateLimiter(name = "apiGateway", fallbackMethod = "rateLimitFallback")
    public Mono<Void> applyRateLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange);
    }

    public Mono<Void> rateLimitFallback(ServerWebExchange exchange, Throwable t) {
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return exchange.getResponse().setComplete();
    }
}
```

### Add Rate Limiter in `application.yml`:

```yaml
resilience4j:
  ratelimiter:
    instances:
      apiGateway:
        limitForPeriod: 5
        limitRefreshPeriod: 1s
        timeoutDuration: 0
```

This limits the API Gateway to **5 requests per second** for any client.

---

## Step 6: Add Logging and Monitoring (Optional)

Enable **Actuator** endpoints to monitor the health of the gateway.

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

You can then access health and metrics at `/actuator/health` and `/actuator/metrics`.

---

## Step 7: Run the API Gateway

1. Start the Spring Boot application for the API Gateway (`port 8080`).
2. Ensure the downstream services (`user-service` and `blog-service`) are running on their respective ports.
3. Test routing:
   - Access `http://localhost:8080/users/1` to route to `user-service`.
   - Access `http://localhost:8080/blogs/1` to route to `blog-service`.

---

### Benefits of This Design
1. **Centralized Routing**: All requests pass through a single entry point.
2. **Authentication**: Ensures that only authenticated users can access resources.
3. **Rate Limiting**: Protects downstream services from excessive requests.
4. **Scalability**: Easy to add new services by defining routes.
5. **Extensibility**: Future integrations like logging, caching, or transformations can be added easily.

Let me know if you need help with implementing specific parts!