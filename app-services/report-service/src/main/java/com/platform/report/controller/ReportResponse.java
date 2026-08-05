package com.platform.report.controller;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(UUID id, String status, String storagePath, Instant createdAt, String content) {
}
