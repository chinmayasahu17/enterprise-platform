package com.platform.processing.repository;

import com.platform.processing.model.ProcessingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingRecordRepository extends JpaRepository<ProcessingRecord, UUID> {
}
