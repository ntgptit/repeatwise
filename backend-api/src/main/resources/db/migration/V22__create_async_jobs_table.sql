-- V22: Create async_jobs table for import/export background processing

CREATE TABLE async_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_type VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL,
    user_id UUID NOT NULL,
    deck_id UUID NOT NULL,
    total_rows INT,
    processed_rows INT,
    success_count INT,
    skipped_count INT,
    failed_count INT,
    duplicate_policy VARCHAR(32),
    export_format VARCHAR(16),
    export_scope VARCHAR(16),
    payload_path VARCHAR(500),
    result_path VARCHAR(500),
    error_report_path VARCHAR(500),
    message VARCHAR(500),
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    started_at TIMESTAMP WITHOUT TIME ZONE,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE async_jobs IS 'Background jobs for card import/export operations';

CREATE INDEX idx_async_jobs_user ON async_jobs (user_id);
CREATE INDEX idx_async_jobs_deck ON async_jobs (deck_id);
CREATE INDEX idx_async_jobs_type_status ON async_jobs (job_type, status);

