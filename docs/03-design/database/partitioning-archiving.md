# Partitioning and Archiving Strategy

## 1. Overview

Partitioning và archiving strategy của RepeatWise được thiết kế để tối ưu hóa performance và quản lý storage cho hệ thống Spaced Repetition System. Chiến lược này tập trung vào việc xử lý dữ liệu lớn và tối ưu hóa query performance.

## 2. Partitioning Strategy

### 2.1 Partitioning Approach
- **Range Partitioning**: Partition theo date ranges cho time-series data
- **Hash Partitioning**: Partition theo user_id cho multi-tenant scenarios
- **List Partitioning**: Partition theo status hoặc category
- **Composite Partitioning**: Kết hợp multiple partitioning strategies

### 2.2 Tables for Partitioning

#### 2.2.1 Review Histories Table
```sql
-- Partition by review_date (monthly partitions)
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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) PARTITION BY RANGE (YEAR(review_date) * 100 + MONTH(review_date)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

#### 2.2.2 Activity Logs Table
```sql
-- Partition by created_at (monthly partitions)
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
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

#### 2.2.3 Reminder Schedules Table
```sql
-- Partition by scheduled_date (weekly partitions for active reminders)
CREATE TABLE reminder_schedules (
    reminder_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    set_id UUID NOT NULL,
    scheduled_date DATE NOT NULL,
    reminder_time TIME NOT NULL,
    status ENUM('pending', 'sent', 'cancelled') NOT NULL DEFAULT 'pending',
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) PARTITION BY RANGE (TO_DAYS(scheduled_date)) (
    PARTITION p_week1 VALUES LESS THAN (TO_DAYS('2024-01-08')),
    PARTITION p_week2 VALUES LESS THAN (TO_DAYS('2024-01-15')),
    PARTITION p_week3 VALUES LESS THAN (TO_DAYS('2024-01-22')),
    PARTITION p_week4 VALUES LESS THAN (TO_DAYS('2024-01-29')),
    PARTITION p_week5 VALUES LESS THAN (TO_DAYS('2024-02-05')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

### 2.3 Partition Management

#### 2.3.1 Automatic Partition Creation
```sql
-- Stored procedure for creating new partitions
DELIMITER $$
CREATE PROCEDURE CreateMonthlyPartition(
    IN table_name VARCHAR(100),
    IN partition_date DATE
)
BEGIN
    DECLARE partition_name VARCHAR(20);
    DECLARE next_month DATE;
    DECLARE partition_value INT;
    
    SET partition_name = CONCAT('p', DATE_FORMAT(partition_date, '%Y%m'));
    SET next_month = DATE_ADD(partition_date, INTERVAL 1 MONTH);
    SET partition_value = YEAR(next_month) * 100 + MONTH(next_month);
    
    SET @sql = CONCAT(
        'ALTER TABLE ', table_name, 
        ' ADD PARTITION (PARTITION ', partition_name, 
        ' VALUES LESS THAN (', partition_value, '))'
    );
    
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$
DELIMITER ;
```

#### 2.3.2 Partition Cleanup
```sql
-- Stored procedure for dropping old partitions
DELIMITER $$
CREATE PROCEDURE DropOldPartitions(
    IN table_name VARCHAR(100),
    IN months_to_keep INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE partition_name VARCHAR(20);
    DECLARE partition_date DATE;
    DECLARE cutoff_date DATE;
    
    DECLARE partition_cursor CURSOR FOR
        SELECT partition_name, partition_description
        FROM information_schema.partitions
        WHERE table_name = table_name
        AND partition_name != 'p_future';
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    SET cutoff_date = DATE_SUB(CURDATE(), INTERVAL months_to_keep MONTH);
    
    OPEN partition_cursor;
    
    read_loop: LOOP
        FETCH partition_cursor INTO partition_name, partition_date;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        IF partition_date < cutoff_date THEN
            SET @sql = CONCAT('ALTER TABLE ', table_name, ' DROP PARTITION ', partition_name);
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END LOOP;
    
    CLOSE partition_cursor;
END$$
DELIMITER ;
```

## 3. Archiving Strategy

### 3.1 Archiving Criteria

#### 3.1.1 Review Histories Archiving
- **Archive Age**: 2 years old
- **Archive Criteria**: 
  - review_date < DATE_SUB(CURDATE(), INTERVAL 2 YEAR)
  - status = 'completed' hoặc 'skipped'
- **Archive Location**: review_histories_archive table
- **Archive Frequency**: Monthly

#### 3.1.2 Activity Logs Archiving
- **Archive Age**: 1 year old
- **Archive Criteria**:
  - created_at < DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
  - action_type IN ('login', 'logout', 'view_set', 'view_profile')
- **Archive Location**: activity_logs_archive table
- **Archive Frequency**: Monthly

#### 3.1.3 Reminder Schedules Archiving
- **Archive Age**: 6 months old
- **Archive Criteria**:
  - scheduled_date < DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
  - status IN ('sent', 'cancelled')
- **Archive Location**: reminder_schedules_archive table
- **Archive Frequency**: Weekly

### 3.2 Archive Tables Structure

#### 3.2.1 Review Histories Archive
```sql
CREATE TABLE review_histories_archive (
    review_id UUID PRIMARY KEY,
    set_id UUID NOT NULL,
    cycle_id UUID NOT NULL,
    review_number INTEGER NOT NULL,
    score INTEGER NULL,
    status ENUM('completed', 'skipped') NOT NULL,
    skip_reason ENUM('forgot', 'busy', 'other') NULL,
    review_date DATE NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_review_histories_archive_set_id (set_id),
    INDEX idx_review_histories_archive_review_date (review_date),
    INDEX idx_review_histories_archive_archived_at (archived_at)
) PARTITION BY RANGE (YEAR(review_date) * 100 + MONTH(review_date));
```

#### 3.2.2 Activity Logs Archive
```sql
CREATE TABLE activity_logs_archive (
    log_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    old_values JSON NULL,
    new_values JSON NULL,
    ip_address VARCHAR(45) NULL,
    user_agent TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_activity_logs_archive_user_id (user_id),
    INDEX idx_activity_logs_archive_created_at (created_at),
    INDEX idx_activity_logs_archive_archived_at (archived_at)
) PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at));
```

### 3.3 Archiving Procedures

#### 3.3.1 Review Histories Archiving
```sql
DELIMITER $$
CREATE PROCEDURE ArchiveReviewHistories()
BEGIN
    DECLARE archive_date DATE;
    SET archive_date = DATE_SUB(CURDATE(), INTERVAL 2 YEAR);
    
    -- Insert into archive table
    INSERT INTO review_histories_archive
    SELECT 
        review_id, set_id, cycle_id, review_number, score, status, 
        skip_reason, review_date, notes, created_at, updated_at, NOW()
    FROM review_histories
    WHERE review_date < archive_date
    AND status IN ('completed', 'skipped');
    
    -- Delete from main table
    DELETE FROM review_histories
    WHERE review_date < archive_date
    AND status IN ('completed', 'skipped');
    
    -- Log archiving activity
    INSERT INTO activity_logs (user_id, action_type, entity_type, entity_id, old_values, new_values)
    VALUES (NULL, 'archive', 'review_histories', NULL, 
            JSON_OBJECT('archived_count', ROW_COUNT()), 
            JSON_OBJECT('archive_date', archive_date));
END$$
DELIMITER ;
```

#### 3.3.2 Activity Logs Archiving
```sql
DELIMITER $$
CREATE PROCEDURE ArchiveActivityLogs()
BEGIN
    DECLARE archive_date DATE;
    SET archive_date = DATE_SUB(CURDATE(), INTERVAL 1 YEAR);
    
    -- Insert into archive table
    INSERT INTO activity_logs_archive
    SELECT 
        log_id, user_id, action_type, entity_type, entity_id,
        old_values, new_values, ip_address, user_agent, created_at, NOW()
    FROM activity_logs
    WHERE created_at < archive_date
    AND action_type IN ('login', 'logout', 'view_set', 'view_profile');
    
    -- Delete from main table
    DELETE FROM activity_logs
    WHERE created_at < archive_date
    AND action_type IN ('login', 'logout', 'view_set', 'view_profile');
    
    -- Log archiving activity
    INSERT INTO activity_logs (user_id, action_type, entity_type, entity_id, old_values, new_values)
    VALUES (NULL, 'archive', 'activity_logs', NULL, 
            JSON_OBJECT('archived_count', ROW_COUNT()), 
            JSON_OBJECT('archive_date', archive_date));
END$$
DELIMITER ;
```

## 4. Data Lifecycle Management

### 4.1 Data Retention Policy

#### 4.1.1 Active Data (0-6 months)
- **Location**: Main tables
- **Access**: Full access with optimal performance
- **Backup**: Daily incremental + weekly full
- **Indexing**: Full indexing for optimal query performance

#### 4.1.2 Recent Archive (6 months - 2 years)
- **Location**: Archive tables
- **Access**: Read-only access with moderate performance
- **Backup**: Weekly incremental + monthly full
- **Indexing**: Basic indexing for common queries

#### 4.1.3 Long-term Archive (2+ years)
- **Location**: Compressed archive tables or external storage
- **Access**: Read-only access with slower performance
- **Backup**: Monthly full backup
- **Indexing**: Minimal indexing for essential queries

### 4.2 Data Compression

#### 4.2.1 Archive Table Compression
```sql
-- Compress archive tables to save storage
ALTER TABLE review_histories_archive ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;
ALTER TABLE activity_logs_archive ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;
```

#### 4.2.2 Partition Compression
```sql
-- Compress old partitions
ALTER TABLE review_histories PARTITION p202301 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;
ALTER TABLE activity_logs PARTITION p202301 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=8;
```

## 5. Performance Optimization

### 5.1 Query Optimization for Partitioned Tables

#### 5.1.1 Partition Pruning
```sql
-- Optimize queries to use partition pruning
SELECT COUNT(*) FROM review_histories 
WHERE review_date >= '2024-01-01' AND review_date < '2024-02-01';

-- Use partition-specific queries for better performance
SELECT * FROM review_histories PARTITION(p202401) 
WHERE review_date >= '2024-01-01' AND review_date < '2024-02-01';
```

#### 5.1.2 Archive Query Optimization
```sql
-- Optimize archive queries with proper indexing
SELECT COUNT(*) FROM review_histories_archive 
WHERE review_date >= '2022-01-01' AND review_date < '2023-01-01'
AND archived_at >= '2024-01-01';
```

### 5.2 Storage Optimization

#### 5.2.1 Storage Monitoring
```sql
-- Monitor partition sizes
SELECT 
    partition_name,
    partition_description,
    table_rows,
    data_length,
    index_length
FROM information_schema.partitions
WHERE table_name = 'review_histories'
ORDER BY partition_ordinal_position;
```

#### 5.2.2 Storage Cleanup
```sql
-- Clean up old compressed data
OPTIMIZE TABLE review_histories_archive;
OPTIMIZE TABLE activity_logs_archive;
```

## 6. Monitoring and Maintenance

### 6.1 Automated Maintenance Jobs

#### 6.1.1 Monthly Partition Management
```sql
-- Event for monthly partition creation
CREATE EVENT monthly_partition_creation
ON SCHEDULE EVERY 1 MONTH
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    CALL CreateMonthlyPartition('review_histories', DATE_ADD(CURDATE(), INTERVAL 1 MONTH));
    CALL CreateMonthlyPartition('activity_logs', DATE_ADD(CURDATE(), INTERVAL 1 MONTH));
    CALL DropOldPartitions('review_histories', 24);
    CALL DropOldPartitions('activity_logs', 12);
END;
```

#### 6.1.2 Monthly Archiving
```sql
-- Event for monthly archiving
CREATE EVENT monthly_archiving
ON SCHEDULE EVERY 1 MONTH
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    CALL ArchiveReviewHistories();
    CALL ArchiveActivityLogs();
END;
```

### 6.2 Performance Monitoring

#### 6.2.1 Partition Performance Metrics
```sql
-- Monitor partition performance
SELECT 
    partition_name,
    table_rows,
    avg_row_length,
    data_length,
    max_data_length,
    index_length,
    data_free
FROM information_schema.partitions
WHERE table_name IN ('review_histories', 'activity_logs')
ORDER BY table_name, partition_ordinal_position;
```

#### 6.2.2 Archive Performance Metrics
```sql
-- Monitor archive table performance
SELECT 
    table_name,
    table_rows,
    data_length,
    index_length,
    (data_length + index_length) as total_size
FROM information_schema.tables
WHERE table_name LIKE '%_archive'
ORDER BY total_size DESC;
```

## 7. Disaster Recovery

### 7.1 Archive Backup Strategy
- **Daily**: Incremental backup of active data
- **Weekly**: Full backup of active data + archive data
- **Monthly**: Full backup of all data including compressed archives
- **Yearly**: Long-term backup to external storage

### 7.2 Recovery Procedures
```sql
-- Restore archive data if needed
INSERT INTO review_histories
SELECT review_id, set_id, cycle_id, review_number, score, status,
       skip_reason, review_date, notes, created_at, updated_at
FROM review_histories_archive
WHERE archived_at >= '2024-01-01' AND archived_at < '2024-02-01';
```

## 8. Expected Benefits

### 8.1 Performance Improvements
- **Query Performance**: 50-80% improvement for date-range queries
- **Storage Efficiency**: 30-50% reduction in storage costs
- **Backup Performance**: 40-60% faster backup operations
- **Maintenance Windows**: 70% reduction in maintenance time

### 8.2 Scalability Benefits
- **Horizontal Scaling**: Easy to add new partitions
- **Storage Management**: Automated storage optimization
- **Data Lifecycle**: Automated data lifecycle management
- **Compliance**: Better data retention compliance 
