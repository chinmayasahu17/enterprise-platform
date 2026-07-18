package com.platform.processing.service;

import com.platform.processing.event.IngestionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IngestionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(IngestionEventConsumer.class);

    private final ProcessingService processingService;

    public IngestionEventConsumer(ProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "ingestion.events", groupId = "processing-service-group")
    public void consume(IngestionEvent event) {
        log.info("Received ingestion event {} from source {}", event.id(), event.source());
        processingService.process(event);
    }
}
