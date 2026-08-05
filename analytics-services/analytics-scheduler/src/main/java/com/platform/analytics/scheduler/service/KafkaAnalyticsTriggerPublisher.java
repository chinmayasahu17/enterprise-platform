package com.platform.analytics.scheduler.service;

import com.platform.analytics.scheduler.event.AnalyticsTriggerEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAnalyticsTriggerPublisher implements AnalyticsTriggerPublisher {
    private static final String ANALYTICS_TOPIC = "analytics.triggers";
    private final KafkaTemplate<String, AnalyticsTriggerEvent> kafkaTemplate;
    public KafkaAnalyticsTriggerPublisher(KafkaTemplate<String, AnalyticsTriggerEvent> kafkaTemplate) { this.kafkaTemplate = kafkaTemplate; }
    @Override
    public void publish(AnalyticsTriggerEvent event) { kafkaTemplate.send(ANALYTICS_TOPIC, event.id().toString(), event); }
}
