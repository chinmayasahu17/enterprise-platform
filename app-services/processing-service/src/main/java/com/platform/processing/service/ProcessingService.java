package com.platform.processing.service;

import com.platform.processing.event.IngestionEvent;
import com.platform.processing.event.ProcessingCompletedEvent;
import com.platform.processing.model.ProcessingRecord;
import com.platform.processing.repository.ProcessingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProcessingService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingService.class);
    private final ProcessingRecordRepository processingRecordRepository;
    private final ProcessingEventPublisher processingEventPublisher;

    public ProcessingService(ProcessingRecordRepository processingRecordRepository,
            ProcessingEventPublisher processingEventPublisher) {
        this.processingRecordRepository = processingRecordRepository;
        this.processingEventPublisher = processingEventPublisher;
    }

    @Transactional
    public ProcessingRecord process(IngestionEvent event) {
        return processingRecordRepository.findById(event.id())
                .orElseGet(() -> createProcessedRecord(event));
    }

    private ProcessingRecord createProcessedRecord(IngestionEvent event) {
        Instant processedAt = Instant.now();
        String status = "processed";
        String payload = event.payload() == null ? "" : event.payload().trim();

        ProcessingRecord record = processingRecordRepository.save(
                new ProcessingRecord(event.id(), event.source() == null ? "unknown" : event.source(), payload, status,
                        processedAt));

        ProcessingCompletedEvent completedEvent = new ProcessingCompletedEvent(
                record.getId(), record.getSource(), record.getPayload(), record.getStatus(), record.getProcessedAt());

        processingEventPublisher.publish(completedEvent);
        log.info("Processed record {} from source {}", record.getId(), record.getSource());

        return record;
    }

    public List<ProcessingRecord> findAll() {
        return processingRecordRepository.findAll();
    }

    public ProcessingRecord findById(UUID id) {
        return processingRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("record not found"));
    }
}
