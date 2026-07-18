package com.platform.gateway.filter;

import com.platform.gateway.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Validates the Authorization: Bearer <jwt> header on every request that is
 * not in platform.security.public-paths. This is intentionally minimal -
 * there is no user-service in this platform, so token issuance is handled by
 * the dev-only /api/auth/token endpoint (see AuthController).
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final SecurityProperties securityProperties;
    private final SecretKey signingKey;

    public JwtAuthFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.signingKey = Keys.hmacShaKeyFor(
                securityProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing bearer token");
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception ex) {
            return unauthorized(exchange, "Invalid or expired token");
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return securityProperties.getPublicPaths().stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        byte[] bytes = ("{\"error\":\"unauthorized\",\"message\":\"" + reason + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
