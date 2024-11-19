Below is the complete code to configure role-based authentication using Keycloak JWT tokens, an API Gateway (Spring Cloud Gateway), and downstream services with Spring Security. This includes JWT validation, role propagation, and role-based access control.

---

### **1. API Gateway Setup with Spring Cloud Gateway**

#### **Step 1: Dependencies for API Gateway**

In the `pom.xml` of your API Gateway project, add the following dependencies:

```xml
<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Spring Security OAuth2 Resource Server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
</dependencies>
```

#### **Step 2: `application.yml` for JWT and Routing Configuration**

Configure Keycloak for JWT validation and routing to different services in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://<your-keycloak-server>/realms/<realm-name>
          jwk-set-uri: https://<your-keycloak-server>/realms/<realm-name>/protocol/openid-connect/certs

  cloud:
    gateway:
      routes:
        - id: user-service
          uri: http://user-service:8081
          predicates:
            - Path=/user/**
          filters:
            - RemoveRequestHeader=Authorization
            - AddRequestHeader=X-Roles, #{role-header}  # Propagate roles header
        - id: admin-service
          uri: http://admin-service:8082
          predicates:
            - Path=/admin/**
          filters:
            - RemoveRequestHeader=Authorization
            - AddRequestHeader=X-Roles, #{role-header}
```

#### **Step 3: Global Filter to Propagate Roles**

Create a custom `GlobalFilter` to validate JWT, extract roles, and propagate them to downstream services:

```java
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RolePropagationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Extract roles from JWT
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String roles = jwt.getClaimAsMap("realm_access").get("roles").toString();

        // Add roles to the header for downstream services
        exchange.getRequest()
                .mutate()
                .header("X-Roles", roles)
                .build();

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // High precedence
    }
}
```

---

### **2. Downstream Services Setup**

#### **Step 1: Dependencies for Downstream Services**

Add Spring Security and OAuth2 dependencies in your downstream service's `pom.xml`:

```xml
<dependencies>
    <!-- Spring Security OAuth2 Resource Server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>

    <!-- Spring Boot Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

#### **Step 2: Security Configuration for Role-Based Access**

Configure Spring Security in the downstream service to check roles:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN can access /admin
            .antMatchers("/user/**").hasRole("USER")   // Only USER can access /user
            .anyRequest().authenticated();             // All other routes require authentication
    }
}
```

#### **Step 3: Role Validation Filter**

Use a filter to extract and validate the `X-Roles` header passed by the API Gateway:

```java
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RoleValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain)
            throws ServletException, IOException {
        String rolesHeader = request.getHeader("X-Roles");

        if (rolesHeader == null || !rolesHeader.contains("USER")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
```

#### **Step 4: Register Role Validation Filter**

Register the `RoleValidationFilter` in the downstream service:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public RoleValidationFilter roleValidationFilter() {
        return new RoleValidationFilter();
    }
}
```

---

### **3. Running the API Gateway and Services**

1. **Run the Gateway**:
    - The API Gateway will handle JWT validation and propagate roles to the downstream services.

2. **Run the Downstream Services**:
    - Both the `user-service` and `admin-service` will check the roles from the `X-Roles` header added by the gateway.

---

### **4. Keycloak Setup for Roles**

Ensure that the JWT token from Keycloak includes the necessary roles:

1. Go to **Keycloak Admin Console**.
2. Under **Clients** > Select your client > **Client Scopes** > Add `roles` scope.
3. Under **Mappers**, create a new **Group Membership** mapper:
    - **Token Claim Name**: `realm_access.roles`
    - **Add to Access Token**: `ON`
    - **Add to ID Token**: `ON`

Ensure that roles are added correctly to the JWT token (`realm_access.roles` claim).

---

### **5. Testing**

1. **Obtain JWT Token**: Use Keycloak to get a token for a user with the required roles (`USER` or `ADMIN`).

2. **Send Request Through Gateway**:
    - Use `curl` or Postman to send a request with the JWT token:
   ```bash
   curl -X GET http://localhost:8080/user/profile -H "Authorization: Bearer <JWT>"
   ```

3. **Role-Based Access**: The API Gateway validates the JWT, extracts roles, and forwards them to the downstream service, where role-based access is enforced.

---

### **Summary**

- **API Gateway**: Handles JWT validation, role extraction, and propagation to downstream services.
- **Downstream Services**: Enforce role-based access control based on roles passed in the `X-Roles` header by the API Gateway.
- **Keycloak**: Manages roles and issues JWT tokens that include role information.

This architecture ensures centralized JWT validation at the gateway and proper role-based access control at each service.