package com.platform.notification.service;

import com.platform.notification.event.ProcessingCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessingCompletedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProcessingCompletedEventConsumer.class);

    private final NotificationService notificationService;

    public ProcessingCompletedEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "processing.completed", groupId = "notification-service-group")
    public void consume(ProcessingCompletedEvent event) {
        log.info("Received processing completion event {}", event.id());
        notificationService.send(event);
    }
}
