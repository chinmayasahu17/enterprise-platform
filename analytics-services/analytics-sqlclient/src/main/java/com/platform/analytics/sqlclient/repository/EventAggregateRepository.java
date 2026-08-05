package com.platform.analytics.sqlclient.repository;

import com.platform.analytics.sqlclient.model.EventAggregate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventAggregateRepository extends JpaRepository<EventAggregate, UUID> {
    List<EventAggregate> findByMetricName(String metricName);
    List<EventAggregate> findByMetricNameAndDimension(String metricName, String dimension);
}
