package com.platform.analytics.processor.service;

public interface AnalyticsCounterCache {
    long increment(String key, long initialValue);
}
