package com.platform.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_records")
public class ReportRecord {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private Instant processedAt;

    protected ReportRecord() {
    }

    public ReportRecord(UUID id, String source, String payload, String status, Instant processedAt) {
        this.id = id;
        this.source = source;
        this.payload = payload;
        this.status = status;
        this.processedAt = processedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
