package com.platform.analytics.sqlclient.service;

import com.platform.analytics.sqlclient.controller.AnalyticsDailyResponse;
import com.platform.analytics.sqlclient.controller.AnalyticsSourceResponse;
import com.platform.analytics.sqlclient.controller.AnalyticsSummaryResponse;
import com.platform.analytics.sqlclient.model.EventAggregate;
import com.platform.analytics.sqlclient.repository.EventAggregateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsQueryService {
    private final EventAggregateRepository eventAggregateRepository;
    public AnalyticsQueryService(EventAggregateRepository eventAggregateRepository) { this.eventAggregateRepository = eventAggregateRepository; }
    @Transactional(readOnly = true)
    public AnalyticsSummaryResponse summary() {
        long totalRecords = eventAggregateRepository.findByMetricName("total_records").stream()
                .mapToLong(EventAggregate::getValue).max().orElse(0);
        long processedToday = eventAggregateRepository.findByMetricName("records_processed_today").stream()
                .max(Comparator.comparing(EventAggregate::getWindowStart)).map(EventAggregate::getValue).orElse(0L);
        Map<String, Long> byStatus = dimensions("records_by_status");
        Map<String, Long> bySource = dimensions("records_by_source");
        return new AnalyticsSummaryResponse(totalRecords, processedToday, byStatus, bySource);
    }
    @Transactional(readOnly = true)
    public List<AnalyticsDailyResponse> daily() {
        return eventAggregateRepository.findByMetricName("records_processed_today").stream()
                .map(aggregate -> new AnalyticsDailyResponse(aggregate.getWindowStart(), aggregate.getWindowEnd(), aggregate.getValue()))
                .toList();
    }
    @Transactional(readOnly = true)
    public AnalyticsSourceResponse source(String source) {
        long value = eventAggregateRepository.findByMetricNameAndDimension("records_by_source", source).stream()
                .mapToLong(EventAggregate::getValue).max().orElse(0);
        return new AnalyticsSourceResponse(source, value);
    }
    private Map<String, Long> dimensions(String metricName) {
        return eventAggregateRepository.findByMetricName(metricName).stream()
                .collect(Collectors.toMap(EventAggregate::getDimension, EventAggregate::getValue, Long::max));
    }
}
