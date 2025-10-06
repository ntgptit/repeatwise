-- =====================================================
-- RepeatWise Database Schema
-- Version: 2.0
-- Description: Complete database schema for RepeatWise spaced repetition system
-- =====================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- 1. USERS TABLE
-- =====================================================
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    preferred_language VARCHAR(2) NOT NULL DEFAULT 'VI' CHECK (preferred_language IN ('VI', 'EN')),
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    default_reminder_time TIME NOT NULL DEFAULT '09:00',
    status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'suspended')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Constraints
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_password_hash_length CHECK (LENGTH(password_hash) >= 60),
    CONSTRAINT chk_full_name_not_empty CHECK (TRIM(full_name) != ''),
    CONSTRAINT chk_timezone_valid CHECK (timezone IN ('Asia/Ho_Chi_Minh', 'UTC', 'America/New_York', 'Europe/London'))
);

-- Create indexes for users table
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_created_at ON users(created_at);

-- =====================================================
-- 2. USER PROFILES TABLE
-- =====================================================
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    avatar_url VARCHAR(500) NULL,
    bio TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_avatar_url_format CHECK (avatar_url IS NULL OR avatar_url ~* '^https?://.*'),
    CONSTRAINT chk_bio_length CHECK (bio IS NULL OR LENGTH(bio) <= 1000)
);

CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- =====================================================
-- 3. USER SETTINGS TABLE
-- =====================================================
CREATE TABLE user_settings (
    settings_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    push_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_limit INTEGER NOT NULL DEFAULT 3,
    learning_preferences JSONB NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT chk_daily_reminder_limit CHECK (daily_reminder_limit >= 1 AND daily_reminder_limit <= 10)
);

CREATE INDEX idx_user_settings_user_id ON user_settings(user_id);

-- =====================================================
-- 4. LEARNING SETS TABLE
-- =====================================================
CREATE TABLE learning_sets (
    set_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'vocabulary' CHECK (category IN ('vocabulary', 'grammar', 'mixed', 'other')),
    word_count INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'not_started' CHECK (status IN ('not_started', 'learning', 'reviewing', 'mastered')),
    current_cycle INTEGER NOT NULL DEFAULT 1,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    average_score DECIMAL(5,2) NULL,
    last_reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_set_name_not_empty CHECK (TRIM(name) != ''),
    CONSTRAINT chk_set_name_length CHECK (LENGTH(name) <= 100),
    CONSTRAINT chk_set_description_length CHECK (description IS NULL OR LENGTH(description) <= 500),
    CONSTRAINT chk_word_count_positive CHECK (word_count >= 0),
    CONSTRAINT chk_current_cycle_positive CHECK (current_cycle > 0),
    CONSTRAINT chk_total_reviews_non_negative CHECK (total_reviews >= 0),
    CONSTRAINT chk_average_score_range CHECK (average_score IS NULL OR (average_score >= 0 AND average_score <= 100)),
    CONSTRAINT chk_status_transition_valid CHECK (
        (status = 'not_started' AND current_cycle = 1) OR
        (status = 'learning' AND current_cycle >= 1) OR
        (status = 'reviewing' AND current_cycle >= 1) OR
        (status = 'mastered' AND current_cycle >= 1)
    )
);

-- Create indexes for learning_sets table
CREATE INDEX idx_learning_sets_user_id ON learning_sets(user_id);
CREATE INDEX idx_learning_sets_status ON learning_sets(status);
CREATE INDEX idx_learning_sets_category ON learning_sets(category);
CREATE INDEX idx_learning_sets_deleted_at ON learning_sets(deleted_at);
CREATE INDEX idx_learning_sets_last_reviewed_at ON learning_sets(last_reviewed_at);
CREATE INDEX idx_learning_sets_created_at ON learning_sets(created_at);

-- =====================================================
-- 5. SET ITEMS TABLE
-- =====================================================
CREATE TABLE set_items (
    item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL,
    front_content TEXT NOT NULL,
    back_content TEXT NOT NULL,
    item_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES learning_sets(set_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_front_content_not_empty CHECK (TRIM(front_content) != ''),
    CONSTRAINT chk_back_content_not_empty CHECK (TRIM(back_content) != ''),
    CONSTRAINT chk_item_order_positive CHECK (item_order > 0),
    UNIQUE KEY uk_set_item_order (set_id, item_order)
);

CREATE INDEX idx_set_items_set_id ON set_items(set_id);
CREATE INDEX idx_set_items_order ON set_items(item_order);

-- =====================================================
-- 6. LEARNING CYCLES TABLE
-- =====================================================
CREATE TABLE learning_cycles (
    cycle_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL,
    cycle_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    average_score DECIMAL(5,2) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'completed', 'paused')),
    next_cycle_delay_days INTEGER NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES learning_sets(set_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cycles_set_cycle (set_id, cycle_number),
    
    -- Business Rule Constraints
    CONSTRAINT chk_cycle_number_positive CHECK (cycle_number > 0),
    CONSTRAINT chk_start_date_not_future CHECK (start_date <= CURRENT_DATE),
    CONSTRAINT chk_end_date_after_start CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_average_score_range CHECK (average_score IS NULL OR (average_score >= 0 AND average_score <= 100)),
    CONSTRAINT chk_next_cycle_delay_range CHECK (next_cycle_delay_days IS NULL OR (next_cycle_delay_days >= 7 AND next_cycle_delay_days <= 90))
);

CREATE INDEX idx_learning_cycles_set_id ON learning_cycles(set_id);
CREATE INDEX idx_learning_cycles_status ON learning_cycles(status);
CREATE INDEX idx_learning_cycles_start_date ON learning_cycles(start_date);

-- =====================================================
-- 7. REVIEW HISTORIES TABLE
-- =====================================================
CREATE TABLE review_histories (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL,
    cycle_id UUID NOT NULL,
    review_number INTEGER NOT NULL,
    score INTEGER NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('completed', 'skipped')),
    skip_reason VARCHAR(20) NULL CHECK (skip_reason IN ('forgot', 'busy', 'other')),
    review_date DATE NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES learning_sets(set_id) ON DELETE CASCADE,
    FOREIGN KEY (cycle_id) REFERENCES learning_cycles(cycle_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_review_number_range CHECK (review_number >= 1 AND review_number <= 5),
    CONSTRAINT chk_score_range CHECK (score IS NULL OR (score >= 0 AND score <= 100)),
    CONSTRAINT chk_skip_reason_when_skipped CHECK (
        (status = 'skipped' AND skip_reason IS NOT NULL) OR
        (status = 'completed' AND skip_reason IS NULL)
    ),
    CONSTRAINT chk_review_date_not_future CHECK (review_date <= CURRENT_DATE),
    CONSTRAINT chk_notes_length CHECK (notes IS NULL OR LENGTH(notes) <= 500),
    UNIQUE KEY uk_cycle_review_number (cycle_id, review_number)
);

CREATE INDEX idx_review_histories_set_id ON review_histories(set_id);
CREATE INDEX idx_review_histories_cycle_id ON review_histories(cycle_id);
CREATE INDEX idx_review_histories_review_date ON review_histories(review_date);
CREATE INDEX idx_review_histories_status ON review_histories(status);
CREATE INDEX idx_review_histories_created_at ON review_histories(created_at);

-- =====================================================
-- 8. REMINDER SCHEDULES TABLE
-- =====================================================
CREATE TABLE reminder_schedules (
    reminder_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    set_id UUID NOT NULL,
    scheduled_date DATE NOT NULL,
    reminder_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'sent', 'cancelled', 'done', 'skipped', 'rescheduled')),
    sent_at TIMESTAMP NULL,
    reschedule_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (set_id) REFERENCES learning_sets(set_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_scheduled_date_not_past CHECK (scheduled_date >= CURRENT_DATE),
    CONSTRAINT chk_sent_at_when_sent CHECK (
        (status = 'sent' AND sent_at IS NOT NULL) OR
        (status IN ('pending', 'cancelled', 'done', 'skipped', 'rescheduled') AND sent_at IS NULL)
    ),
    CONSTRAINT chk_reminder_time_valid CHECK (reminder_time BETWEEN '00:00:00' AND '23:59:59'),
    CONSTRAINT chk_reschedule_count_range CHECK (reschedule_count >= 0 AND reschedule_count <= 2)
);

CREATE INDEX idx_reminder_schedules_user_id ON reminder_schedules(user_id);
CREATE INDEX idx_reminder_schedules_set_id ON reminder_schedules(set_id);
CREATE INDEX idx_reminder_schedules_scheduled_date ON reminder_schedules(scheduled_date);
CREATE INDEX idx_reminder_schedules_status ON reminder_schedules(status);
CREATE INDEX idx_reminder_schedules_created_at ON reminder_schedules(created_at);

-- =====================================================
-- 9. ACTIVITY LOGS TABLE
-- =====================================================
CREATE TABLE activity_logs (
    log_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    old_values JSONB NULL,
    new_values JSONB NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_action_type_valid CHECK (action_type IN (
        'create', 'update', 'delete', 'login', 'logout', 'view_set', 'view_profile',
        'start_cycle', 'complete_review', 'skip_review', 'archive', 'reschedule'
    )),
    CONSTRAINT chk_entity_type_valid CHECK (entity_type IN (
        'user', 'set', 'cycle', 'review', 'reminder', 'profile', 'settings'
    )),
    CONSTRAINT chk_ip_address_format CHECK (ip_address IS NULL OR ip_address ~* '^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$')
);

CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_action_type ON activity_logs(action_type);
CREATE INDEX idx_activity_logs_entity_type ON activity_logs(entity_type);
CREATE INDEX idx_activity_logs_created_at ON activity_logs(created_at);

-- =====================================================
-- 10. SYSTEM CONFIGURATION TABLE
-- =====================================================
CREATE TABLE system_configuration (
    config_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Business Rule Constraints
    CONSTRAINT chk_config_key_format CHECK (config_key ~* '^[a-z_][a-z0-9_]*$'),
    CONSTRAINT chk_config_value_not_empty CHECK (TRIM(config_value) != ''),
    CONSTRAINT chk_description_length CHECK (description IS NULL OR LENGTH(description) <= 500)
);

CREATE INDEX idx_system_configuration_key ON system_configuration(config_key);
CREATE INDEX idx_system_configuration_active ON system_configuration(is_active);

-- =====================================================
-- 11. STATISTICS TABLE (for analytics and reporting)
-- =====================================================
CREATE TABLE statistics (
    stat_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    set_id UUID NULL, -- NULL for user-level statistics
    stat_type VARCHAR(50) NOT NULL,
    stat_date DATE NOT NULL,
    stat_value DECIMAL(10,2) NOT NULL,
    metadata JSONB NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (set_id) REFERENCES learning_sets(set_id) ON DELETE CASCADE,
    
    -- Business Rule Constraints
    CONSTRAINT chk_stat_type_valid CHECK (stat_type IN (
        'daily_reviews', 'weekly_reviews', 'monthly_reviews',
        'average_score', 'total_sets', 'active_sets',
        'learning_streak', 'review_accuracy', 'time_spent'
    )),
    CONSTRAINT chk_stat_value_positive CHECK (stat_value >= 0),
    UNIQUE KEY uk_user_set_stat_date (user_id, set_id, stat_type, stat_date)
);

CREATE INDEX idx_statistics_user_id ON statistics(user_id);
CREATE INDEX idx_statistics_set_id ON statistics(set_id);
CREATE INDEX idx_statistics_stat_type ON statistics(stat_type);
CREATE INDEX idx_statistics_stat_date ON statistics(stat_date);
CREATE INDEX idx_statistics_created_at ON statistics(created_at);

-- =====================================================
-- 12. TRIGGERS FOR UPDATED_AT
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_profiles_updated_at BEFORE UPDATE ON user_profiles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_user_settings_updated_at BEFORE UPDATE ON user_settings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_learning_sets_updated_at BEFORE UPDATE ON learning_sets FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_set_items_updated_at BEFORE UPDATE ON set_items FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_learning_cycles_updated_at BEFORE UPDATE ON learning_cycles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_review_histories_updated_at BEFORE UPDATE ON review_histories FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_reminder_schedules_updated_at BEFORE UPDATE ON reminder_schedules FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_system_configuration_updated_at BEFORE UPDATE ON system_configuration FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_statistics_updated_at BEFORE UPDATE ON statistics FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- 13. INITIAL SYSTEM CONFIGURATION DATA
-- =====================================================
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('base_cycle_delay_days', '30', 'Base delay in days for next learning cycle'),
('score_penalty_factor', '0.2', 'Penalty factor for low scores in cycle delay calculation'),
('word_count_scaling_factor', '0.02', 'Scaling factor for word count in cycle delay calculation'),
('max_daily_reminders', '3', 'Maximum number of daily reminders per user'),
('min_cycle_delay_days', '7', 'Minimum delay in days for next learning cycle'),
('max_cycle_delay_days', '90', 'Maximum delay in days for next learning cycle'),
('default_timezone', 'Asia/Ho_Chi_Minh', 'Default timezone for new users'),
('default_language', 'VI', 'Default language for new users'),
('default_reminder_time', '09:00', 'Default reminder time for new users'),
('session_timeout_seconds', '3600', 'Session timeout in seconds'),
('max_sets_per_user', '100', 'Maximum number of sets per user'),
('max_word_count_per_set', '10000', 'Maximum word count per set');

-- =====================================================
-- 14. COMMENTS FOR DOCUMENTATION
-- =====================================================
COMMENT ON TABLE users IS 'User accounts and authentication information';
COMMENT ON TABLE user_profiles IS 'Extended user profile information';
COMMENT ON TABLE user_settings IS 'User preferences and notification settings';
COMMENT ON TABLE learning_sets IS 'Learning sets containing vocabulary/grammar items';
COMMENT ON TABLE set_items IS 'Individual items within a learning set';
COMMENT ON TABLE learning_cycles IS 'Learning cycles for spaced repetition';
COMMENT ON TABLE review_histories IS 'History of review sessions and scores';
COMMENT ON TABLE reminder_schedules IS 'Scheduled reminders for learning sessions';
COMMENT ON TABLE activity_logs IS 'Audit trail of user activities';
COMMENT ON TABLE system_configuration IS 'System-wide configuration settings';
COMMENT ON TABLE statistics IS 'Analytics and reporting data';

-- =====================================================
-- SCHEMA CREATION COMPLETE
-- =====================================================
