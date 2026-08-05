package com.platform.report.controller;

import java.time.Instant;

public record ReportGenerationRequest(Instant from, Instant to) {
}
