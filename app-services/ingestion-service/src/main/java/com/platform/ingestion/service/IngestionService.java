package com.platform.ingestion.service;

import com.platform.ingestion.controller.SubmissionRequest;
import com.platform.ingestion.controller.SubmissionResponse;
import com.platform.ingestion.event.IngestionEvent;
import com.platform.ingestion.model.IngestionRecord;
import com.platform.ingestion.repository.IngestionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private static final String INGESTION_TOPIC = "ingestion.events";

    private final IngestionRecordRepository ingestionRecordRepository;
    private final KafkaTemplate<String, IngestionEvent> kafkaTemplate;

    public IngestionService(IngestionRecordRepository ingestionRecordRepository,
                            KafkaTemplate<String, IngestionEvent> kafkaTemplate) {
        this.ingestionRecordRepository = ingestionRecordRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public SubmissionResponse ingest(SubmissionRequest request) {
        Instant receivedAt = Instant.now();
        UUID id = UUID.randomUUID();
        String source = request.source() == null || request.source().isBlank() ? "unknown" : request.source();

        IngestionRecord record = ingestionRecordRepository.save(
                new IngestionRecord(id, source, request.payload(), receivedAt));
        IngestionEvent event = new IngestionEvent(
                record.getId(), record.getSource(), record.getPayload(), record.getReceivedAt());

        kafkaTemplate.send(INGESTION_TOPIC, record.getId().toString(), event);
        log.info("Accepted ingestion record {} from source {}", record.getId(), record.getSource());

        return new SubmissionResponse(record.getId(), "accepted", record.getReceivedAt());
    }
}
