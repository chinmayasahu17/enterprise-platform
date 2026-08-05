package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.AnalyticsAggregateEvent;

public interface AnalyticsAggregatePublisher {
    void publish(AnalyticsAggregateEvent event);
}
