package com.platform.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * Rate limit key resolver. Keys by remote address so the RedisRateLimiter
     * filter (configured per-route in application.yml) can throttle per client IP.
     */
    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> {
            String host = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(host);
        };
    }
}
