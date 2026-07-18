package com.platform.notification.event;

import java.time.Instant;
import java.util.UUID;

public record ProcessingCompletedEvent(UUID id, String source, String payload, String status, Instant processedAt) {
}
