package com.platform.ingestion.controller;

import com.platform.ingestion.service.IngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class IngestionControllerTest {

    private final IngestionController controller = new IngestionController(mock(IngestionService.class));

    @Test
    void rejectsBlankPayload() {
        assertThat(controller.submit(new SubmissionRequest("test", " ")).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
