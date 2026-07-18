package com.platform.ingestion.controller;

import java.time.Instant;
import java.util.UUID;

public record SubmissionResponse(UUID id, String status, Instant receivedAt) {
}
