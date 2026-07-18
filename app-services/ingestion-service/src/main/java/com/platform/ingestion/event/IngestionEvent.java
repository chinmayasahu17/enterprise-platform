package com.platform.ingestion.event;

import java.time.Instant;
import java.util.UUID;

public record IngestionEvent(UUID id, String source, String payload, Instant receivedAt) {
}
