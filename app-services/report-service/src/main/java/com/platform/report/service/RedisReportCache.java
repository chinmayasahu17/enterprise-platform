package com.platform.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.report.controller.ReportResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class RedisReportCache implements ReportCache {

    private static final String KEY_PREFIX = "report:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisReportCache(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<ReportResponse> find(UUID id) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + id);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, ReportResponse.class));
        } catch (JsonProcessingException ex) {
            redisTemplate.delete(KEY_PREFIX + id);
            return Optional.empty();
        }
    }

    @Override
    public void save(ReportResponse report, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(
                    KEY_PREFIX + report.id(), objectMapper.writeValueAsString(report), ttl);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to cache report " + report.id(), ex);
        }
    }
}
