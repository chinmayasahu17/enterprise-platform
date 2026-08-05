package com.platform.report.repository;

import com.platform.report.model.GeneratedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GeneratedReportRepository extends JpaRepository<GeneratedReport, UUID> {

    List<GeneratedReport> findAllByOrderByCreatedAtDesc();
}
