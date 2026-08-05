package com.platform.report.service;

import com.platform.report.config.ReportProperties;
import com.platform.report.controller.ReportGenerationRequest;
import com.platform.report.controller.ReportResponse;
import com.platform.report.controller.ReportSummary;
import com.platform.report.event.ProcessingCompletedEvent;
import com.platform.report.model.GeneratedReport;
import com.platform.report.model.ReportRecord;
import com.platform.report.repository.GeneratedReportRepository;
import com.platform.report.repository.ReportRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRecordRepository reportRecordRepository;
    private final GeneratedReportRepository generatedReportRepository;
    private final ReportCache reportCache;
    private final ReportProperties reportProperties;

    public ReportService(ReportRecordRepository reportRecordRepository,
                         GeneratedReportRepository generatedReportRepository,
                         ReportCache reportCache,
                         ReportProperties reportProperties) {
        this.reportRecordRepository = reportRecordRepository;
        this.generatedReportRepository = generatedReportRepository;
        this.reportCache = reportCache;
        this.reportProperties = reportProperties;
    }

    @Transactional
    public void recordProcessedRecord(ProcessingCompletedEvent event) {
        if (reportRecordRepository.existsById(event.id())) {
            return;
        }

        reportRecordRepository.save(new ReportRecord(
                event.id(),
                event.source() == null ? "unknown" : event.source(),
                event.payload() == null ? "" : event.payload(),
                event.status() == null ? "processed" : event.status(),
                event.processedAt() == null ? Instant.now() : event.processedAt()));
        log.info("Added processed record {} to report read model", event.id());
    }

    @Transactional
    public ReportResponse generate(ReportGenerationRequest request) {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        String content = renderReport(request);
        Path storagePath = writeReport(id, content);

        GeneratedReport generatedReport = generatedReportRepository.save(new GeneratedReport(
                id, request.from(), request.to(), "generated", storagePath.toString(), createdAt));
        ReportResponse response = toResponse(generatedReport, content);
        reportCache.save(response, Duration.ofSeconds(reportProperties.getCacheTtlSeconds()));

        log.info("Generated report {} with {} processed records", id, countRecords(request));
        return response;
    }

    public List<ReportSummary> findAll() {
        return generatedReportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    public ReportResponse findById(UUID id) {
        return reportCache.find(id).orElseGet(() -> loadAndCacheReport(id));
    }

    private ReportResponse loadAndCacheReport(UUID id) {
        GeneratedReport generatedReport = generatedReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("report not found"));
        String content = readReport(generatedReport.getStoragePath());
        ReportResponse response = toResponse(generatedReport, content);
        reportCache.save(response, Duration.ofSeconds(reportProperties.getCacheTtlSeconds()));
        return response;
    }

    private String renderReport(ReportGenerationRequest request) {
        List<ReportRecord> records = recordsFor(request);
        StringBuilder report = new StringBuilder("Enterprise Platform Report\n");
        report.append("Generated at: ").append(Instant.now()).append('\n');
        report.append("Record count: ").append(records.size()).append("\n\n");

        for (ReportRecord record : records) {
            report.append(record.getProcessedAt()).append(" | ")
                    .append(record.getId()).append(" | ")
                    .append(record.getSource()).append(" | ")
                    .append(record.getStatus()).append('\n');
        }
        return report.toString();
    }

    private int countRecords(ReportGenerationRequest request) {
        return recordsFor(request).size();
    }

    private List<ReportRecord> recordsFor(ReportGenerationRequest request) {
        return reportRecordRepository.findAllByOrderByProcessedAtAsc().stream()
                .filter(record -> request.from() == null || !record.getProcessedAt().isBefore(request.from()))
                .filter(record -> request.to() == null || !record.getProcessedAt().isAfter(request.to()))
                .toList();
    }

    private Path writeReport(UUID id, String content) {
        try {
            Path outputDirectory = Path.of(reportProperties.getOutputPath());
            Files.createDirectories(outputDirectory);
            Path storagePath = outputDirectory.resolve(id + ".txt");
            Files.writeString(storagePath, content);
            return storagePath;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write report file", ex);
        }
    }

    private String readReport(String storagePath) {
        try {
            return Files.readString(Path.of(storagePath));
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read report file", ex);
        }
    }

    private ReportResponse toResponse(GeneratedReport report, String content) {
        return new ReportResponse(
                report.getId(), report.getStatus(), report.getStoragePath(), report.getCreatedAt(), content);
    }

    private ReportSummary toSummary(GeneratedReport report) {
        return new ReportSummary(report.getId(), report.getStatus(), report.getStoragePath(), report.getCreatedAt());
    }
}
