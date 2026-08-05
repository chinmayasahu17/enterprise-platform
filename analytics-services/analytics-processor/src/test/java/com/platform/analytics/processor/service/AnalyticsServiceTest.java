package com.platform.analytics.processor.service;

import com.platform.analytics.processor.event.IngestionEvent;
import com.platform.analytics.processor.model.AggregateRun;
import com.platform.analytics.processor.model.EventAggregate;
import com.platform.analytics.processor.repository.AggregateRunRepository;
import com.platform.analytics.processor.repository.EventAggregateRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsServiceTest {
    @Test
    void recordsTotalAndSourceAggregatesForIngestion() {
        List<EventAggregate> aggregates = new ArrayList<>();
        Map<String, Long> counters = new HashMap<>();
        AnalyticsService service = new AnalyticsService(
                aggregateRepository(aggregates), runRepository(),
                (key, initialValue) -> counters.merge(key, initialValue + 1, (current, ignored) -> current + 1),
                event -> { });

        service.recordIngestion(new IngestionEvent(UUID.randomUUID(), "source-a", "payload", Instant.now()));

        assertThat(aggregates).extracting(EventAggregate::getMetricName)
                .containsExactlyInAnyOrder("total_records", "records_by_source");
    }
    private EventAggregateRepository aggregateRepository(List<EventAggregate> aggregates) {
        return (EventAggregateRepository) Proxy.newProxyInstance(EventAggregateRepository.class.getClassLoader(),
                new Class<?>[] { EventAggregateRepository.class }, (proxy, method, args) -> {
                    if (method.getName().equals("findByMetricNameAndDimensionAndWindowStartAndWindowEnd")) {
                        return aggregates.stream().filter(aggregate -> aggregate.getMetricName().equals(args[0])
                                && aggregate.getDimension().equals(args[1])).findFirst();
                    }
                    if (method.getName().equals("save")) { aggregates.add((EventAggregate) args[0]); return args[0]; }
                    if (method.getName().equals("count")) { return (long) aggregates.size(); }
                    if (method.getName().equals("findByMetricName") || method.getName().equals("findByMetricNameAndDimension")) { return List.of(); }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
    private AggregateRunRepository runRepository() {
        return (AggregateRunRepository) Proxy.newProxyInstance(AggregateRunRepository.class.getClassLoader(),
                new Class<?>[] { AggregateRunRepository.class }, (proxy, method, args) -> {
                    if (method.getName().equals("count")) { return 0L; }
                    if (method.getName().equals("save")) { return (AggregateRun) args[0]; }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
