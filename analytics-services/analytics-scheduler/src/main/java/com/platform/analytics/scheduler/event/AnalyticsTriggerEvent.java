package com.platform.analytics.scheduler.event;

import java.time.Instant;
import java.util.UUID;

public record AnalyticsTriggerEvent(UUID id, String job, Instant triggeredAt) {
}
