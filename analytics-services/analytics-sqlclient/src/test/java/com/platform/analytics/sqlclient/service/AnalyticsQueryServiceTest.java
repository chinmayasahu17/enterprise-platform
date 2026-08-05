package com.platform.analytics.sqlclient.service;

import com.platform.analytics.sqlclient.model.EventAggregate;
import com.platform.analytics.sqlclient.repository.EventAggregateRepository;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsQueryServiceTest {
    @Test
    void returnsSummaryFromAggregates() {
        EventAggregateRepository repository = repository();
        AnalyticsQueryService service = new AnalyticsQueryService(repository);
        assertThat(service.summary().totalRecords()).isEqualTo(2);
        assertThat(service.summary().recordsBySource()).containsEntry("source-a", 2L);
    }
    private EventAggregateRepository repository() {
        EventAggregate total = new EventAggregate(UUID.randomUUID(), "total_records", "all", 2, Instant.EPOCH, Instant.EPOCH);
        EventAggregate source = new EventAggregate(UUID.randomUUID(), "records_by_source", "source-a", 2, Instant.EPOCH, Instant.EPOCH);
        return (EventAggregateRepository) Proxy.newProxyInstance(EventAggregateRepository.class.getClassLoader(), new Class<?>[] { EventAggregateRepository.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("findByMetricName")) {
                        return args[0].equals("total_records") ? List.of(total) : args[0].equals("records_by_source") ? List.of(source) : List.of();
                    }
                    if (method.getName().equals("findByMetricNameAndDimension")) { return List.of(source); }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
