# Database Schema

## 1. Overview

Database schema của RepeatWise được thiết kế theo chuẩn PostgreSQL với các bảng chính quản lý user, set học tập, chu kỳ học, lịch sử ôn tập và reminder. Schema được tối ưu cho hiệu suất và đảm bảo tính toàn vẹn dữ liệu.

## 2. Database Configuration

### 2.1 PostgreSQL Version
```sql
-- Minimum version required
SELECT version(); -- PostgreSQL 14.0 or higher
```

### 2.2 Database Creation
```sql
-- Create database
CREATE DATABASE repeatwise_prod
    WITH 
    OWNER = repeatwise_prod
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Create development database
CREATE DATABASE repeatwise_dev
    WITH 
    OWNER = repeatwise_dev
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
```

### 2.3 Extensions
```sql
-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "btree_gin";
```

## 3. Core Tables

### 3.1 Users Table
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    preferred_language VARCHAR(2) NOT NULL DEFAULT 'VI' CHECK (preferred_language IN ('VI', 'EN')),
    timezone VARCHAR(50) NOT NULL DEFAULT 'Asia/Ho_Chi_Minh',
    default_reminder_time TIME NOT NULL DEFAULT '20:00:00',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE NULL
);

-- Indexes
CREATE INDEX idx_users_email ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_status ON users(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_created_at ON users(created_at) WHERE deleted_at IS NULL;

-- Trigger for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.2 User Profiles Table
```sql
CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    avatar_url VARCHAR(500) NULL,
    bio TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

-- Indexes
CREATE INDEX idx_user_profiles_user_id ON user_profiles(user_id);

-- Trigger for updated_at
CREATE TRIGGER update_user_profiles_updated_at 
    BEFORE UPDATE ON user_profiles 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.3 User Settings Table
```sql
CREATE TABLE user_settings (
    settings_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    notification_email BOOLEAN NOT NULL DEFAULT TRUE,
    notification_push BOOLEAN NOT NULL DEFAULT TRUE,
    notification_sound BOOLEAN NOT NULL DEFAULT TRUE,
    privacy_share_progress BOOLEAN NOT NULL DEFAULT FALSE,
    privacy_share_statistics BOOLEAN NOT NULL DEFAULT FALSE,
    learning_auto_start BOOLEAN NOT NULL DEFAULT TRUE,
    learning_reminder_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id)
);

-- Indexes
CREATE INDEX idx_user_settings_user_id ON user_settings(user_id);

-- Trigger for updated_at
CREATE TRIGGER update_user_settings_updated_at 
    BEFORE UPDATE ON user_settings 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.4 Sets Table
```sql
CREATE TABLE sets (
    set_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'VOCABULARY' CHECK (category IN ('VOCABULARY', 'GRAMMAR', 'MIXED', 'OTHER')),
    word_count INTEGER NOT NULL CHECK (word_count > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED' CHECK (status IN ('NOT_STARTED', 'LEARNING', 'REVIEWING', 'MASTERED')),
    current_cycle INTEGER NOT NULL DEFAULT 1,
    average_score DECIMAL(5,2) NULL CHECK (average_score >= 0 AND average_score <= 100),
    total_cycles INTEGER NOT NULL DEFAULT 0,
    total_reviews INTEGER NOT NULL DEFAULT 0,
    last_review_date TIMESTAMP WITH TIME ZONE NULL,
    next_review_date TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE NULL
);

-- Indexes
CREATE INDEX idx_sets_user_id ON sets(user_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_sets_status ON sets(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_sets_category ON sets(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_sets_next_review_date ON sets(next_review_date) WHERE deleted_at IS NULL;
CREATE INDEX idx_sets_created_at ON sets(created_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_sets_user_status ON sets(user_id, status) WHERE deleted_at IS NULL;

-- Trigger for updated_at
CREATE TRIGGER update_sets_updated_at 
    BEFORE UPDATE ON sets 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.5 Review Histories Table
```sql
CREATE TABLE review_histories (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL REFERENCES sets(set_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    cycle_number INTEGER NOT NULL CHECK (cycle_number > 0),
    review_number INTEGER NOT NULL CHECK (review_number >= 1 AND review_number <= 5),
    score INTEGER NULL CHECK (score >= 0 AND score <= 100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'SKIPPED')),
    skip_reason VARCHAR(20) NULL CHECK (skip_reason IN ('FORGOT', 'BUSY', 'OTHER')),
    note TEXT NULL,
    review_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_review_histories_set_id ON review_histories(set_id);
CREATE INDEX idx_review_histories_user_id ON review_histories(user_id);
CREATE INDEX idx_review_histories_cycle_number ON review_histories(cycle_number);
CREATE INDEX idx_review_histories_status ON review_histories(status);
CREATE INDEX idx_review_histories_review_date ON review_histories(review_date);
CREATE INDEX idx_review_histories_set_cycle ON review_histories(set_id, cycle_number);
CREATE INDEX idx_review_histories_user_date ON review_histories(user_id, review_date);

-- Trigger for updated_at
CREATE TRIGGER update_review_histories_updated_at 
    BEFORE UPDATE ON review_histories 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.6 Reminder Schedules Table
```sql
CREATE TABLE reminder_schedules (
    reminder_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL REFERENCES sets(set_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    cycle_number INTEGER NOT NULL CHECK (cycle_number > 0),
    review_number INTEGER NOT NULL CHECK (review_number >= 1 AND review_number <= 5),
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'SENT', 'COMPLETED', 'SKIPPED', 'CANCELLED')),
    reschedule_count INTEGER NOT NULL DEFAULT 0 CHECK (reschedule_count >= 0),
    reschedule_reason VARCHAR(20) NULL CHECK (reschedule_reason IN ('BUSY', 'FORGOT', 'OTHER')),
    sent_at TIMESTAMP WITH TIME ZONE NULL,
    completed_at TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_reminder_schedules_set_id ON reminder_schedules(set_id);
CREATE INDEX idx_reminder_schedules_user_id ON reminder_schedules(user_id);
CREATE INDEX idx_reminder_schedules_scheduled_date ON reminder_schedules(scheduled_date);
CREATE INDEX idx_reminder_schedules_status ON reminder_schedules(status);
CREATE INDEX idx_reminder_schedules_user_date ON reminder_schedules(user_id, scheduled_date);
CREATE INDEX idx_reminder_schedules_pending ON reminder_schedules(scheduled_date, status) WHERE status = 'PENDING';

-- Trigger for updated_at
CREATE TRIGGER update_reminder_schedules_updated_at 
    BEFORE UPDATE ON reminder_schedules 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

### 3.7 Learning Cycles Table
```sql
CREATE TABLE learning_cycles (
    cycle_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    set_id UUID NOT NULL REFERENCES sets(set_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    cycle_number INTEGER NOT NULL CHECK (cycle_number > 0),
    start_date DATE NOT NULL,
    end_date DATE NULL,
    average_score DECIMAL(5,2) NULL CHECK (average_score >= 0 AND average_score <= 100),
    completed_reviews INTEGER NOT NULL DEFAULT 0 CHECK (completed_reviews >= 0 AND completed_reviews <= 5),
    skipped_reviews INTEGER NOT NULL DEFAULT 0 CHECK (skipped_reviews >= 0 AND skipped_reviews <= 5),
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS' CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED')),
    next_cycle_delay INTEGER NULL CHECK (next_cycle_delay >= 7 AND next_cycle_delay <= 90),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(set_id, cycle_number)
);

-- Indexes
CREATE INDEX idx_learning_cycles_set_id ON learning_cycles(set_id);
CREATE INDEX idx_learning_cycles_user_id ON learning_cycles(user_id);
CREATE INDEX idx_learning_cycles_status ON learning_cycles(status);
CREATE INDEX idx_learning_cycles_start_date ON learning_cycles(start_date);
CREATE INDEX idx_learning_cycles_set_cycle ON learning_cycles(set_id, cycle_number);

-- Trigger for updated_at
CREATE TRIGGER update_learning_cycles_updated_at 
    BEFORE UPDATE ON learning_cycles 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
```

## 4. Reference Tables

### 4.1 Set Categories Table
```sql
CREATE TABLE set_categories (
    category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(20) UNIQUE NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    description TEXT NULL,
    icon VARCHAR(50) NULL,
    color VARCHAR(7) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default categories
INSERT INTO set_categories (name, display_name, description, icon, color, sort_order) VALUES
('VOCABULARY', 'Từ vựng', 'Học từ vựng mới', 'book', '#4CAF50', 1),
('GRAMMAR', 'Ngữ pháp', 'Học ngữ pháp', 'school', '#2196F3', 2),
('MIXED', 'Tổng hợp', 'Kết hợp từ vựng và ngữ pháp', 'library_books', '#FF9800', 3),
('OTHER', 'Khác', 'Các chủ đề khác', 'category', '#9C27B0', 4);
```

### 4.2 Skip Reasons Table
```sql
CREATE TABLE skip_reasons (
    reason_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(20) UNIQUE NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    description TEXT NULL,
    requires_note BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default skip reasons
INSERT INTO skip_reasons (code, display_name, description, requires_note, sort_order) VALUES
('FORGOT', 'Quên', 'Quên ôn tập', FALSE, 1),
('BUSY', 'Bận', 'Quá bận rộn', FALSE, 2),
('OTHER', 'Khác', 'Lý do khác', TRUE, 3);
```

## 5. Views

### 5.1 Set Progress View
```sql
CREATE VIEW set_progress_view AS
SELECT 
    s.set_id,
    s.user_id,
    s.name,
    s.category,
    s.status,
    s.current_cycle,
    s.average_score,
    s.total_cycles,
    s.total_reviews,
    s.last_review_date,
    s.next_review_date,
    COUNT(rh.review_id) as completed_reviews,
    COUNT(CASE WHEN rh.status = 'SKIPPED' THEN 1 END) as skipped_reviews,
    AVG(rh.score) as recent_average_score,
    MAX(rh.review_date) as last_activity_date
FROM sets s
LEFT JOIN review_histories rh ON s.set_id = rh.set_id 
    AND rh.cycle_number = s.current_cycle
WHERE s.deleted_at IS NULL
GROUP BY s.set_id, s.user_id, s.name, s.category, s.status, s.current_cycle, 
         s.average_score, s.total_cycles, s.total_reviews, s.last_review_date, s.next_review_date;
```

### 5.2 User Statistics View
```sql
CREATE VIEW user_statistics_view AS
SELECT 
    u.user_id,
    u.email,
    u.full_name,
    COUNT(s.set_id) as total_sets,
    COUNT(CASE WHEN s.status = 'ACTIVE' THEN 1 END) as active_sets,
    COUNT(CASE WHEN s.status = 'MASTERED' THEN 1 END) as mastered_sets,
    COUNT(rh.review_id) as total_reviews,
    AVG(rh.score) as average_score,
    COUNT(CASE WHEN rh.status = 'SKIPPED' THEN 1 END) as skipped_reviews,
    MAX(rh.review_date) as last_review_date
FROM users u
LEFT JOIN sets s ON u.user_id = s.user_id AND s.deleted_at IS NULL
LEFT JOIN review_histories rh ON u.user_id = rh.user_id
WHERE u.deleted_at IS NULL
GROUP BY u.user_id, u.email, u.full_name;
```

### 5.3 Daily Reminders View
```sql
CREATE VIEW daily_reminders_view AS
SELECT 
    rs.reminder_id,
    rs.set_id,
    rs.user_id,
    s.name as set_name,
    s.category,
    rs.cycle_number,
    rs.review_number,
    rs.scheduled_date,
    rs.scheduled_time,
    rs.status,
    rs.reschedule_count,
    u.default_reminder_time,
    u.timezone,
    CASE 
        WHEN rs.scheduled_date < CURRENT_DATE THEN 'OVERDUE'
        WHEN rs.scheduled_date = CURRENT_DATE THEN 'TODAY'
        ELSE 'FUTURE'
    END as priority
FROM reminder_schedules rs
JOIN sets s ON rs.set_id = s.set_id AND s.deleted_at IS NULL
JOIN users u ON rs.user_id = u.user_id AND u.deleted_at IS NULL
WHERE rs.status = 'PENDING'
ORDER BY 
    CASE 
        WHEN rs.scheduled_date < CURRENT_DATE THEN 1
        WHEN rs.scheduled_date = CURRENT_DATE THEN 2
        ELSE 3
    END,
    rs.scheduled_time;
```

## 6. Functions

### 6.1 Calculate Set Average Score Function
```sql
CREATE OR REPLACE FUNCTION calculate_set_average_score(set_uuid UUID)
RETURNS DECIMAL(5,2) AS $$
DECLARE
    avg_score DECIMAL(5,2);
BEGIN
    SELECT AVG(score) INTO avg_score
    FROM review_histories
    WHERE set_id = set_uuid 
    AND score IS NOT NULL
    AND status = 'COMPLETED';
    
    RETURN COALESCE(avg_score, 0.0);
END;
$$ LANGUAGE plpgsql;
```

### 6.2 Update Set Statistics Function
```sql
CREATE OR REPLACE FUNCTION update_set_statistics(set_uuid UUID)
RETURNS VOID AS $$
DECLARE
    total_reviews_count INTEGER;
    avg_score DECIMAL(5,2);
    last_review TIMESTAMP WITH TIME ZONE;
BEGIN
    -- Calculate total reviews
    SELECT COUNT(*) INTO total_reviews_count
    FROM review_histories
    WHERE set_id = set_uuid AND status = 'COMPLETED';
    
    -- Calculate average score
    SELECT calculate_set_average_score(set_uuid) INTO avg_score;
    
    -- Get last review date
    SELECT MAX(review_date) INTO last_review
    FROM review_histories
    WHERE set_id = set_uuid AND status = 'COMPLETED';
    
    -- Update set statistics
    UPDATE sets 
    SET 
        total_reviews = total_reviews_count,
        average_score = avg_score,
        last_review_date = last_review,
        updated_at = CURRENT_TIMESTAMP
    WHERE set_id = set_uuid;
END;
$$ LANGUAGE plpgsql;
```

### 6.3 Check Mastered Status Function
```sql
CREATE OR REPLACE FUNCTION check_mastered_status(set_uuid UUID)
RETURNS BOOLEAN AS $$
DECLARE
    recent_cycles RECORD;
    has_high_scores BOOLEAN := TRUE;
    no_skips BOOLEAN := TRUE;
    sufficient_time BOOLEAN := FALSE;
    first_review_date DATE;
    current_date DATE := CURRENT_DATE;
BEGIN
    -- Check if set has at least 3 completed cycles
    SELECT COUNT(*) INTO recent_cycles
    FROM (
        SELECT DISTINCT cycle_number
        FROM review_histories
        WHERE set_id = set_uuid 
        AND status = 'COMPLETED'
        ORDER BY cycle_number DESC
        LIMIT 3
    ) cycles;
    
    IF recent_cycles < 3 THEN
        RETURN FALSE;
    END IF;
    
    -- Check average score >= 85% in last 3 cycles
    FOR recent_cycles IN 
        SELECT cycle_number, AVG(score) as avg_score
        FROM review_histories
        WHERE set_id = set_uuid 
        AND status = 'COMPLETED'
        AND cycle_number IN (
            SELECT DISTINCT cycle_number
            FROM review_histories
            WHERE set_id = set_uuid 
            AND status = 'COMPLETED'
            ORDER BY cycle_number DESC
            LIMIT 3
        )
        GROUP BY cycle_number
    LOOP
        IF recent_cycles.avg_score < 85.0 THEN
            has_high_scores := FALSE;
            EXIT;
        END IF;
    END LOOP;
    
    -- Check no skips in last 3 cycles
    SELECT EXISTS(
        SELECT 1
        FROM review_histories
        WHERE set_id = set_uuid 
        AND status = 'SKIPPED'
        AND cycle_number IN (
            SELECT DISTINCT cycle_number
            FROM review_histories
            WHERE set_id = set_uuid 
            AND status = 'COMPLETED'
            ORDER BY cycle_number DESC
            LIMIT 3
        )
    ) INTO no_skips;
    
    -- Check sufficient learning time (>= 30 days)
    SELECT MIN(review_date)::DATE INTO first_review_date
    FROM review_histories
    WHERE set_id = set_uuid AND status = 'COMPLETED';
    
    IF first_review_date IS NOT NULL THEN
        sufficient_time := (current_date - first_review_date) >= 30;
    END IF;
    
    RETURN has_high_scores AND no_skips AND sufficient_time;
END;
$$ LANGUAGE plpgsql;
```

## 7. Triggers

### 7.1 Review History Trigger
```sql
CREATE OR REPLACE FUNCTION review_history_trigger()
RETURNS TRIGGER AS $$
BEGIN
    -- Update set statistics when review is completed
    IF NEW.status = 'COMPLETED' AND (OLD.status IS NULL OR OLD.status != 'COMPLETED') THEN
        PERFORM update_set_statistics(NEW.set_id);
        
        -- Check if cycle is completed
        IF EXISTS (
            SELECT 1 
            FROM review_histories 
            WHERE set_id = NEW.set_id 
            AND cycle_number = NEW.cycle_number 
            AND status = 'COMPLETED'
            HAVING COUNT(*) = 5
        ) THEN
            -- Update set current cycle
            UPDATE sets 
            SET current_cycle = current_cycle + 1,
                total_cycles = total_cycles + 1,
                updated_at = CURRENT_TIMESTAMP
            WHERE set_id = NEW.set_id;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER review_history_after_insert_update
    AFTER INSERT OR UPDATE ON review_histories
    FOR EACH ROW
    EXECUTE FUNCTION review_history_trigger();
```

### 7.2 Set Status Trigger
```sql
CREATE OR REPLACE FUNCTION set_status_trigger()
RETURNS TRIGGER AS $$
BEGIN
    -- Check mastered status when set is updated
    IF NEW.status = 'REVIEWING' AND check_mastered_status(NEW.set_id) THEN
        NEW.status := 'MASTERED';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_status_before_update
    BEFORE UPDATE ON sets
    FOR EACH ROW
    EXECUTE FUNCTION set_status_trigger();
```

## 8. Performance Indexes

### 8.1 Composite Indexes
```sql
-- Performance indexes for common queries
CREATE INDEX idx_sets_user_status_category ON sets(user_id, status, category) WHERE deleted_at IS NULL;
CREATE INDEX idx_review_histories_set_cycle_review ON review_histories(set_id, cycle_number, review_number);
CREATE INDEX idx_reminder_schedules_user_date_status ON reminder_schedules(user_id, scheduled_date, status);
```

### 8.2 Partial Indexes
```sql
-- Indexes for active records only
CREATE INDEX idx_sets_active ON sets(user_id, status) WHERE deleted_at IS NULL AND status IN ('LEARNING', 'REVIEWING');
CREATE INDEX idx_reminder_schedules_pending_today ON reminder_schedules(scheduled_date, status) WHERE status = 'PENDING' AND scheduled_date <= CURRENT_DATE + INTERVAL '7 days';
```

## 9. Data Migration

### 9.1 Initial Data Migration
```sql
-- Migration script for initial setup
DO $$
BEGIN
    -- Create default categories if not exist
    INSERT INTO set_categories (name, display_name, description, icon, color, sort_order)
    SELECT 'VOCABULARY', 'Từ vựng', 'Học từ vựng mới', 'book', '#4CAF50', 1
    WHERE NOT EXISTS (SELECT 1 FROM set_categories WHERE name = 'VOCABULARY');
    
    INSERT INTO set_categories (name, display_name, description, icon, color, sort_order)
    SELECT 'GRAMMAR', 'Ngữ pháp', 'Học ngữ pháp', 'school', '#2196F3', 2
    WHERE NOT EXISTS (SELECT 1 FROM set_categories WHERE name = 'GRAMMAR');
    
    INSERT INTO set_categories (name, display_name, description, icon, color, sort_order)
    SELECT 'MIXED', 'Tổng hợp', 'Kết hợp từ vựng và ngữ pháp', 'library_books', '#FF9800', 3
    WHERE NOT EXISTS (SELECT 1 FROM set_categories WHERE name = 'MIXED');
    
    INSERT INTO set_categories (name, display_name, description, icon, color, sort_order)
    SELECT 'OTHER', 'Khác', 'Các chủ đề khác', 'category', '#9C27B0', 4
    WHERE NOT EXISTS (SELECT 1 FROM set_categories WHERE name = 'OTHER');
    
    -- Create default skip reasons if not exist
    INSERT INTO skip_reasons (code, display_name, description, requires_note, sort_order)
    SELECT 'FORGOT', 'Quên', 'Quên ôn tập', FALSE, 1
    WHERE NOT EXISTS (SELECT 1 FROM skip_reasons WHERE code = 'FORGOT');
    
    INSERT INTO skip_reasons (code, display_name, description, requires_note, sort_order)
    SELECT 'BUSY', 'Bận', 'Quá bận rộn', FALSE, 2
    WHERE NOT EXISTS (SELECT 1 FROM skip_reasons WHERE code = 'BUSY');
    
    INSERT INTO skip_reasons (code, display_name, description, requires_note, sort_order)
    SELECT 'OTHER', 'Khác', 'Lý do khác', TRUE, 3
    WHERE NOT EXISTS (SELECT 1 FROM skip_reasons WHERE code = 'OTHER');
END $$;
```
