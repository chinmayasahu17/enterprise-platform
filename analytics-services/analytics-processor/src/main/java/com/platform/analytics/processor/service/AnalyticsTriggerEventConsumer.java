package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.AnalyticsTriggerEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsTriggerEventConsumer {
    private final AnalyticsService analyticsService;
    public AnalyticsTriggerEventConsumer(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }
    @KafkaListener(topics = "analytics.triggers", groupId = "analytics-processor-group",
            properties = "spring.json.value.default.type=com.platform.analytics.processor.event.AnalyticsTriggerEvent")
    public void consume(AnalyticsTriggerEvent event) { analyticsService.runAggregation(event); }
}
