package com.platform.gateway.controller;

import com.platform.gateway.config.SecurityProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * DEV-ONLY token issuer. There is no user-service in this platform - this
 * endpoint exists purely so protected gateway routes can be exercised while
 * you build out real services. It performs no credential check.
 * Do not carry this pattern into anything resembling production.
 */
@RestController
public class AuthController {

    private final SecretKey signingKey;
    private final long ttlSeconds;

    public AuthController(SecurityProperties securityProperties) {
        this.signingKey = Keys.hmacShaKeyFor(
                securityProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
        this.ttlSeconds = securityProperties.getJwtTtlSeconds();
    }

    public record TokenRequest(String subject) {}

    @PostMapping("/api/auth/token")
    public Mono<ResponseEntity<Map<String, Object>>> issueToken(@RequestBody TokenRequest request) {
        String subject = (request == null || request.subject() == null || request.subject().isBlank())
                ? "dev-user"
                : request.subject();

        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttlSeconds, ChronoUnit.SECONDS)))
                .signWith(signingKey)
                .compact();

        return Mono.just(ResponseEntity.ok(Map.of(
                "token", token,
                "tokenType", "Bearer",
                "expiresInSeconds", ttlSeconds
        )));
    }
}
