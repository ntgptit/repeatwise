# Database Schema

## 1. Overview

Database schema của RepeatWise được thiết kế để hỗ trợ Spaced Repetition System (SRS) với các tính năng chính:
- Quản lý người dùng và authentication
- Quản lý set học tập và chu kỳ ôn tập
- Lịch sử điểm số và tiến trình học tập
- Hệ thống nhắc nhở thông minh
- Phân tích và báo cáo

## 2. Database Tables

### 2.1 Users Table

```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    preferred_language ENUM('VI', 'EN') NOT NULL DEFAULT 'VI',
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    default_reminder_time TIME NOT NULL DEFAULT '09:00',
    status ENUM('active', 'inactive', 'suspended') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_users_email (email),
    INDEX idx_users_status (status),
    INDEX idx_users_deleted_at (deleted_at),
    
    -- Business Rule Constraints
    CONSTRAINT chk_email_format CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_password_hash_length CHECK (LENGTH(password_hash) >= 60),
    CONSTRAINT chk_full_name_not_empty CHECK (TRIM(full_name) != ''),
    CONSTRAINT chk_timezone_valid CHECK (timezone IN ('Asia/Ho_Chi_Minh', 'UTC', 'America/New_York', 'Europe/London'))
);
```

### 2.2 User Profiles Table

```sql
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    avatar_url VARCHAR(500) NULL,
    bio TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_profiles_user_id (user_id),
    
    -- Business Rule Constraints
    CONSTRAINT chk_avatar_url_format CHECK (avatar_url IS NULL OR avatar_url REGEXP '^https?://.*'),
    CONSTRAINT chk_bio_length CHECK (bio IS NULL OR LENGTH(bio) <= 1000)
);
```

### 2.3 User Settings Table

```sql
CREATE TABLE user_settings (
    settings_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    push_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    daily_reminder_limit INTEGER NOT NULL DEFAULT 3,
    learning_preferences JSON NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_settings_user_id (user_id),
    
    -- Business Rule Constraints (BR-010: Daily Reminder Limit)
    CONSTRAINT chk_daily_reminder_limit CHECK (daily_reminder_limit >= 1 AND daily_reminder_limit <= 10),
    CONSTRAINT chk_learning_preferences_json CHECK (learning_preferences IS NULL OR JSON_VALID(learning_preferences))
);
```

### 2.4 Sets Table

```sql
CREATE TABLE sets (
    set_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    category ENUM('vocabulary', 'grammar', 'mixed', 'other') NOT NULL DEFAULT 'vocabulary',
    word_count INTEGER NOT NULL DEFAULT 0,
    status ENUM('not_started', 'learning', 'reviewing', 'mastered') NOT NULL DEFAULT 'not_started',
    current_cycle INTEGER NOT NULL DEFAULT 1,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    average_score DECIMAL(5,2) NULL,
    last_reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_sets_user_id (user_id),
    INDEX idx_sets_status (status),
    INDEX idx_sets_category (category),
    INDEX idx_sets_deleted_at (deleted_at),
    INDEX idx_sets_last_reviewed_at (last_reviewed_at),
    
    -- Business Rule Constraints
    CONSTRAINT chk_set_name_not_empty CHECK (TRIM(name) != ''), -- BR-001
    CONSTRAINT chk_set_name_length CHECK (LENGTH(name) <= 100), -- BR-001
    CONSTRAINT chk_set_description_length CHECK (description IS NULL OR LENGTH(description) <= 500), -- BR-001
    CONSTRAINT chk_word_count_positive CHECK (word_count > 0), -- BR-001
    CONSTRAINT chk_current_cycle_positive CHECK (current_cycle > 0), -- BR-006
    CONSTRAINT chk_total_reviews_non_negative CHECK (total_reviews >= 0),
    CONSTRAINT chk_average_score_range CHECK (average_score IS NULL OR (average_score >= 0 AND average_score <= 100)),
    CONSTRAINT chk_status_transition_valid CHECK (
        (status = 'not_started' AND current_cycle = 1) OR
        (status = 'learning' AND current_cycle >= 1) OR
        (status = 'reviewing' AND current_cycle >= 1) OR
        (status = 'mastered' AND current_cycle >= 1)
    )
);
```

### 2.5 Set Items Table

```sql
CREATE TABLE set_items (
    item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    set_id UUID NOT NULL,
    front_content TEXT NOT NULL,
    back_content TEXT NOT NULL,
    item_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE,
    INDEX idx_set_items_set_id (set_id),
    INDEX idx_set_items_order (item_order),
    
    -- Business Rule Constraints
    CONSTRAINT chk_front_content_not_empty CHECK (TRIM(front_content) != ''),
    CONSTRAINT chk_back_content_not_empty CHECK (TRIM(back_content) != ''),
    CONSTRAINT chk_item_order_positive CHECK (item_order > 0),
    UNIQUE KEY uk_set_item_order (set_id, item_order)
);
```

### 2.6 Learning Cycles Table

```sql
CREATE TABLE learning_cycles (
    cycle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    set_id UUID NOT NULL,
    cycle_number INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    average_score DECIMAL(5,2) NULL,
    status ENUM('active', 'completed', 'paused') NOT NULL DEFAULT 'active',
    next_cycle_delay_days INTEGER NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE,
    UNIQUE KEY uk_cycles_set_cycle (set_id, cycle_number),
    INDEX idx_learning_cycles_set_id (set_id),
    INDEX idx_learning_cycles_status (status),
    INDEX idx_learning_cycles_start_date (start_date),
    
    -- Business Rule Constraints
    CONSTRAINT chk_cycle_number_positive CHECK (cycle_number > 0), -- BR-006
    CONSTRAINT chk_start_date_not_future CHECK (start_date <= CURDATE()),
    CONSTRAINT chk_end_date_after_start CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_average_score_range CHECK (average_score IS NULL OR (average_score >= 0 AND average_score <= 100)),
    CONSTRAINT chk_next_cycle_delay_range CHECK (next_cycle_delay_days IS NULL OR (next_cycle_delay_days >= 7 AND next_cycle_delay_days <= 90)) -- BR-005
);
```

### 2.7 Review Histories Table

```sql
CREATE TABLE review_histories (
    review_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    set_id UUID NOT NULL,
    cycle_id UUID NOT NULL,
    review_number INTEGER NOT NULL,
    score INTEGER NULL,
    status ENUM('completed', 'skipped') NOT NULL,
    skip_reason ENUM('forgot', 'busy', 'other') NULL,
    review_date DATE NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE,
    FOREIGN KEY (cycle_id) REFERENCES learning_cycles(cycle_id) ON DELETE CASCADE,
    INDEX idx_review_histories_set_id (set_id),
    INDEX idx_review_histories_cycle_id (cycle_id),
    INDEX idx_review_histories_review_date (review_date),
    INDEX idx_review_histories_status (status),
    
    -- Business Rule Constraints
    CONSTRAINT chk_review_number_range CHECK (review_number >= 1 AND review_number <= 5), -- BR-004
    CONSTRAINT chk_score_range CHECK (score IS NULL OR (score >= 0 AND score <= 100)), -- BR-009
    CONSTRAINT chk_skip_reason_when_skipped CHECK (
        (status = 'skipped' AND skip_reason IS NOT NULL) OR
        (status = 'completed' AND skip_reason IS NULL)
    ), -- BR-007
    CONSTRAINT chk_review_date_not_future CHECK (review_date <= CURDATE()),
    CONSTRAINT chk_notes_length CHECK (notes IS NULL OR LENGTH(notes) <= 500),
    UNIQUE KEY uk_cycle_review_number (cycle_id, review_number)
);
```

### 2.8 Reminder Schedules Table

```sql
CREATE TABLE reminder_schedules (
    reminder_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    set_id UUID NOT NULL,
    scheduled_date DATE NOT NULL,
    reminder_time TIME NOT NULL,
    status ENUM('pending', 'sent', 'cancelled') NOT NULL DEFAULT 'pending',
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE,
    INDEX idx_reminder_schedules_user_id (user_id),
    INDEX idx_reminder_schedules_set_id (set_id),
    INDEX idx_reminder_schedules_scheduled_date (scheduled_date),
    INDEX idx_reminder_schedules_status (status),
    
    -- Business Rule Constraints
    CONSTRAINT chk_scheduled_date_not_past CHECK (scheduled_date >= CURDATE()),
    CONSTRAINT chk_sent_at_when_sent CHECK (
        (status = 'sent' AND sent_at IS NOT NULL) OR
        (status IN ('pending', 'cancelled') AND sent_at IS NULL)
    ),
    CONSTRAINT chk_reminder_time_valid CHECK (reminder_time BETWEEN '00:00:00' AND '23:59:59')
);
```

### 2.9 Activity Logs Table

```sql
CREATE TABLE activity_logs (
    log_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    old_values JSON NULL,
    new_values JSON NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_activity_logs_user_id (user_id),
    INDEX idx_activity_logs_action_type (action_type),
    INDEX idx_activity_logs_entity_type (entity_type),
    INDEX idx_activity_logs_created_at (created_at),
    
    -- Business Rule Constraints
    CONSTRAINT chk_action_type_valid CHECK (action_type IN (
        'create', 'update', 'delete', 'login', 'logout', 'view_set', 'view_profile',
        'start_cycle', 'complete_review', 'skip_review', 'archive'
    )),
    CONSTRAINT chk_entity_type_valid CHECK (entity_type IN (
        'user', 'set', 'cycle', 'review', 'reminder', 'profile', 'settings'
    )),
    CONSTRAINT chk_ip_address_format CHECK (ip_address IS NULL OR ip_address REGEXP '^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$'),
    CONSTRAINT chk_json_valid CHECK (
        (old_values IS NULL OR JSON_VALID(old_values)) AND
        (new_values IS NULL OR JSON_VALID(new_values))
    )
);
```

### 2.10 System Configuration Table

```sql
CREATE TABLE system_configuration (
    config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_system_configuration_key (config_key),
    INDEX idx_system_configuration_active (is_active),
    
    -- Business Rule Constraints
    CONSTRAINT chk_config_key_format CHECK (config_key REGEXP '^[a-z_][a-z0-9_]*$'),
    CONSTRAINT chk_config_value_not_empty CHECK (TRIM(config_value) != ''),
    CONSTRAINT chk_description_length CHECK (description IS NULL OR LENGTH(description) <= 500)
);
```

## 3. Initial Configuration Data

### 3.1 System Configuration Values (BR-005 Implementation)
```sql
-- Insert default system configuration for cycle delay calculation
INSERT INTO system_configuration (config_key, config_value, description) VALUES
('base_cycle_delay_days', '30', 'Base delay in days for next learning cycle'),
('score_penalty_factor', '0.2', 'Penalty factor for low scores in cycle delay calculation'),
('word_count_scaling_factor', '0.02', 'Scaling factor for word count in cycle delay calculation'),
('max_daily_reminders', '3', 'Maximum number of daily reminders per user'),
('min_cycle_delay_days', '7', 'Minimum delay in days for next learning cycle'),
('max_cycle_delay_days', '90', 'Maximum delay in days for next learning cycle'),
('default_timezone', 'Asia/Ho_Chi_Minh', 'Default timezone for new users'),
('default_language', 'VI', 'Default language for new users'),
('default_reminder_time', '09:00', 'Default reminder time for new users');
```

## 4. Relationships

### 4.1 Primary Relationships
- **User → Sets**: One-to-Many (1 user có nhiều sets)
- **User → UserProfile**: One-to-One (1 user có 1 profile)
- **User → UserSettings**: One-to-One (1 user có 1 settings)
- **Set → SetItems**: One-to-Many (1 set có nhiều items)
- **Set → LearningCycles**: One-to-Many (1 set có nhiều cycles)
- **LearningCycle → ReviewHistories**: One-to-Many (1 cycle có nhiều reviews)
- **User → ReminderSchedules**: One-to-Many (1 user có nhiều reminders)
- **Set → ReminderSchedules**: One-to-Many (1 set có nhiều reminders)

### 4.2 Foreign Key Constraints
- Tất cả foreign keys đều sử dụng CASCADE DELETE để đảm bảo tính toàn vẹn dữ liệu
- Unique constraints được áp dụng cho các trường cần thiết (email, set_id + cycle_number)
- Indexes được tạo cho tất cả foreign keys và các trường thường xuyên query

## 5. Data Types and Constraints

### 5.1 UUID Usage
- Tất cả primary keys sử dụng UUID để đảm bảo tính duy nhất và bảo mật
- UUID được generate tự động bằng `gen_random_uuid()`

### 5.2 Timestamp Fields
- `created_at`: Tự động set khi tạo record
- `updated_at`: Tự động update khi có thay đổi
- `deleted_at`: Soft delete timestamp

### 5.3 Enum Fields
- Status fields sử dụng ENUM để đảm bảo tính nhất quán
- Category fields sử dụng ENUM để phân loại rõ ràng

### 5.4 JSON Fields
- `learning_preferences`: Lưu preferences dưới dạng JSON
- `old_values/new_values`: Lưu audit trail dưới dạng JSON

## 6. Performance Considerations

### 6.1 Indexing Strategy
- Primary keys tự động có index
- Foreign keys có index để tối ưu JOIN operations
- Composite indexes cho các query phức tạp
- Partial indexes cho soft delete records

### 6.2 Partitioning Strategy
- Có thể partition theo `created_at` cho các bảng lớn
- Partition theo `user_id` cho multi-tenant scenarios
- Partition theo `review_date` cho review_histories

### 6.3 Archiving Strategy
- Review histories cũ có thể được archive sau 2 năm
- Activity logs có thể được archive sau 1 năm
- Soft delete records có thể được permanently delete sau 6 tháng 
