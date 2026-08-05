package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.AnalyticsAggregateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAnalyticsAggregatePublisher implements AnalyticsAggregatePublisher {
    private static final String AGGREGATES_TOPIC = "analytics.aggregates";
    private final KafkaTemplate<String, AnalyticsAggregateEvent> kafkaTemplate;
    public KafkaAnalyticsAggregatePublisher(KafkaTemplate<String, AnalyticsAggregateEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void publish(AnalyticsAggregateEvent event) {
        kafkaTemplate.send(AGGREGATES_TOPIC, event.runId().toString(), event);
    }
}
