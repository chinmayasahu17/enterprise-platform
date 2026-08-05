package com.platform.analytics.sqlclient.controller;

import java.time.Instant;

public record AnalyticsDailyResponse(Instant windowStart, Instant windowEnd, long value) {
}
