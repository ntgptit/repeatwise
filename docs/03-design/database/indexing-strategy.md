# Indexing Strategy

## 1. Overview

Indexing strategy của RepeatWise được thiết kế để tối ưu hóa performance cho các query patterns phổ biến trong Spaced Repetition System. Chiến lược này tập trung vào việc tối ưu hóa các operations chính: user authentication, set management, review scheduling, và analytics.

## 2. Primary Indexes

### 2.1 Primary Key Indexes
Tất cả primary keys đều có index tự động:

```sql
-- Tự động tạo bởi database engine
PRIMARY KEY (user_id) -- users table
PRIMARY KEY (set_id) -- sets table
PRIMARY KEY (cycle_id) -- learning_cycles table
PRIMARY KEY (review_id) -- review_histories table
PRIMARY KEY (reminder_id) -- reminder_schedules table
```

### 2.2 Unique Indexes
```sql
-- Email uniqueness
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- Configuration key uniqueness
CREATE UNIQUE INDEX idx_system_config_key ON system_configuration(config_key);

-- Set-cycle uniqueness
CREATE UNIQUE INDEX uk_cycles_set_cycle ON learning_cycles(set_id, cycle_number);
```

## 3. Foreign Key Indexes

### 3.1 User-Related Indexes
```sql
-- User profile relationship
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- User settings relationship
CREATE INDEX idx_user_settings_user_id ON user_settings(user_id);

-- User sets relationship
CREATE INDEX idx_sets_user_id ON sets(user_id);

-- User reminders relationship
CREATE INDEX idx_reminder_schedules_user_id ON reminder_schedules(user_id);

-- User activity logs relationship
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
```

### 3.2 Set-Related Indexes
```sql
-- Set items relationship
CREATE INDEX idx_set_items_set_id ON set_items(set_id);

-- Learning cycles relationship
CREATE INDEX idx_learning_cycles_set_id ON learning_cycles(set_id);

-- Review histories relationship
CREATE INDEX idx_review_histories_set_id ON review_histories(set_id);
CREATE INDEX idx_review_histories_cycle_id ON review_histories(cycle_id);

-- Reminder schedules relationship
CREATE INDEX idx_reminder_schedules_set_id ON reminder_schedules(set_id);
```

## 4. Query-Specific Indexes

### 4.1 Authentication and User Management
```sql
-- User login queries
CREATE INDEX idx_users_email_status ON users(email, status);

-- Active users queries
CREATE INDEX idx_users_status_deleted_at ON users(status, deleted_at);

-- User search by name
CREATE INDEX idx_users_full_name ON users(full_name);
```

### 4.2 Set Management
```sql
-- Set listing by user and status
CREATE INDEX idx_sets_user_status ON sets(user_id, status);

-- Set filtering by category
CREATE INDEX idx_sets_user_category ON sets(user_id, category);

-- Set search by name
CREATE INDEX idx_sets_user_name ON sets(user_id, name);

-- Active sets (not deleted)
CREATE INDEX idx_sets_user_deleted_at ON sets(user_id, deleted_at);
```

### 4.3 Learning Cycle Management
```sql
-- Current active cycles
CREATE INDEX idx_learning_cycles_set_status ON learning_cycles(set_id, status);

-- Cycle by date range
CREATE INDEX idx_learning_cycles_start_date ON learning_cycles(start_date);

-- Cycle performance analysis
CREATE INDEX idx_learning_cycles_set_avg_score ON learning_cycles(set_id, average_score);
```

### 4.4 Review Management
```sql
-- Reviews by date
CREATE INDEX idx_review_histories_review_date ON review_histories(review_date);

-- Reviews by status
CREATE INDEX idx_review_histories_status ON review_histories(status);

-- Reviews by set and date
CREATE INDEX idx_review_histories_set_date ON review_histories(set_id, review_date);

-- Reviews by cycle and number
CREATE INDEX idx_review_histories_cycle_review ON review_histories(cycle_id, review_number);
```

### 4.5 Reminder Scheduling
```sql
-- Pending reminders by date
CREATE INDEX idx_reminder_schedules_date_status ON reminder_schedules(scheduled_date, status);

-- User reminders by date
CREATE INDEX idx_reminder_schedules_user_date ON reminder_schedules(user_id, scheduled_date);

-- Reminders by time
CREATE INDEX idx_reminder_schedules_time ON reminder_schedules(reminder_time);
```

### 4.6 Analytics and Reporting
```sql
-- Activity logs by type and date
CREATE INDEX idx_activity_logs_type_date ON activity_logs(action_type, created_at);

-- Activity logs by user and date
CREATE INDEX idx_activity_logs_user_date ON activity_logs(user_id, created_at);

-- Performance analysis by date range
CREATE INDEX idx_review_histories_date_score ON review_histories(review_date, score);
```

## 5. Composite Indexes

### 5.1 Multi-Column Indexes for Complex Queries
```sql
-- Set management with multiple filters
CREATE INDEX idx_sets_user_status_category ON sets(user_id, status, category);

-- Review scheduling with priority
CREATE INDEX idx_reminder_schedules_user_status_date ON reminder_schedules(user_id, status, scheduled_date);

-- Learning progress analysis
CREATE INDEX idx_learning_cycles_set_status_score ON learning_cycles(set_id, status, average_score);

-- User activity analysis
CREATE INDEX idx_activity_logs_user_type_date ON activity_logs(user_id, action_type, created_at);
```

### 5.2 Covering Indexes
```sql
-- User profile data (covering index)
CREATE INDEX idx_users_profile_data ON users(user_id, email, full_name, status, created_at);

-- Set summary data (covering index)
CREATE INDEX idx_sets_summary_data ON sets(set_id, user_id, name, category, status, word_count, average_score);

-- Review summary data (covering index)
CREATE INDEX idx_review_histories_summary ON review_histories(review_id, set_id, review_date, score, status);
```

## 6. Partial Indexes

### 6.1 Soft Delete Indexes
```sql
-- Active users only
CREATE INDEX idx_users_active ON users(user_id, email, status) WHERE deleted_at IS NULL;

-- Active sets only
CREATE INDEX idx_sets_active ON sets(set_id, user_id, name, status) WHERE deleted_at IS NULL;

-- Pending reminders only
CREATE INDEX idx_reminder_schedules_pending ON reminder_schedules(reminder_id, user_id, scheduled_date) WHERE status = 'pending';
```

### 6.2 Status-Specific Indexes
```sql
-- Active learning cycles
CREATE INDEX idx_learning_cycles_active ON learning_cycles(cycle_id, set_id, cycle_number) WHERE status = 'active';

-- Completed reviews
CREATE INDEX idx_review_histories_completed ON review_histories(review_id, set_id, score) WHERE status = 'completed';
```

## 7. Performance Monitoring Indexes

### 7.1 Query Performance Tracking
```sql
-- Slow query identification
CREATE INDEX idx_activity_logs_performance ON activity_logs(created_at, action_type, entity_type);

-- User activity patterns
CREATE INDEX idx_activity_logs_user_patterns ON activity_logs(user_id, action_type, created_at);
```

## 8. Index Maintenance Strategy

### 8.1 Index Statistics
```sql
-- Regular statistics update
ANALYZE TABLE users, sets, learning_cycles, review_histories, reminder_schedules;
```

### 8.2 Index Fragmentation
```sql
-- Regular index optimization
OPTIMIZE TABLE users, sets, learning_cycles, review_histories, reminder_schedules;
```

### 8.3 Index Usage Monitoring
```sql
-- Monitor index usage
SELECT 
    table_name,
    index_name,
    cardinality,
    sub_part,
    packed,
    null,
    index_type,
    comment
FROM information_schema.statistics 
WHERE table_schema = 'repeatwise';
```

## 9. Index Optimization Guidelines

### 9.1 Index Selection Criteria
- **Selectivity**: Index columns với high selectivity (nhiều unique values)
- **Query Patterns**: Index theo các query patterns phổ biến
- **Join Operations**: Index foreign keys cho JOIN operations
- **Sorting**: Index columns được sử dụng trong ORDER BY
- **Filtering**: Index columns được sử dụng trong WHERE clause

### 9.2 Index Size Considerations
- **Column Order**: Đặt columns với high selectivity trước
- **Column Types**: Ưu tiên smaller data types
- **Covering Indexes**: Include frequently accessed columns
- **Partial Indexes**: Chỉ index subset của data khi cần thiết

### 9.3 Index Maintenance
- **Regular Monitoring**: Kiểm tra index usage và performance
- **Statistics Update**: Cập nhật statistics định kỳ
- **Fragmentation**: Optimize indexes khi cần thiết
- **Storage**: Monitor index storage usage

## 10. Expected Performance Improvements

### 10.1 Query Performance
- **User Authentication**: < 10ms với email index
- **Set Listing**: < 50ms với composite indexes
- **Review Scheduling**: < 100ms với date-based indexes
- **Analytics Queries**: < 500ms với covering indexes

### 10.2 Scalability Considerations
- **Index Partitioning**: Partition indexes theo date cho large tables
- **Read Replicas**: Sử dụng read replicas cho analytics queries
- **Caching**: Cache frequently accessed data
- **Connection Pooling**: Optimize database connections 
