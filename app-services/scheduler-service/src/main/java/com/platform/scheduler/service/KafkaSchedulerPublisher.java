package com.platform.scheduler.service;

import com.platform.scheduler.event.SchedulerTriggerEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSchedulerPublisher implements SchedulerEventPublisher {

    private static final String SCHEDULER_TOPIC = "scheduler.triggers";

    private final KafkaTemplate<String, SchedulerTriggerEvent> kafkaTemplate;

    public KafkaSchedulerPublisher(KafkaTemplate<String, SchedulerTriggerEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(SchedulerTriggerEvent event) {
        kafkaTemplate.send(SCHEDULER_TOPIC, event.id().toString(), event);
    }
}
