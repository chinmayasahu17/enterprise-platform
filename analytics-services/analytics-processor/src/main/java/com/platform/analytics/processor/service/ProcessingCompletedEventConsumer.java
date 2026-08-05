package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.ProcessingCompletedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessingCompletedEventConsumer {
    private final AnalyticsService analyticsService;
    public ProcessingCompletedEventConsumer(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }
    @KafkaListener(topics = "processing.completed", groupId = "analytics-processor-group",
            properties = "spring.json.value.default.type=com.platform.analytics.processor.event.ProcessingCompletedEvent")
    public void consume(ProcessingCompletedEvent event) { analyticsService.recordProcessing(event); }
}
