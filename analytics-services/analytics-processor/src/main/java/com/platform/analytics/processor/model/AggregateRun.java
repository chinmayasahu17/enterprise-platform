package com.platform.analytics.processor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "aggregate_runs")
public class AggregateRun {
    @Id
    private UUID id;
    @Column(nullable = false, length = 100)
    private String triggeredBy;
    @Column(nullable = false)
    private Instant startedAt;
    @Column(nullable = false)
    private Instant completedAt;
    @Column(nullable = false, length = 20)
    private String status;
    protected AggregateRun() { }
    public AggregateRun(UUID id, String triggeredBy, Instant startedAt, Instant completedAt, String status) {
        this.id = id; this.triggeredBy = triggeredBy; this.startedAt = startedAt; this.completedAt = completedAt; this.status = status;
    }
    public UUID getId() { return id; }
    public String getTriggeredBy() { return triggeredBy; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getStatus() { return status; }
}
