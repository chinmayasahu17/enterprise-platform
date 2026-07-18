package com.platform.ingestion.repository;

import com.platform.ingestion.model.IngestionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngestionRecordRepository extends JpaRepository<IngestionRecord, UUID> {
}
