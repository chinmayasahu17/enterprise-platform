package com.platform.analytics.processor.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisAnalyticsCounterCache implements AnalyticsCounterCache {
    private final StringRedisTemplate redisTemplate;
    public RedisAnalyticsCounterCache(StringRedisTemplate redisTemplate) { this.redisTemplate = redisTemplate; }
    @Override
    public long increment(String key, long initialValue) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            redisTemplate.opsForValue().set(key, Long.toString(initialValue));
        }
        Long updatedValue = redisTemplate.opsForValue().increment(key);
        return updatedValue == null ? initialValue : updatedValue;
    }
}
