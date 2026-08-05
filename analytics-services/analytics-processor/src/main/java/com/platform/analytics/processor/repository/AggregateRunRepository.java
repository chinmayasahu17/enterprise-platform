package com.platform.analytics.processor.repository;

import com.platform.analytics.processor.model.AggregateRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AggregateRunRepository extends JpaRepository<AggregateRun, UUID> {
}
