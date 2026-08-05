package com.platform.analytics.processor.repository;

import com.platform.analytics.processor.model.EventAggregate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventAggregateRepository extends JpaRepository<EventAggregate, UUID> {
    Optional<EventAggregate> findByMetricNameAndDimensionAndWindowStartAndWindowEnd(
            String metricName, String dimension, Instant windowStart, Instant windowEnd);
    List<EventAggregate> findByMetricName(String metricName);
    List<EventAggregate> findByMetricNameAndDimension(String metricName, String dimension);
}
