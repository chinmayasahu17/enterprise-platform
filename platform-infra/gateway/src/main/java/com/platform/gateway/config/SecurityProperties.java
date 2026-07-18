package com.platform.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "platform.security")
public class SecurityProperties {

    private String jwtSecret;
    private long jwtTtlSeconds = 3600;
    private List<String> publicPaths = List.of("/actuator", "/api/auth/token");

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtTtlSeconds() {
        return jwtTtlSeconds;
    }

    public void setJwtTtlSeconds(long jwtTtlSeconds) {
        this.jwtTtlSeconds = jwtTtlSeconds;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }
}
