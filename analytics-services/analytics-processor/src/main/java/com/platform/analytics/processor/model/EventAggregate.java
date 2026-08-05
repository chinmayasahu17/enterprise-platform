package com.platform.analytics.processor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "event_aggregates")
public class EventAggregate {
    @Id
    private UUID id;
    @Column(nullable = false, length = 100)
    private String metricName;
    @Column(nullable = false, length = 100)
    private String dimension;
    @Column(nullable = false)
    private long value;
    @Column(nullable = false)
    private Instant windowStart;
    @Column(nullable = false)
    private Instant windowEnd;
    protected EventAggregate() { }
    public EventAggregate(UUID id, String metricName, String dimension, long value, Instant windowStart, Instant windowEnd) {
        this.id = id; this.metricName = metricName; this.dimension = dimension; this.value = value;
        this.windowStart = windowStart; this.windowEnd = windowEnd;
    }
    public UUID getId() { return id; }
    public String getMetricName() { return metricName; }
    public String getDimension() { return dimension; }
    public long getValue() { return value; }
    public Instant getWindowStart() { return windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public void setValue(long value) { this.value = value; }
}
