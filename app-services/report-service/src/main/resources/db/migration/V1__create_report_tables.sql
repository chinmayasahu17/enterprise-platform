CREATE TABLE report_records (
    id UUID PRIMARY KEY,
    source VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE generated_reports (
    id UUID PRIMARY KEY,
    requested_from TIMESTAMP WITH TIME ZONE,
    requested_to TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) NOT NULL,
    storage_path TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
