package com.platform.report.service;

import com.platform.report.config.ReportProperties;
import com.platform.report.controller.ReportGenerationRequest;
import com.platform.report.controller.ReportResponse;
import com.platform.report.model.GeneratedReport;
import com.platform.report.model.ReportRecord;
import com.platform.report.repository.GeneratedReportRepository;
import com.platform.report.repository.ReportRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportServiceTest {

    @TempDir
    Path reportsPath;

    @Test
    void generatesAndCachesReportFromLocalReadModel() {
        ReportRecord reportRecord = new ReportRecord(
                UUID.randomUUID(), "source-a", "payload", "processed", Instant.now());
        Map<UUID, GeneratedReport> generatedReports = new HashMap<>();
        InMemoryReportCache reportCache = new InMemoryReportCache();
        ReportService reportService = new ReportService(
                reportRecordRepository(List.of(reportRecord)),
                generatedReportRepository(generatedReports),
                reportCache,
                reportProperties());

        ReportResponse report = reportService.generate(new ReportGenerationRequest(null, null));

        assertThat(report.status()).isEqualTo("generated");
        assertThat(report.content()).contains("Record count: 1");
        assertThat(Path.of(report.storagePath())).exists();
        assertThat(reportCache.find(report.id())).contains(report);
    }

    private ReportProperties reportProperties() {
        ReportProperties reportProperties = new ReportProperties();
        reportProperties.setOutputPath(reportsPath.toString());
        return reportProperties;
    }

    private ReportRecordRepository reportRecordRepository(List<ReportRecord> records) {
        return (ReportRecordRepository) Proxy.newProxyInstance(
                ReportRecordRepository.class.getClassLoader(),
                new Class<?>[] { ReportRecordRepository.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("findAllByOrderByProcessedAtAsc")) {
                        return records;
                    }
                    if (method.getName().equals("existsById")) {
                        return false;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private GeneratedReportRepository generatedReportRepository(Map<UUID, GeneratedReport> reports) {
        return (GeneratedReportRepository) Proxy.newProxyInstance(
                GeneratedReportRepository.class.getClassLoader(),
                new Class<?>[] { GeneratedReportRepository.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("save")) {
                        GeneratedReport report = (GeneratedReport) args[0];
                        reports.put(report.getId(), report);
                        return report;
                    }
                    if (method.getName().equals("findById")) {
                        return Optional.ofNullable(reports.get(args[0]));
                    }
                    if (method.getName().equals("findAllByOrderByCreatedAtDesc")) {
                        return List.copyOf(reports.values());
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }

    private static class InMemoryReportCache implements ReportCache {

        private final Map<UUID, ReportResponse> reports = new HashMap<>();

        @Override
        public Optional<ReportResponse> find(UUID id) {
            return Optional.ofNullable(reports.get(id));
        }

        @Override
        public void save(ReportResponse report, Duration ttl) {
            reports.put(report.id(), report);
        }
    }
}
