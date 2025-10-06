-- =====================================================
-- Additional Indexes for Performance Optimization
-- Version: 3.0
-- Description: Additional indexes for better query performance
-- =====================================================

-- =====================================================
-- 1. COMPOSITE INDEXES FOR COMPLEX QUERIES
-- =====================================================

-- User indexes
CREATE INDEX IF NOT EXISTS idx_users_status_created_at ON users(status, created_at);
CREATE INDEX IF NOT EXISTS idx_users_language_timezone ON users(preferred_language, timezone);
CREATE INDEX IF NOT EXISTS idx_users_status_language ON users(status, preferred_language);

-- Learning Set indexes
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_status ON learning_sets(user_id, status);
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_category ON learning_sets(user_id, category);
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_cycle ON learning_sets(user_id, current_cycle);
CREATE INDEX IF NOT EXISTS idx_learning_sets_status_category ON learning_sets(status, category);
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_created_at ON learning_sets(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_reviewed_at ON learning_sets(user_id, last_reviewed_at);

-- Review History indexes
CREATE INDEX IF NOT EXISTS idx_review_histories_set_cycle ON review_histories(set_id, cycle_id);
CREATE INDEX IF NOT EXISTS idx_review_histories_set_status ON review_histories(set_id, status);
CREATE INDEX IF NOT EXISTS idx_review_histories_cycle_status ON review_histories(cycle_id, status);
CREATE INDEX IF NOT EXISTS idx_review_histories_set_date ON review_histories(set_id, review_date);
CREATE INDEX IF NOT EXISTS idx_review_histories_cycle_date ON review_histories(cycle_id, review_date);
CREATE INDEX IF NOT EXISTS idx_review_histories_set_score ON review_histories(set_id, score);
CREATE INDEX IF NOT EXISTS idx_review_histories_status_date ON review_histories(status, review_date);

-- Reminder Schedule indexes
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_user_date ON reminder_schedules(user_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_user_status ON reminder_schedules(user_id, status);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_set_date ON reminder_schedules(set_id, scheduled_date);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_set_status ON reminder_schedules(set_id, status);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_date_status ON reminder_schedules(scheduled_date, status);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_user_time ON reminder_schedules(user_id, reminder_time);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_date_time ON reminder_schedules(scheduled_date, reminder_time);

-- Statistics indexes
CREATE INDEX IF NOT EXISTS idx_statistics_user_type ON statistics(user_id, stat_type);
CREATE INDEX IF NOT EXISTS idx_statistics_user_date ON statistics(user_id, stat_date);
CREATE INDEX IF NOT EXISTS idx_statistics_set_type ON statistics(set_id, stat_type);
CREATE INDEX IF NOT EXISTS idx_statistics_set_date ON statistics(set_id, stat_date);
CREATE INDEX IF NOT EXISTS idx_statistics_type_date ON statistics(stat_type, stat_date);
CREATE INDEX IF NOT EXISTS idx_statistics_user_set_type ON statistics(user_id, set_id, stat_type);

-- Activity Log indexes
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_type ON activity_logs(user_id, action_type);
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_entity ON activity_logs(user_id, entity_type);
CREATE INDEX IF NOT EXISTS idx_activity_logs_type_entity ON activity_logs(action_type, entity_type);
CREATE INDEX IF NOT EXISTS idx_activity_logs_user_date ON activity_logs(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_activity_logs_type_date ON activity_logs(action_type, created_at);

-- =====================================================
-- 2. PARTIAL INDEXES FOR SPECIFIC CONDITIONS
-- =====================================================

-- Active users only
CREATE INDEX IF NOT EXISTS idx_users_active_email ON users(email) WHERE status = 'active';

-- Non-deleted learning sets only
CREATE INDEX IF NOT EXISTS idx_learning_sets_active_user ON learning_sets(user_id) WHERE deleted_at IS NULL;

-- Completed reviews only
CREATE INDEX IF NOT EXISTS idx_review_histories_completed_set ON review_histories(set_id, score) WHERE status = 'completed';

-- Pending reminders only
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_pending_date ON reminder_schedules(scheduled_date, reminder_time) WHERE status = 'pending';

-- User-level statistics only
CREATE INDEX IF NOT EXISTS idx_statistics_user_level ON statistics(user_id, stat_type, stat_date) WHERE set_id IS NULL;

-- =====================================================
-- 3. FUNCTIONAL INDEXES FOR TEXT SEARCH
-- =====================================================

-- Case-insensitive name search
CREATE INDEX IF NOT EXISTS idx_users_name_lower ON users(LOWER(full_name));
CREATE INDEX IF NOT EXISTS idx_learning_sets_name_lower ON learning_sets(LOWER(name));

-- Email domain search
CREATE INDEX IF NOT EXISTS idx_users_email_domain ON users(SUBSTRING(email FROM '@(.*)$'));

-- =====================================================
-- 4. INDEXES FOR DATE RANGE QUERIES
-- =====================================================

-- Date range queries for statistics
CREATE INDEX IF NOT EXISTS idx_statistics_date_range ON statistics(stat_date, stat_type, user_id);

-- Date range queries for activity logs
CREATE INDEX IF NOT EXISTS idx_activity_logs_date_range ON activity_logs(created_at, user_id, action_type);

-- Date range queries for review histories
CREATE INDEX IF NOT EXISTS idx_review_histories_date_range ON review_histories(review_date, set_id, status);

-- =====================================================
-- 5. INDEXES FOR AGGREGATION QUERIES
-- =====================================================

-- For counting and grouping operations
CREATE INDEX IF NOT EXISTS idx_learning_sets_user_status_count ON learning_sets(user_id, status, created_at);
CREATE INDEX IF NOT EXISTS idx_review_histories_set_status_count ON review_histories(set_id, status, review_date);
CREATE INDEX IF NOT EXISTS idx_reminder_schedules_user_status_count ON reminder_schedules(user_id, status, scheduled_date);

-- =====================================================
-- 6. INDEXES FOR FOREIGN KEY LOOKUPS
-- =====================================================

-- Set Items indexes
CREATE INDEX IF NOT EXISTS idx_set_items_set_order ON set_items(set_id, item_order);

-- Learning Cycles indexes
CREATE INDEX IF NOT EXISTS idx_learning_cycles_set_number ON learning_cycles(set_id, cycle_number);
CREATE INDEX IF NOT EXISTS idx_learning_cycles_set_status ON learning_cycles(set_id, status);

-- User Profile indexes
CREATE INDEX IF NOT EXISTS idx_user_profiles_user_active ON user_profiles(user_id) WHERE user_id IS NOT NULL;

-- User Settings indexes
CREATE INDEX IF NOT EXISTS idx_user_settings_user_active ON user_settings(user_id) WHERE user_id IS NOT NULL;

-- =====================================================
-- 7. INDEXES FOR UNIQUE CONSTRAINTS
-- =====================================================

-- Ensure unique constraint performance
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_active ON users(email) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uk_learning_cycles_set_cycle ON learning_cycles(set_id, cycle_number);
CREATE UNIQUE INDEX IF NOT EXISTS uk_review_histories_cycle_review ON review_histories(cycle_id, review_number);
CREATE UNIQUE INDEX IF NOT EXISTS uk_set_items_set_order ON set_items(set_id, item_order);
CREATE UNIQUE INDEX IF NOT EXISTS uk_statistics_user_set_type_date ON statistics(user_id, set_id, stat_type, stat_date);

-- =====================================================
-- 8. INDEXES FOR SOFT DELETE QUERIES
-- =====================================================

-- Soft delete performance
CREATE INDEX IF NOT EXISTS idx_users_deleted_at_null ON users(id) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_learning_sets_deleted_at_null ON learning_sets(id) WHERE deleted_at IS NULL;

-- =====================================================
-- INDEX CREATION COMPLETE
-- =====================================================
