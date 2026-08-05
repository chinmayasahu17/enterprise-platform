package com.platform.analytics.sqlclient.controller;

import java.util.Map;

public record AnalyticsSummaryResponse(long totalRecords, long recordsProcessedToday,
                                       Map<String, Long> recordsByStatus, Map<String, Long> recordsBySource) {
}
