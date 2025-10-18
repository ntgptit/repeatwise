-- RepeatWise Database Migration
-- Version: V7
-- Description: Create notification tables for daily reminders and delivery tracking
-- Requirements: UC-024 - Manage Notifications
-- Date: 2025-01-18

-- ============================================================
-- Table 1: notification_settings - User Notification Preferences
-- ============================================================
CREATE TABLE IF NOT EXISTS notification_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    daily_reminder_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_time TIME NOT NULL DEFAULT '09:00',
    daily_reminder_days VARCHAR(50) NOT NULL DEFAULT 'MON,TUE,WED,THU,FRI,SAT,SUN',
    notification_method VARCHAR(20) NOT NULL DEFAULT 'EMAIL',
    notification_email VARCHAR(255),
    push_token VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_notification_settings_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_notification_method CHECK (notification_method IN ('EMAIL', 'PUSH', 'SMS')),
    CONSTRAINT chk_notification_email_format CHECK (
        notification_email IS NULL OR
        notification_email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'
    ),
    CONSTRAINT chk_daily_reminder_days_valid CHECK (
        daily_reminder_days ~* '^(MON|TUE|WED|THU|FRI|SAT|SUN)(,(MON|TUE|WED|THU|FRI|SAT|SUN))*$'
    )
);

-- Indexes for notification_settings
CREATE INDEX IF NOT EXISTS idx_notification_settings_user
    ON notification_settings(user_id);

-- Index for batch notification query (cron job)
CREATE INDEX IF NOT EXISTS idx_notification_settings_batch
    ON notification_settings(daily_reminder_enabled, daily_reminder_time)
    WHERE daily_reminder_enabled = TRUE;

-- ============================================================
-- Table 2: notification_logs - Notification Delivery History
-- ============================================================
CREATE TABLE IF NOT EXISTS notification_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    notification_method VARCHAR(20) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    metadata JSONB,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_notification_logs_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT chk_notification_type CHECK (
        notification_type IN ('DAILY_REMINDER', 'STREAK_REMINDER', 'ACHIEVEMENT', 'SYSTEM')
    ),
    CONSTRAINT chk_notification_method_log CHECK (
        notification_method IN ('EMAIL', 'PUSH', 'SMS')
    ),
    CONSTRAINT chk_notification_status CHECK (
        status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'BOUNCED')
    ),
    CONSTRAINT chk_delivered_at_after_sent CHECK (
        delivered_at IS NULL OR delivered_at >= sent_at
    )
);

-- Indexes for notification_logs
CREATE INDEX IF NOT EXISTS idx_notification_logs_user_date
    ON notification_logs(user_id, sent_at DESC);

-- Index for failed notifications (retry queue)
CREATE INDEX IF NOT EXISTS idx_notification_logs_status
    ON notification_logs(status, sent_at DESC)
    WHERE status IN ('PENDING', 'FAILED');

CREATE INDEX IF NOT EXISTS idx_notification_logs_type_date
    ON notification_logs(notification_type, sent_at DESC);

-- Index for cleanup job (delete old logs)
CREATE INDEX IF NOT EXISTS idx_notification_logs_cleanup
    ON notification_logs(sent_at)
    WHERE status IN ('SENT', 'DELIVERED');

-- ============================================================
-- Comments for documentation
-- ============================================================
COMMENT ON TABLE notification_settings IS 'User notification preferences for daily reminders and push notifications';
COMMENT ON TABLE notification_logs IS 'Immutable notification delivery history for audit trail and analytics';

COMMENT ON COLUMN notification_settings.daily_reminder_enabled IS 'Master switch for daily reminders';
COMMENT ON COLUMN notification_settings.daily_reminder_time IS 'Time to send daily reminder (user local time)';
COMMENT ON COLUMN notification_settings.daily_reminder_days IS 'CSV of days: MON,TUE,WED,THU,FRI,SAT,SUN';
COMMENT ON COLUMN notification_settings.notification_method IS 'Delivery method: EMAIL (MVP), PUSH (Future), SMS (Future)';
COMMENT ON COLUMN notification_settings.notification_email IS 'Custom email address (defaults to user.email if NULL)';
COMMENT ON COLUMN notification_settings.push_token IS 'FCM/APNs device token for push notifications (Future)';

COMMENT ON COLUMN notification_logs.notification_type IS 'Type: DAILY_REMINDER, STREAK_REMINDER, ACHIEVEMENT, SYSTEM';
COMMENT ON COLUMN notification_logs.status IS 'Status: PENDING, SENT, DELIVERED, FAILED, BOUNCED';
COMMENT ON COLUMN notification_logs.error_message IS 'Error details for failed notifications (SMTP errors, bounce messages)';
COMMENT ON COLUMN notification_logs.metadata IS 'JSONB field for extensibility (due_cards_count, streak_days, retry_count, etc.)';
