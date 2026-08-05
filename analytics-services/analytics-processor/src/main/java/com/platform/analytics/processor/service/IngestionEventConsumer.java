package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.IngestionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IngestionEventConsumer {
    private final AnalyticsService analyticsService;
    public IngestionEventConsumer(AnalyticsService analyticsService) { this.analyticsService = analyticsService; }
    @KafkaListener(topics = "ingestion.events", groupId = "analytics-processor-group",
            properties = "spring.json.value.default.type=com.platform.analytics.processor.event.IngestionEvent")
    public void consume(IngestionEvent event) { analyticsService.recordIngestion(event); }
}
