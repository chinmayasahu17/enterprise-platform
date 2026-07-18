package com.platform.processing.service;

import com.platform.processing.event.ProcessingCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProcessingEventPublisher implements ProcessingEventPublisher {

    private static final String COMPLETED_TOPIC = "processing.completed";

    private final KafkaTemplate<String, ProcessingCompletedEvent> kafkaTemplate;

    public KafkaProcessingEventPublisher(KafkaTemplate<String, ProcessingCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(ProcessingCompletedEvent event) {
        kafkaTemplate.send(COMPLETED_TOPIC, event.id().toString(), event);
    }
}
