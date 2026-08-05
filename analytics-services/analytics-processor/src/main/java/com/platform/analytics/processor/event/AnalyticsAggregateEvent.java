package com.platform.analytics.processor.event;

import java.time.Instant;
import java.util.UUID;

public record AnalyticsAggregateEvent(UUID runId, String triggeredBy, long aggregateCount, Instant completedAt) {
}
