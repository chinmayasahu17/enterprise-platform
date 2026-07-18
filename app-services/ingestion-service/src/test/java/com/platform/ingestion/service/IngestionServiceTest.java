package com.platform.ingestion.service;

import com.platform.ingestion.controller.SubmissionRequest;
import com.platform.ingestion.controller.SubmissionResponse;
import com.platform.ingestion.event.IngestionEvent;
import com.platform.ingestion.model.IngestionRecord;
import com.platform.ingestion.repository.IngestionRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IngestionServiceTest {

    private final IngestionRecordRepository repository = mock(IngestionRecordRepository.class);
    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, IngestionEvent> kafkaTemplate = mock(KafkaTemplate.class);
    private final IngestionService ingestionService = new IngestionService(repository, kafkaTemplate);

    @Test
    void persistsRecordAndPublishesIngestionEvent() {
        when(repository.save(any(IngestionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SubmissionResponse response = ingestionService.ingest(new SubmissionRequest("partner-a", "raw-data"));

        assertThat(response.status()).isEqualTo("accepted");
        verify(repository).save(any(IngestionRecord.class));
        verify(kafkaTemplate).send(eq("ingestion.events"), eq(response.id().toString()), any(IngestionEvent.class));
    }

    @Test
    void usesUnknownWhenSourceIsMissing() {
        when(repository.save(any(IngestionRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ingestionService.ingest(new SubmissionRequest(null, "raw-data"));

        verify(kafkaTemplate).send(eq("ingestion.events"), any(String.class), any(IngestionEvent.class));
    }
}
