# Database Migration Plan

## 1. Overview

Database migration plan của RepeatWise được thiết kế để đảm bảo việc triển khai và cập nhật database một cách an toàn và hiệu quả. Plan này bao gồm các phases từ development đến production với rollback strategies.

## 2. Migration Strategy

### 2.1 Migration Approach
- **Versioned Migrations**: Mỗi thay đổi database được versioned
- **Forward-Only**: Migrations chỉ chạy forward, không rollback
- **Idempotent**: Migrations có thể chạy nhiều lần an toàn
- **Transactional**: Mỗi migration chạy trong transaction

### 2.2 Migration Tools
- **Flyway**: Database migration tool chính
- **Liquibase**: Alternative cho complex migrations
- **Custom Scripts**: Cho specific database operations

## 3. Migration Phases

### 3.1 Phase 1: Initial Schema Setup

#### V1__Create_Initial_Schema.sql
```sql
-- Create database
CREATE DATABASE IF NOT EXISTS repeatwise CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use database
USE repeatwise;

-- Create users table
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

-- Create indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
```

#### V2__Create_User_Profile_Tables.sql
```sql
-- Create user_profiles table
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    avatar_url VARCHAR(500) NULL,
    bio TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_profiles_user_id (user_id)
);

-- Create user_settings table
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
    INDEX idx_user_settings_user_id (user_id)
);
```

### 3.2 Phase 2: Learning Management Tables

#### V3__Create_Learning_Tables.sql
```sql
-- Create sets table
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
    INDEX idx_sets_last_reviewed_at (last_reviewed_at)
);

-- Create set_items table
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
    INDEX idx_set_items_order (item_order)
);
```

#### V4__Create_Cycle_Management_Tables.sql
```sql
-- Create learning_cycles table
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
    INDEX idx_learning_cycles_start_date (start_date)
);

-- Create review_histories table
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
    INDEX idx_review_histories_status (status)
);
```

### 3.3 Phase 3: Scheduling and Notification Tables

#### V5__Create_Scheduling_Tables.sql
```sql
-- Create reminder_schedules table
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
    INDEX idx_reminder_schedules_status (status)
);
```

### 3.4 Phase 4: Audit and Configuration Tables

#### V6__Create_Audit_Tables.sql
```sql
-- Create activity_logs table
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
    INDEX idx_activity_logs_created_at (created_at)
);

-- Create system_configuration table
CREATE TABLE system_configuration (
    config_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT NOT NULL,
    description TEXT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_system_configuration_key (config_key),
    INDEX idx_system_configuration_active (is_active)
);
```

### 3.5 Phase 5: Performance Optimization

#### V7__Create_Performance_Indexes.sql
```sql
-- Composite indexes for better performance
CREATE INDEX idx_sets_user_status ON sets(user_id, status);
CREATE INDEX idx_sets_user_category ON sets(user_id, category);
CREATE INDEX idx_sets_user_deleted_at ON sets(user_id, deleted_at);

CREATE INDEX idx_learning_cycles_set_status ON learning_cycles(set_id, status);
CREATE INDEX idx_learning_cycles_set_avg_score ON learning_cycles(set_id, average_score);

CREATE INDEX idx_review_histories_set_date ON review_histories(set_id, review_date);
CREATE INDEX idx_review_histories_cycle_review ON review_histories(cycle_id, review_number);

CREATE INDEX idx_reminder_schedules_user_date ON reminder_schedules(user_id, scheduled_date);
CREATE INDEX idx_reminder_schedules_date_status ON reminder_schedules(scheduled_date, status);

CREATE INDEX idx_activity_logs_user_date ON activity_logs(user_id, created_at);
CREATE INDEX idx_activity_logs_type_date ON activity_logs(action_type, created_at);
```

#### V8__Create_Covering_Indexes.sql
```sql
-- Covering indexes for common queries
CREATE INDEX idx_users_profile_data ON users(user_id, email, full_name, status, created_at);
CREATE INDEX idx_sets_summary_data ON sets(set_id, user_id, name, category, status, word_count, average_score);
CREATE INDEX idx_review_histories_summary ON review_histories(review_id, set_id, review_date, score, status);
```

## 4. Data Migration Scripts

### 4.1 Initial Data Setup

#### V9__Insert_Initial_Data.sql
```sql
-- Insert default system configuration
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

-- Insert sample user for testing (password: test123)
INSERT INTO users (email, password_hash, full_name, preferred_language, timezone, default_reminder_time) VALUES
('test@repeatwise.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test User', 'VI', 'Asia/Ho_Chi_Minh', '09:00');
```

### 4.2 Sample Data for Development

#### V10__Insert_Sample_Data.sql
```sql
-- Insert sample sets for test user
INSERT INTO sets (user_id, name, description, category, word_count, status) VALUES
((SELECT user_id FROM users WHERE email = 'test@repeatwise.com'), 'Basic Vocabulary', 'Basic English vocabulary for beginners', 'vocabulary', 50, 'not_started'),
((SELECT user_id FROM users WHERE email = 'test@repeatwise.com'), 'Grammar Rules', 'Essential grammar rules', 'grammar', 30, 'not_started'),
((SELECT user_id FROM users WHERE email = 'test@repeatwise.com'), 'Mixed Learning', 'Combined vocabulary and grammar', 'mixed', 40, 'not_started');

-- Insert sample set items
INSERT INTO set_items (set_id, front_content, back_content, item_order) VALUES
((SELECT set_id FROM sets WHERE name = 'Basic Vocabulary' LIMIT 1), 'Hello', 'Xin chào', 1),
((SELECT set_id FROM sets WHERE name = 'Basic Vocabulary' LIMIT 1), 'Goodbye', 'Tạm biệt', 2),
((SELECT set_id FROM sets WHERE name = 'Basic Vocabulary' LIMIT 1), 'Thank you', 'Cảm ơn', 3);
```

## 5. Migration Execution Plan

### 5.1 Development Environment
```bash
# 1. Setup Flyway
flyway -url=jdbc:mysql://localhost:3306/repeatwise_dev -user=dev_user -password=dev_pass migrate

# 2. Verify migration
flyway -url=jdbc:mysql://localhost:3306/repeatwise_dev -user=dev_user -password=dev_pass info

# 3. Test with sample data
flyway -url=jdbc:mysql://localhost:3306/repeatwise_dev -user=dev_user -password=dev_pass migrate
```

### 5.2 Staging Environment
```bash
# 1. Backup existing data
mysqldump -u staging_user -p repeatwise_staging > backup_staging_$(date +%Y%m%d_%H%M%S).sql

# 2. Run migration
flyway -url=jdbc:mysql://staging-db:3306/repeatwise_staging -user=staging_user -password=staging_pass migrate

# 3. Verify data integrity
./scripts/verify_migration.sh staging
```

### 5.3 Production Environment
```bash
# 1. Pre-migration backup
mysqldump -u prod_user -p repeatwise_prod > backup_prod_$(date +%Y%m%d_%H%M%S).sql

# 2. Maintenance window notification
./scripts/notify_maintenance.sh

# 3. Run migration
flyway -url=jdbc:mysql://prod-db:3306/repeatwise_prod -user=prod_user -password=prod_pass migrate

# 4. Post-migration verification
./scripts/verify_migration.sh production

# 5. Rollback plan (if needed)
./scripts/rollback_migration.sh
```

## 6. Rollback Strategy

### 6.1 Rollback Triggers
- Data integrity violations
- Performance degradation > 20%
- Application errors > 5%
- User complaints > threshold

### 6.2 Rollback Procedures
```bash
# 1. Stop application
./scripts/stop_application.sh

# 2. Restore from backup
mysql -u prod_user -p repeatwise_prod < backup_prod_$(date +%Y%m%d_%H%M%S).sql

# 3. Verify rollback
./scripts/verify_rollback.sh

# 4. Restart application
./scripts/start_application.sh
```

## 7. Migration Monitoring

### 7.1 Pre-Migration Checks
- Database size and available space
- Current performance metrics
- Application health status
- User activity patterns

### 7.2 During Migration Monitoring
- Migration progress tracking
- Database performance metrics
- Error rate monitoring
- Resource utilization

### 7.3 Post-Migration Verification
- Data integrity checks
- Performance comparison
- Application functionality tests
- User acceptance testing

## 8. Migration Schedule

### 8.1 Development Phase
- **Week 1-2**: Schema design and initial migrations
- **Week 3**: Performance optimization migrations
- **Week 4**: Testing and refinement

### 8.2 Staging Phase
- **Week 5**: Staging deployment and testing
- **Week 6**: Performance testing and optimization
- **Week 7**: User acceptance testing

### 8.3 Production Phase
- **Week 8**: Production deployment
- **Week 9**: Monitoring and optimization
- **Week 10**: Documentation and handover

## 9. Risk Mitigation

### 9.1 Technical Risks
- **Data Loss**: Multiple backup strategies
- **Performance Issues**: Pre-migration performance testing
- **Compatibility Issues**: Comprehensive testing in staging

### 9.2 Business Risks
- **Downtime**: Maintenance window planning
- **User Impact**: Gradual rollout strategy
- **Data Corruption**: Validation scripts

### 9.3 Operational Risks
- **Resource Constraints**: Resource planning and monitoring
- **Skill Gaps**: Training and documentation
- **Communication Issues**: Clear communication plan 
