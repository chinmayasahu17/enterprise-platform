package com.platform.ingestion.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ingestion_records")
public class IngestionRecord {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false, updatable = false)
    private Instant receivedAt;

    protected IngestionRecord() {
    }

    public IngestionRecord(UUID id, String source, String payload, Instant receivedAt) {
        this.id = id;
        this.source = source;
        this.payload = payload;
        this.receivedAt = receivedAt;
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

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
