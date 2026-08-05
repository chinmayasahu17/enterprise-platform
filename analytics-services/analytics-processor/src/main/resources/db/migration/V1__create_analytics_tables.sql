CREATE TABLE event_aggregates (
    id UUID PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    dimension VARCHAR(100) NOT NULL,
    value BIGINT NOT NULL,
    window_start TIMESTAMP WITH TIME ZONE NOT NULL,
    window_end TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE UNIQUE INDEX event_aggregates_metric_window_idx
    ON event_aggregates (metric_name, dimension, window_start, window_end);
CREATE TABLE aggregate_runs (
    id UUID PRIMARY KEY,
    triggered_by VARCHAR(100) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL
);
