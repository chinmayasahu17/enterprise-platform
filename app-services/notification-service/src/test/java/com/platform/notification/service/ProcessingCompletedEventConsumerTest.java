package com.platform.notification.service;

import com.platform.notification.event.ProcessingCompletedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProcessingCompletedEventConsumerTest {

    @Test
    void consumesCompletionEvent() {
        ProcessingCompletedEventConsumer consumer = new ProcessingCompletedEventConsumer(new NotificationService());

        assertDoesNotThrow(() -> consumer.consume(
                new ProcessingCompletedEvent(UUID.randomUUID(), "test", "payload", "processed", Instant.now())));
    }
}
