package com.platform.report.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "generated_reports")
public class GeneratedReport {

    @Id
    private UUID id;

    private Instant requestedFrom;

    private Instant requestedTo;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, columnDefinition = "text")
    private String storagePath;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected GeneratedReport() {
    }

    public GeneratedReport(UUID id, Instant requestedFrom, Instant requestedTo, String status,
                           String storagePath, Instant createdAt) {
        this.id = id;
        this.requestedFrom = requestedFrom;
        this.requestedTo = requestedTo;
        this.status = status;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Instant getRequestedFrom() {
        return requestedFrom;
    }

    public Instant getRequestedTo() {
        return requestedTo;
    }

    public String getStatus() {
        return status;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
