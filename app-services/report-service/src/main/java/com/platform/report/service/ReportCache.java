package com.platform.report.service;

import com.platform.report.controller.ReportResponse;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface ReportCache {

    Optional<ReportResponse> find(UUID id);

    void save(ReportResponse report, Duration ttl);
}
