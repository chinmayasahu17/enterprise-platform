package com.platform.scheduler.event;

import java.time.Instant;
import java.util.UUID;

public record SchedulerTriggerEvent(UUID id, String job, Instant triggeredAt) {
}
