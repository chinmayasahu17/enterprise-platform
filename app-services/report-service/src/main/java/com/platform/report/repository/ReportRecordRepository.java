package com.platform.report.repository;

import com.platform.report.model.ReportRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRecordRepository extends JpaRepository<ReportRecord, UUID> {

    List<ReportRecord> findAllByOrderByProcessedAtAsc();
}
