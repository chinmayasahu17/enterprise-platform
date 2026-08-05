package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.AnalyticsAggregateEvent;
import com.platform.analytics.processor.event.AnalyticsTriggerEvent;
import com.platform.analytics.processor.event.IngestionEvent;
import com.platform.analytics.processor.event.ProcessingCompletedEvent;
import com.platform.analytics.processor.model.AggregateRun;
import com.platform.analytics.processor.model.EventAggregate;
import com.platform.analytics.processor.repository.AggregateRunRepository;
import com.platform.analytics.processor.repository.EventAggregateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AnalyticsService {
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private static final Instant ALL_TIME = Instant.EPOCH;
    private final EventAggregateRepository eventAggregateRepository;
    private final AggregateRunRepository aggregateRunRepository;
    private final AnalyticsCounterCache analyticsCounterCache;
    private final AnalyticsAggregatePublisher analyticsAggregatePublisher;
    public AnalyticsService(EventAggregateRepository eventAggregateRepository, AggregateRunRepository aggregateRunRepository,
                            AnalyticsCounterCache analyticsCounterCache, AnalyticsAggregatePublisher analyticsAggregatePublisher) {
        this.eventAggregateRepository = eventAggregateRepository;
        this.aggregateRunRepository = aggregateRunRepository;
        this.analyticsCounterCache = analyticsCounterCache;
        this.analyticsAggregatePublisher = analyticsAggregatePublisher;
    }
    @Transactional
    public void recordIngestion(IngestionEvent event) {
        increment("total_records", "all", ALL_TIME, ALL_TIME);
        increment("records_by_source", source(event.source()), ALL_TIME, ALL_TIME);
        log.info("Recorded ingestion event {} for analytics", event.id());
    }
    @Transactional
    public void recordProcessing(ProcessingCompletedEvent event) {
        Instant now = event.processedAt() == null ? Instant.now() : event.processedAt();
        Instant start = ZonedDateTime.ofInstant(now, ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = start.plusSeconds(86_400);
        increment("records_processed_today", "all", start, end);
        increment("records_by_status", event.status() == null ? "processed" : event.status(), start, end);
        log.info("Recorded processing completion {} for analytics", event.id());
    }
    @Transactional
    public void runAggregation(AnalyticsTriggerEvent event) {
        Instant now = Instant.now();
        UUID runId = UUID.randomUUID();
        AggregateRun run = aggregateRunRepository.save(new AggregateRun(runId,
                event.job() == null ? "analytics" : event.job(), now, now, "completed"));
        long aggregateCount = eventAggregateRepository.count();
        analyticsAggregatePublisher.publish(new AnalyticsAggregateEvent(
                run.getId(), run.getTriggeredBy(), aggregateCount, run.getCompletedAt()));
        log.info("Completed analytics aggregation run {} with {} aggregates", run.getId(), aggregateCount);
    }
    public AnalyticsStatus status() { return new AnalyticsStatus(eventAggregateRepository.count(), aggregateRunRepository.count()); }
    private void increment(String metricName, String dimension, Instant windowStart, Instant windowEnd) {
        EventAggregate aggregate = eventAggregateRepository
                .findByMetricNameAndDimensionAndWindowStartAndWindowEnd(metricName, dimension, windowStart, windowEnd)
                .orElse(null);
        long currentValue = aggregate == null ? 0 : aggregate.getValue();
        long updatedValue = analyticsCounterCache.increment(cacheKey(metricName, dimension, windowStart), currentValue);
        if (aggregate == null) {
            eventAggregateRepository.save(new EventAggregate(UUID.randomUUID(), metricName, dimension, updatedValue, windowStart, windowEnd));
        } else {
            aggregate.setValue(updatedValue);
            eventAggregateRepository.save(aggregate);
        }
    }
    private String source(String source) { return source == null || source.isBlank() ? "unknown" : source; }
    private String cacheKey(String metricName, String dimension, Instant windowStart) {
        return "analytics:" + metricName + ":" + dimension + ":" + windowStart;
    }
    public record AnalyticsStatus(long aggregateCount, long runCount) { }
}
