# Data Design - RepeatWise

## 1. Overview

Data Design mô tả chi tiết cấu trúc dữ liệu, ràng buộc, indexes và relationships cho hệ thống RepeatWise. Thiết kế này đảm bảo tính toàn vẹn dữ liệu, hiệu suất truy vấn và khả năng mở rộng.

## 2. Database Tables

### 2.1 Users Table

#### 2.1.1 Table Structure
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
    deleted_at TIMESTAMP NULL
);
```

#### 2.1.2 Constraints
- **Primary Key**: `user_id` (UUID)
- **Unique**: `email` (case-insensitive)
- **Check Constraints**:
  - Email format: `email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'`
  - Password hash length: `LENGTH(password_hash) >= 60`
  - Full name not empty: `TRIM(full_name) != ''`
  - Valid timezone: `timezone IN ('Asia/Ho_Chi_Minh', 'UTC', 'America/New_York', 'Europe/London')`

#### 2.1.3 Indexes
```sql
-- Performance indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Composite indexes
CREATE INDEX idx_users_status_deleted ON users(status, deleted_at);
CREATE INDEX idx_users_email_status ON users(email, status);
```

#### 2.1.4 Business Rules
- **BR-020**: Email phải unique và valid format
- **BR-021**: Password phải được hash với BCrypt (cost=12)
- **BR-015**: User ID sử dụng UUID format
- **BR-016**: Bắt buộc có created_at, updated_at

### 2.2 User Profiles Table

#### 2.2.1 Table Structure
```sql
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    avatar_url VARCHAR(500) NULL,
    bio TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

#### 2.2.2 Constraints
- **Primary Key**: `profile_id` (UUID)
- **Foreign Key**: `user_id` → `users.user_id` (CASCADE DELETE)
- **Check Constraints**:
  - Avatar URL format: `avatar_url IS NULL OR avatar_url REGEXP '^https?://.*'`
  - Bio length: `bio IS NULL OR LENGTH(bio) <= 1000`

#### 2.2.3 Indexes
```sql
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE UNIQUE INDEX uk_user_profiles_user_id ON user_profiles(user_id);
```

### 2.3 User Settings Table

#### 2.3.1 Table Structure
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
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

#### 2.3.2 Constraints
- **Primary Key**: `settings_id` (UUID)
- **Foreign Key**: `user_id` → `users.user_id` (CASCADE DELETE)
- **Check Constraints**:
  - Daily reminder limit: `daily_reminder_limit >= 1 AND daily_reminder_limit <= 10`
  - JSON validation: `learning_preferences IS NULL OR JSON_VALID(learning_preferences)`

#### 2.3.3 Indexes
```sql
CREATE INDEX idx_user_settings_user_id ON user_settings(user_id);
CREATE UNIQUE INDEX uk_user_settings_user_id ON user_settings(user_id);
```

### 2.4 Sets Table

#### 2.4.1 Table Structure
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
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

#### 2.4.2 Constraints
- **Primary Key**: `set_id` (UUID)
- **Foreign Key**: `user_id` → `users.user_id` (CASCADE DELETE)
- **Check Constraints**:
  - Set name not empty: `TRIM(name) != ''`
  - Set name length: `LENGTH(name) <= 100`
  - Description length: `description IS NULL OR LENGTH(description) <= 500`
  - Word count positive: `word_count > 0`
  - Current cycle positive: `current_cycle > 0`
  - Total reviews non-negative: `total_reviews >= 0`
  - Average score range: `average_score IS NULL OR (average_score >= 0 AND average_score <= 100)`
  - Status transition valid: Complex constraint for valid state transitions

#### 2.4.3 Indexes
```sql
-- Performance indexes
CREATE INDEX idx_sets_user_id ON sets(user_id);
CREATE INDEX idx_sets_status ON sets(status);
CREATE INDEX idx_sets_category ON sets(category);
CREATE INDEX idx_sets_deleted_at ON sets(deleted_at);
CREATE INDEX idx_sets_last_reviewed_at ON sets(last_reviewed_at);
CREATE INDEX idx_sets_created_at ON sets(created_at);

-- Composite indexes
CREATE INDEX idx_sets_user_status ON sets(user_id, status);
CREATE INDEX idx_sets_user_deleted ON sets(user_id, deleted_at);
CREATE INDEX idx_sets_status_deleted ON sets(status, deleted_at);
```

### 2.5 Set Items Table

#### 2.5.1 Table Structure
```sql
CREATE TABLE set_items (
    item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    set_id UUID NOT NULL,
    front_content TEXT NOT NULL,
    back_content TEXT NOT NULL,
    item_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE
);
```

#### 2.5.2 Constraints
- **Primary Key**: `item_id` (UUID)
- **Foreign Key**: `set_id` → `sets.set_id` (CASCADE DELETE)
- **Unique**: `(set_id, item_order)` - Unique order within set
- **Check Constraints**:
  - Front content not empty: `TRIM(front_content) != ''`
  - Back content not empty: `TRIM(back_content) != ''`
  - Item order positive: `item_order > 0`

#### 2.5.3 Indexes
```sql
CREATE INDEX idx_set_items_set_id ON set_items(set_id);
CREATE INDEX idx_set_items_order ON set_items(item_order);
CREATE UNIQUE INDEX uk_set_item_order ON set_items(set_id, item_order);
```

### 2.6 Learning Cycles Table

#### 2.6.1 Table Structure
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
    
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE
);
```

#### 2.6.2 Constraints
- **Primary Key**: `cycle_id` (UUID)
- **Foreign Key**: `set_id` → `sets.set_id` (CASCADE DELETE)
- **Unique**: `(set_id, cycle_number)` - Unique cycle number within set
- **Check Constraints**:
  - Cycle number positive: `cycle_number > 0`
  - Start date not future: `start_date <= CURDATE()`
  - End date after start: `end_date IS NULL OR end_date >= start_date`
  - Average score range: `average_score IS NULL OR (average_score >= 0 AND average_score <= 100)`
  - Next cycle delay range: `next_cycle_delay_days IS NULL OR (next_cycle_delay_days >= 7 AND next_cycle_delay_days <= 90)`

#### 2.6.3 Indexes
```sql
CREATE INDEX idx_learning_cycles_set_id ON learning_cycles(set_id);
CREATE INDEX idx_learning_cycles_status ON learning_cycles(status);
CREATE INDEX idx_learning_cycles_start_date ON learning_cycles(start_date);
CREATE UNIQUE INDEX uk_cycles_set_cycle ON learning_cycles(set_id, cycle_number);
```

### 2.7 Review Histories Table

#### 2.7.1 Table Structure
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
    FOREIGN KEY (cycle_id) REFERENCES learning_cycles(cycle_id) ON DELETE CASCADE
);
```

#### 2.7.2 Constraints
- **Primary Key**: `review_id` (UUID)
- **Foreign Keys**: 
  - `set_id` → `sets.set_id` (CASCADE DELETE)
  - `cycle_id` → `learning_cycles.cycle_id` (CASCADE DELETE)
- **Unique**: `(cycle_id, review_number)` - Unique review number within cycle
- **Check Constraints**:
  - Review number range: `review_number >= 1 AND review_number <= 5`
  - Score range: `score IS NULL OR (score >= 0 AND score <= 100)`
  - Skip reason when skipped: `(status = 'skipped' AND skip_reason IS NOT NULL) OR (status = 'completed' AND skip_reason IS NULL)`
  - Review date not future: `review_date <= CURDATE()`
  - Notes length: `notes IS NULL OR LENGTH(notes) <= 500`

#### 2.7.3 Indexes
```sql
CREATE INDEX idx_review_histories_set_id ON review_histories(set_id);
CREATE INDEX idx_review_histories_cycle_id ON review_histories(cycle_id);
CREATE INDEX idx_review_histories_review_date ON review_histories(review_date);
CREATE INDEX idx_review_histories_status ON review_histories(status);
CREATE UNIQUE INDEX uk_cycle_review_number ON review_histories(cycle_id, review_number);
```

### 2.8 Reminder Schedules Table

#### 2.8.1 Table Structure
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
    FOREIGN KEY (set_id) REFERENCES sets(set_id) ON DELETE CASCADE
);
```

#### 2.8.2 Constraints
- **Primary Key**: `reminder_id` (UUID)
- **Foreign Keys**: 
  - `user_id` → `users.user_id` (CASCADE DELETE)
  - `set_id` → `sets.set_id` (CASCADE DELETE)
- **Check Constraints**:
  - Scheduled date not past: `scheduled_date >= CURDATE()`
  - Sent at when sent: `(status = 'sent' AND sent_at IS NOT NULL) OR (status IN ('pending', 'cancelled') AND sent_at IS NULL)`
  - Reminder time valid: `reminder_time BETWEEN '00:00:00' AND '23:59:59'`

#### 2.8.3 Indexes
```sql
CREATE INDEX idx_reminder_schedules_user_id ON reminder_schedules(user_id);
CREATE INDEX idx_reminder_schedules_set_id ON reminder_schedules(set_id);
CREATE INDEX idx_reminder_schedules_scheduled_date ON reminder_schedules(scheduled_date);
CREATE INDEX idx_reminder_schedules_status ON reminder_schedules(status);
CREATE INDEX idx_reminder_schedules_sent_at ON reminder_schedules(sent_at);
```

### 2.9 Activity Logs Table

#### 2.9.1 Table Structure
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
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
```

#### 2.9.2 Constraints
- **Primary Key**: `log_id` (UUID)
- **Foreign Key**: `user_id` → `users.user_id` (CASCADE DELETE)
- **Check Constraints**:
  - Action type valid: `action_type IN ('create', 'update', 'delete', 'login', 'logout', 'view_set', 'view_profile', 'start_cycle', 'complete_review', 'skip_review', 'archive')`
  - Entity type valid: `entity_type IN ('user', 'set', 'cycle', 'review', 'reminder', 'profile', 'settings')`
  - IP address format: `ip_address IS NULL OR ip_address REGEXP '^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$'`
  - JSON valid: `(old_values IS NULL OR JSON_VALID(old_values)) AND (new_values IS NULL OR JSON_VALID(new_values))`

#### 2.9.3 Indexes
```sql
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_action_type ON activity_logs(action_type);
CREATE INDEX idx_activity_logs_entity_type ON activity_logs(entity_type);
CREATE INDEX idx_activity_logs_created_at ON activity_logs(created_at);
CREATE INDEX idx_activity_logs_entity_id ON activity_logs(entity_id);
```

### 2.10 System Configuration Table

#### 2.10.1 Table Structure
```sql
CREATE TABLE system_configuration (
    config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 2.10.2 Constraints
- **Primary Key**: `config_id` (UUID)
- **Unique**: `config_key`
- **Check Constraints**:
  - Config key format: `config_key REGEXP '^[a-z_][a-z0-9_]*$'`
  - Config value not empty: `TRIM(config_value) != ''`
  - Description length: `description IS NULL OR LENGTH(description) <= 500`

#### 2.10.3 Indexes
```sql
CREATE INDEX idx_system_configuration_key ON system_configuration(config_key);
CREATE INDEX idx_system_configuration_active ON system_configuration(is_active);
```

## 3. Data Relationships

### 3.1 Primary Relationships
- **User → Sets**: One-to-Many (1 user có nhiều sets)
- **User → UserProfile**: One-to-One (1 user có 1 profile)
- **User → UserSettings**: One-to-One (1 user có 1 settings)
- **Set → SetItems**: One-to-Many (1 set có nhiều items)
- **Set → LearningCycles**: One-to-Many (1 set có nhiều cycles)
- **LearningCycle → ReviewHistories**: One-to-Many (1 cycle có nhiều reviews)
- **User → ReminderSchedules**: One-to-Many (1 user có nhiều reminders)
- **Set → ReminderSchedules**: One-to-Many (1 set có nhiều reminders)

### 3.2 Foreign Key Constraints
- Tất cả foreign keys sử dụng CASCADE DELETE
- Unique constraints cho các trường cần thiết
- Indexes cho tất cả foreign keys

## 4. Data Validation Rules

### 4.1 Input Validation
- **Email**: Format validation, uniqueness check
- **Password**: Strength requirements, BCrypt hashing
- **UUID**: Format validation, uniqueness
- **JSON**: Structure validation, schema compliance
- **Date/Time**: Range validation, timezone handling

### 4.2 Business Rule Validation
- **Set Creation**: Word count > 0, name not empty
- **Cycle Progression**: Valid state transitions
- **Review Completion**: Score range 0-100
- **Reminder Scheduling**: Future dates only
- **User Status**: Valid status transitions

## 5. Performance Considerations

### 5.1 Indexing Strategy
- **Primary Keys**: Automatic indexes
- **Foreign Keys**: Performance indexes
- **Composite Indexes**: Multi-column queries
- **Partial Indexes**: Soft delete records
- **Covering Indexes**: Query optimization

### 5.2 Query Optimization
- **Pagination**: LIMIT/OFFSET with proper indexes
- **Filtering**: Indexed columns for WHERE clauses
- **Sorting**: Indexed columns for ORDER BY
- **Joins**: Proper foreign key indexes
- **Aggregations**: Optimized GROUP BY queries

### 5.3 Partitioning Strategy
- **Time-based**: Partition by created_at
- **User-based**: Partition by user_id
- **Status-based**: Partition by status
- **Archive**: Move old data to archive tables

## 6. Data Integrity

### 6.1 Referential Integrity
- **Foreign Key Constraints**: CASCADE DELETE
- **Unique Constraints**: Prevent duplicates
- **Check Constraints**: Business rule enforcement
- **Trigger-based**: Complex validation logic

### 6.2 Transaction Management
- **ACID Properties**: Atomicity, Consistency, Isolation, Durability
- **Lock Management**: Row-level locking
- **Deadlock Prevention**: Proper transaction ordering
- **Rollback Handling**: Error recovery

## 7. Security Considerations

### 7.1 Data Protection
- **Password Hashing**: BCrypt with salt
- **PII Encryption**: Sensitive data encryption
- **Access Control**: Role-based permissions
- **Audit Logging**: All data changes logged

### 7.2 Data Isolation
- **User Data**: Complete isolation by user_id
- **Soft Delete**: Logical deletion with deleted_at
- **Data Retention**: Automatic cleanup policies
- **Backup Encryption**: Encrypted backups

## 8. Migration Strategy

### 8.1 Schema Evolution
- **Version Control**: Schema version tracking
- **Backward Compatibility**: Non-breaking changes
- **Data Migration**: Safe data transformation
- **Rollback Plan**: Reversible changes

### 8.2 Deployment Process
1. **Schema Changes**: Apply DDL changes
2. **Data Migration**: Transform existing data
3. **Index Creation**: Add new indexes
4. **Constraint Addition**: Add new constraints
5. **Validation**: Verify data integrity

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Database Architect  
**Stakeholders**: Development Team, DevOps Team
