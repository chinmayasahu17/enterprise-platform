package com.platform.processing.service;

import com.platform.processing.event.IngestionEvent;
import com.platform.processing.event.ProcessingCompletedEvent;
import com.platform.processing.model.ProcessingRecord;
import com.platform.processing.repository.ProcessingRecordRepository;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessingServiceTest {

    private final List<ProcessingRecord> savedRecords = new ArrayList<>();
    private final List<SentEvent> sentEvents = new ArrayList<>();
    private final ProcessingRecordRepository repository = createRepository();
    private final ProcessingService processingService = new ProcessingService(repository, this::recordPublishedEvent);

    @Test
    void persistsProcessedRecordAndPublishesCompletionEvent() {
        UUID id = UUID.randomUUID();
        ProcessingRecord record = processingService
                .process(new IngestionEvent(id, "source-a", "raw-data", Instant.now()));

        assertThat(record.getStatus()).isEqualTo("processed");
        assertThat(savedRecords).hasSize(1);
        assertThat(savedRecords.get(0).getId()).isEqualTo(id);
        assertThat(sentEvents).hasSize(1);
        assertThat(sentEvents.get(0).topic()).isEqualTo("processing.completed");
    }

    @Test
    void doesNotPublishAgainForAnAlreadyProcessedRecord() {
        UUID id = UUID.randomUUID();
        ProcessingRecord existing = new ProcessingRecord(id, "source-a", "raw-data", "processed", Instant.now());
        ProcessingRecordRepository existingRecordRepository = createExistingRecordRepository(existing);
        ProcessingService service = new ProcessingService(existingRecordRepository, this::recordPublishedEvent);

        ProcessingRecord record = service.process(new IngestionEvent(id, "source-a", "raw-data", Instant.now()));

        assertThat(record).isSameAs(existing);
        assertThat(sentEvents).isEmpty();
    }

    private ProcessingRecordRepository createRepository() {
        return (ProcessingRecordRepository) Proxy.newProxyInstance(
                ProcessingRecordRepository.class.getClassLoader(),
                new Class<?>[] { ProcessingRecordRepository.class },
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "save":
                            ProcessingRecord entity = (ProcessingRecord) args[0];
                            savedRecords.add(entity);
                            return entity;
                        case "findById":
                            return Optional.empty();
                        case "findAll":
                            return List.of();
                        default:
                            if (method.getReturnType().equals(boolean.class)
                                    || method.getReturnType().equals(Boolean.class)) {
                                return false;
                            }
                            if (method.getReturnType().equals(int.class)
                                    || method.getReturnType().equals(Integer.class)) {
                                return 0;
                            }
                            if (method.getReturnType().equals(long.class)
                                    || method.getReturnType().equals(Long.class)) {
                                return 0L;
                            }
                            if (method.getReturnType().equals(void.class)) {
                                return null;
                            }
                            if (method.getReturnType().equals(Optional.class)) {
                                return Optional.empty();
                            }
                            if (method.getReturnType().equals(List.class)) {
                                return List.of();
                            }
                            return null;
                    }
                });
    }

    private ProcessingRecordRepository createExistingRecordRepository(ProcessingRecord existing) {
        return (ProcessingRecordRepository) Proxy.newProxyInstance(
                ProcessingRecordRepository.class.getClassLoader(),
                new Class<?>[] { ProcessingRecordRepository.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("findById")) {
                        return Optional.of(existing);
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private void recordPublishedEvent(ProcessingCompletedEvent event) {
        sentEvents.add(new SentEvent("processing.completed", event.id().toString(), event));
    }

    private record SentEvent(String topic, String key, ProcessingCompletedEvent data) {
    }
}
