-- ==============================================
-- NOTIFICATIONS
-- ==============================================
CREATE TABLE notifications (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    set_id              UUID REFERENCES sets(id) ON DELETE CASCADE,
    remind_schedule_id  UUID REFERENCES remind_schedules(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    message             TEXT NOT NULL,
    type                VARCHAR(50) NOT NULL, -- REVIEW_DUE, CYCLE_COMPLETED, SET_MASTERED, etc.
    priority            VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    scheduled_at        TIMESTAMPTZ,
    sent_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ
);

CREATE INDEX idx_notifications_user ON notifications(user_id, deleted_at);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read, deleted_at);
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_at, sent_at, deleted_at);
CREATE INDEX idx_notifications_type ON notifications(type, deleted_at);
CREATE INDEX idx_notifications_priority ON notifications(priority, deleted_at);

COMMENT ON TABLE notifications IS 'Stores user notifications for various events.';
COMMENT ON COLUMN notifications.type IS 'Type of notification (REVIEW_DUE, CYCLE_COMPLETED, SET_MASTERED, etc.)';
COMMENT ON COLUMN notifications.priority IS 'Priority level (LOW, MEDIUM, HIGH, URGENT)';
COMMENT ON COLUMN notifications.is_read IS 'Whether the notification has been read by the user';
COMMENT ON COLUMN notifications.scheduled_at IS 'When the notification is scheduled to be sent';
COMMENT ON COLUMN notifications.sent_at IS 'When the notification was actually sent'; 