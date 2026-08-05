package com.platform.report.controller;

import java.time.Instant;
import java.util.UUID;

public record ReportSummary(UUID id, String status, String storagePath, Instant createdAt) {
}
