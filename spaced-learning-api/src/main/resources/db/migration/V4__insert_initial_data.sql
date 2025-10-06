-- =====================================================
-- Initial Data Insertion
-- Version: 4.0
-- Description: Insert initial system data and sample data
-- =====================================================

-- =====================================================
-- 1. INSERT SYSTEM CONFIGURATION DATA
-- =====================================================

-- SRS Algorithm Parameters
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('srs_base_delay_days', '30', 'Base delay in days for next learning cycle'),
('srs_score_penalty_factor', '0.2', 'Penalty factor for low scores in cycle delay calculation'),
('srs_word_count_scaling_factor', '0.02', 'Scaling factor for word count in cycle delay calculation'),
('srs_min_delay_days', '7', 'Minimum delay in days for next learning cycle'),
('srs_max_delay_days', '90', 'Maximum delay in days for next learning cycle');

-- Reminder Settings
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('reminder_max_daily_sets', '3', 'Maximum number of daily reminders per user'),
('reminder_default_time', '09:00', 'Default reminder time for new users'),
('reminder_reschedule_limit', '2', 'Maximum number of reschedules allowed'),
('reminder_advance_notice_hours', '24', 'Hours in advance to send reminder notifications');

-- System Limits
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('system_max_sets_per_user', '100', 'Maximum number of sets per user'),
('system_max_word_count_per_set', '10000', 'Maximum word count per set'),
('system_max_items_per_set', '1000', 'Maximum items per set'),
('system_session_timeout_seconds', '3600', 'Session timeout in seconds'),
('system_password_min_length', '8', 'Minimum password length'),
('system_password_max_length', '128', 'Maximum password length');

-- Default User Settings
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('default_timezone', 'Asia/Ho_Chi_Minh', 'Default timezone for new users'),
('default_language', 'VI', 'Default language for new users'),
('default_reminder_time', '09:00', 'Default reminder time for new users'),
('default_notification_enabled', 'true', 'Default notification setting for new users'),
('default_email_notifications', 'true', 'Default email notification setting for new users'),
('default_push_notifications', 'true', 'Default push notification setting for new users'),
('default_daily_reminder_limit', '3', 'Default daily reminder limit for new users');

-- Feature Flags
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('feature_advanced_statistics', 'true', 'Enable advanced statistics features'),
('feature_export_data', 'true', 'Enable data export functionality'),
('feature_import_data', 'true', 'Enable data import functionality'),
('feature_backup_restore', 'true', 'Enable backup and restore functionality'),
('feature_social_features', 'false', 'Enable social features (sharing, leaderboards)'),
('feature_ai_suggestions', 'false', 'Enable AI-powered learning suggestions');

-- Performance Settings
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('performance_cache_ttl_seconds', '3600', 'Cache time-to-live in seconds'),
('performance_batch_size', '100', 'Default batch size for bulk operations'),
('performance_max_concurrent_users', '1000', 'Maximum concurrent users'),
('performance_query_timeout_seconds', '30', 'Query timeout in seconds');

-- =====================================================
-- 2. INSERT SAMPLE USER DATA (FOR TESTING)
-- =====================================================

-- Sample Users
INSERT INTO users (user_id, email, password_hash, full_name, preferred_language, timezone, default_reminder_time, status) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin@repeatwise.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin User', 'EN', 'UTC', '09:00', 'active'),
('550e8400-e29b-41d4-a716-446655440002', 'user1@repeatwise.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Nguyễn Văn A', 'VI', 'Asia/Ho_Chi_Minh', '08:00', 'active'),
('550e8400-e29b-41d4-a716-446655440003', 'user2@repeatwise.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Trần Thị B', 'VI', 'Asia/Ho_Chi_Minh', '19:00', 'active'),
('550e8400-e29b-41d4-a716-446655440004', 'user3@repeatwise.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'John Smith', 'EN', 'America/New_York', '10:00', 'active');

-- Sample User Profiles
INSERT INTO user_profiles (profile_id, user_id, avatar_url, bio) VALUES
('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'https://example.com/avatars/admin.jpg', 'System Administrator'),
('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'https://example.com/avatars/user1.jpg', 'Học tiếng Anh để phát triển sự nghiệp'),
('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'https://example.com/avatars/user2.jpg', 'Yêu thích học từ vựng mới mỗi ngày'),
('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'https://example.com/avatars/user3.jpg', 'Learning Vietnamese for business');

-- Sample User Settings
INSERT INTO user_settings (settings_id, user_id, notification_enabled, email_notifications, push_notifications, daily_reminder_limit, learning_preferences) VALUES
('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', true, true, true, 5, '{"difficulty": "intermediate", "focus_areas": ["vocabulary", "grammar"]}'),
('770e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', true, true, false, 3, '{"difficulty": "beginner", "focus_areas": ["vocabulary"]}'),
('770e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', true, false, true, 2, '{"difficulty": "advanced", "focus_areas": ["grammar", "pronunciation"]}'),
('770e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', false, false, false, 1, '{"difficulty": "beginner", "focus_areas": ["vocabulary"]}');

-- =====================================================
-- 3. INSERT SAMPLE LEARNING SETS
-- =====================================================

-- Sample Learning Sets
INSERT INTO learning_sets (set_id, user_id, name, description, category, word_count, status, current_cycle, total_reviews, average_score) VALUES
('880e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'Từ vựng tiếng Anh cơ bản', 'Các từ vựng tiếng Anh cơ bản cho người mới bắt đầu', 'vocabulary', 50, 'learning', 2, 15, 85.5),
('880e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'Ngữ pháp tiếng Anh', 'Các cấu trúc ngữ pháp tiếng Anh quan trọng', 'grammar', 30, 'reviewing', 3, 25, 78.2),
('880e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', 'Từ vựng nâng cao', 'Từ vựng tiếng Anh nâng cao cho người có trình độ cao', 'vocabulary', 100, 'mastered', 5, 50, 92.8),
('880e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', 'Vietnamese Basics', 'Basic Vietnamese vocabulary for English speakers', 'vocabulary', 40, 'not_started', 1, 0, NULL);

-- Sample Set Items
INSERT INTO set_items (item_id, set_id, front_content, back_content, item_order) VALUES
-- Items for "Từ vựng tiếng Anh cơ bản"
('990e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', 'Hello', 'Xin chào', 1),
('990e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440001', 'Goodbye', 'Tạm biệt', 2),
('990e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440001', 'Thank you', 'Cảm ơn', 3),
('990e8400-e29b-41d4-a716-446655440004', '880e8400-e29b-41d4-a716-446655440001', 'Please', 'Làm ơn', 4),
('990e8400-e29b-41d4-a716-446655440005', '880e8400-e29b-41d4-a716-446655440001', 'Sorry', 'Xin lỗi', 5),

-- Items for "Ngữ pháp tiếng Anh"
('990e8400-e29b-41d4-a716-446655440006', '880e8400-e29b-41d4-a716-446655440002', 'Present Simple', 'Thì hiện tại đơn: S + V(s/es) + O', 1),
('990e8400-e29b-41d4-a716-446655440007', '880e8400-e29b-41d4-a716-446655440002', 'Past Simple', 'Thì quá khứ đơn: S + V2 + O', 2),
('990e8400-e29b-41d4-a716-446655440008', '880e8400-e29b-41d4-a716-446655440002', 'Future Simple', 'Thì tương lai đơn: S + will + V + O', 3),

-- Items for "Vietnamese Basics"
('990e8400-e29b-41d4-a716-446655440009', '880e8400-e29b-41d4-a716-446655440004', 'Xin chào', 'Hello', 1),
('990e8400-e29b-41d4-a716-446655440010', '880e8400-e29b-41d4-a716-446655440004', 'Tạm biệt', 'Goodbye', 2),
('990e8400-e29b-41d4-a716-446655440011', '880e8400-e29b-41d4-a716-446655440004', 'Cảm ơn', 'Thank you', 3);

-- =====================================================
-- 4. INSERT SAMPLE LEARNING CYCLES
-- =====================================================

-- Sample Learning Cycles
INSERT INTO learning_cycles (cycle_id, set_id, cycle_number, start_date, end_date, average_score, status, next_cycle_delay_days) VALUES
('aa0e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', 1, '2024-01-01', '2024-01-15', 82.5, 'completed', 30),
('aa0e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440001', 2, '2024-02-14', NULL, 88.5, 'active', NULL),
('aa0e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440002', 1, '2024-01-10', '2024-01-25', 75.0, 'completed', 35),
('aa0e8400-e29b-41d4-a716-446655440004', '880e8400-e29b-41d4-a716-446655440002', 2, '2024-03-01', '2024-03-15', 80.0, 'completed', 40),
('aa0e8400-e29b-41d4-a716-446655440005', '880e8400-e29b-41d4-a716-446655440002', 3, '2024-04-24', NULL, 78.2, 'active', NULL);

-- =====================================================
-- 5. INSERT SAMPLE REVIEW HISTORIES
-- =====================================================

-- Sample Review Histories
INSERT INTO review_histories (review_id, set_id, cycle_id, review_number, score, status, review_date, notes) VALUES
-- Reviews for "Từ vựng tiếng Anh cơ bản" Cycle 1
('bb0e8400-e29b-41d4-a716-446655440001', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440001', 1, 80, 'completed', '2024-01-01', 'Lần đầu học, cần cải thiện'),
('bb0e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440001', 2, 85, 'completed', '2024-01-03', 'Tiến bộ tốt'),
('bb0e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440001', 3, 90, 'completed', '2024-01-05', 'Rất tốt'),
('bb0e8400-e29b-41d4-a716-446655440004', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440001', 4, 88, 'completed', '2024-01-08', 'Ổn định'),
('bb0e8400-e29b-41d4-a716-446655440005', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440001', 5, 92, 'completed', '2024-01-12', 'Xuất sắc'),

-- Reviews for "Từ vựng tiếng Anh cơ bản" Cycle 2
('bb0e8400-e29b-41d4-a716-446655440006', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440002', 1, 88, 'completed', '2024-02-14', 'Bắt đầu chu kỳ mới'),
('bb0e8400-e29b-41d4-a716-446655440007', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440002', 2, 90, 'completed', '2024-02-16', 'Tiếp tục tốt'),
('bb0e8400-e29b-41d4-a716-446655440008', '880e8400-e29b-41d4-a716-446655440001', 'aa0e8400-e29b-41d4-a716-446655440002', 3, 87, 'completed', '2024-02-18', 'Cần chú ý hơn'),

-- Reviews for "Ngữ pháp tiếng Anh" Cycle 1
('bb0e8400-e29b-41d4-a716-446655440009', '880e8400-e29b-41d4-a716-446655440002', 'aa0e8400-e29b-41d4-a716-446655440003', 1, 70, 'completed', '2024-01-10', 'Khó hơn từ vựng'),
('bb0e8400-e29b-41d4-a716-446655440010', '880e8400-e29b-41d4-a716-446655440002', 'aa0e8400-e29b-41d4-a716-446655440003', 2, 75, 'completed', '2024-01-12', 'Cải thiện dần'),
('bb0e8400-e29b-41d4-a716-446655440011', '880e8400-e29b-41d4-a716-446655440002', 'aa0e8400-e29b-41d4-a716-446655440003', 3, 80, 'completed', '2024-01-15', 'Tốt hơn'),
('bb0e8400-e29b-41d4-a716-446655440012', '880e8400-e29b-41d4-a716-446655440002', 'aa0e8400-e29b-41d4-a716-446655440003', 4, 78, 'completed', '2024-01-18', 'Ổn định'),
('bb0e8400-e29b-41d4-a716-446655440013', '880e8400-e29b-41d4-a716-446655440002', 'aa0e8400-e29b-41d4-a716-446655440003', 5, 82, 'completed', '2024-01-22', 'Hoàn thành chu kỳ');

-- =====================================================
-- 6. INSERT SAMPLE REMINDER SCHEDULES
-- =====================================================

-- Sample Reminder Schedules
INSERT INTO reminder_schedules (reminder_id, user_id, set_id, scheduled_date, reminder_time, status, reschedule_count) VALUES
('cc0e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440001', '2024-12-20', '08:00', 'pending', 0),
('cc0e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440002', '2024-12-21', '08:00', 'pending', 0),
('cc0e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440003', '2024-12-20', '19:00', 'pending', 0),
('cc0e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440004', '880e8400-e29b-41d4-a716-446655440004', '2024-12-22', '10:00', 'pending', 0);

-- =====================================================
-- 7. INSERT SAMPLE STATISTICS
-- =====================================================

-- Sample Statistics
INSERT INTO statistics (stat_id, user_id, set_id, stat_type, stat_date, stat_value, metadata) VALUES
-- User-level statistics
('dd0e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', NULL, 'daily_reviews', '2024-12-19', 15.0, '{"total_time_minutes": 45, "accuracy": 0.85}'),
('dd0e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', NULL, 'weekly_reviews', '2024-12-15', 85.0, '{"total_time_minutes": 300, "accuracy": 0.82}'),
('dd0e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', NULL, 'learning_streak', '2024-12-19', 7.0, '{"current_streak": 7, "longest_streak": 15}'),
('dd0e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', NULL, 'total_sets', '2024-12-19', 2.0, '{"active_sets": 2, "completed_sets": 0}'),

-- Set-level statistics
('dd0e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440001', 'average_score', '2024-12-19', 85.5, '{"reviews_count": 15, "last_review_date": "2024-12-18"}'),
('dd0e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440002', '880e8400-e29b-41d4-a716-446655440002', 'average_score', '2024-12-19', 78.2, '{"reviews_count": 25, "last_review_date": "2024-12-17"}'),
('dd0e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440003', '880e8400-e29b-41d4-a716-446655440003', 'average_score', '2024-12-19', 92.8, '{"reviews_count": 50, "last_review_date": "2024-12-18"}');

-- =====================================================
-- 8. INSERT SAMPLE ACTIVITY LOGS
-- =====================================================

-- Sample Activity Logs
INSERT INTO activity_logs (log_id, user_id, action_type, entity_type, entity_id, old_values, new_values, ip_address, user_agent) VALUES
('ee0e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', 'create', 'set', '880e8400-e29b-41d4-a716-446655440001', NULL, '{"name": "Từ vựng tiếng Anh cơ bản", "category": "vocabulary"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('ee0e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440002', 'complete_review', 'review', 'bb0e8400-e29b-41d4-a716-446655440001', NULL, '{"score": 80, "status": "completed"}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('ee0e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440002', 'update', 'set', '880e8400-e29b-41d4-a716-446655440001', '{"current_cycle": 1}', '{"current_cycle": 2}', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
('ee0e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440003', 'create', 'set', '880e8400-e29b-41d4-a716-446655440003', NULL, '{"name": "Từ vựng nâng cao", "category": "vocabulary"}', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36');

-- =====================================================
-- INITIAL DATA INSERTION COMPLETE
-- =====================================================
