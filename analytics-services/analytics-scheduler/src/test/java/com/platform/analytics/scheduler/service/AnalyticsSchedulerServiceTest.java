package com.platform.analytics.scheduler.service;

import com.platform.analytics.scheduler.event.AnalyticsTriggerEvent;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsSchedulerServiceTest {
    private final List<AnalyticsTriggerEvent> publishedEvents = new ArrayList<>();
    private final AnalyticsSchedulerService service = new AnalyticsSchedulerService(publishedEvents::add);
    @Test
    void publishesAnalyticsTrigger() {
        AnalyticsTriggerEvent event = service.trigger("analytics");
        assertThat(publishedEvents).containsExactly(event);
    }
}
