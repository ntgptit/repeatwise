# Entity Relationship Diagram (ERD)

## 1. Overview

Entity Relationship Diagram của RepeatWise mô tả mối quan hệ giữa các entities chính trong hệ thống Spaced Repetition System. ERD này được thiết kế để hỗ trợ các use cases chính của ứng dụng.

## 2. Core Entities

### 2.1 User Management Entities

```mermaid
erDiagram
    USERS {
        UUID user_id PK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR full_name
        ENUM preferred_language
        VARCHAR timezone
        TIME default_reminder_time
        ENUM status
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    USER_PROFILES {
        UUID profile_id PK
        UUID user_id FK
        VARCHAR avatar_url
        TEXT bio
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USER_SETTINGS {
        UUID settings_id PK
        UUID user_id FK
        BOOLEAN notification_enabled
        BOOLEAN email_notifications
        BOOLEAN push_notifications
        INTEGER daily_reminder_limit
        JSON learning_preferences
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USERS ||--|| USER_PROFILES : has
    USERS ||--|| USER_SETTINGS : has
```

### 2.2 Learning Management Entities

```mermaid
erDiagram
    SETS {
        UUID set_id PK
        UUID user_id FK
        VARCHAR name
        TEXT description
        ENUM category
        INTEGER word_count
        ENUM status
        INTEGER current_cycle
        INTEGER total_reviews
        DECIMAL average_score
        TIMESTAMP last_reviewed_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    SET_ITEMS {
        UUID item_id PK
        UUID set_id FK
        TEXT front_content
        TEXT back_content
        INTEGER item_order
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    LEARNING_CYCLES {
        UUID cycle_id PK
        UUID set_id FK
        INTEGER cycle_number
        DATE start_date
        DATE end_date
        DECIMAL average_score
        ENUM status
        INTEGER next_cycle_delay_days
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    REVIEW_HISTORIES {
        UUID review_id PK
        UUID set_id FK
        UUID cycle_id FK
        INTEGER review_number
        INTEGER score
        ENUM status
        ENUM skip_reason
        DATE review_date
        TEXT notes
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    SETS ||--o{ SET_ITEMS : contains
    SETS ||--o{ LEARNING_CYCLES : has
    LEARNING_CYCLES ||--o{ REVIEW_HISTORIES : contains
    USERS ||--o{ SETS : owns
```

### 2.3 Scheduling and Notification Entities

```mermaid
erDiagram
    REMINDER_SCHEDULES {
        UUID reminder_id PK
        UUID user_id FK
        UUID set_id FK
        DATE scheduled_date
        TIME reminder_time
        ENUM status
        TIMESTAMP sent_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USERS ||--o{ REMINDER_SCHEDULES : receives
    SETS ||--o{ REMINDER_SCHEDULES : scheduled_for
```

### 2.4 Audit and Configuration Entities

```mermaid
erDiagram
    ACTIVITY_LOGS {
        UUID log_id PK
        UUID user_id FK
        VARCHAR action_type
        VARCHAR entity_type
        UUID entity_id
        JSON old_values
        JSON new_values
        VARCHAR ip_address
        TEXT user_agent
        TIMESTAMP created_at
    }
    
    SYSTEM_CONFIGURATION {
        UUID config_id PK
        VARCHAR config_key UK
        TEXT config_value
        TEXT description
        BOOLEAN is_active
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USERS ||--o{ ACTIVITY_LOGS : generates
```

## 3. Complete ERD

```mermaid
erDiagram
    USERS {
        UUID user_id PK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR full_name
        ENUM preferred_language
        VARCHAR timezone
        TIME default_reminder_time
        ENUM status
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    USER_PROFILES {
        UUID profile_id PK
        UUID user_id FK
        VARCHAR avatar_url
        TEXT bio
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USER_SETTINGS {
        UUID settings_id PK
        UUID user_id FK
        BOOLEAN notification_enabled
        BOOLEAN email_notifications
        BOOLEAN push_notifications
        INTEGER daily_reminder_limit
        JSON learning_preferences
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    SETS {
        UUID set_id PK
        UUID user_id FK
        VARCHAR name
        TEXT description
        ENUM category
        INTEGER word_count
        ENUM status
        INTEGER current_cycle
        INTEGER total_reviews
        DECIMAL average_score
        TIMESTAMP last_reviewed_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at
    }
    
    SET_ITEMS {
        UUID item_id PK
        UUID set_id FK
        TEXT front_content
        TEXT back_content
        INTEGER item_order
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    LEARNING_CYCLES {
        UUID cycle_id PK
        UUID set_id FK
        INTEGER cycle_number
        DATE start_date
        DATE end_date
        DECIMAL average_score
        ENUM status
        INTEGER next_cycle_delay_days
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    REVIEW_HISTORIES {
        UUID review_id PK
        UUID set_id FK
        UUID cycle_id FK
        INTEGER review_number
        INTEGER score
        ENUM status
        ENUM skip_reason
        DATE review_date
        TEXT notes
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    REMINDER_SCHEDULES {
        UUID reminder_id PK
        UUID user_id FK
        UUID set_id FK
        DATE scheduled_date
        TIME reminder_time
        ENUM status
        TIMESTAMP sent_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    ACTIVITY_LOGS {
        UUID log_id PK
        UUID user_id FK
        VARCHAR action_type
        VARCHAR entity_type
        UUID entity_id
        JSON old_values
        JSON new_values
        VARCHAR ip_address
        TEXT user_agent
        TIMESTAMP created_at
    }
    
    SYSTEM_CONFIGURATION {
        UUID config_id PK
        VARCHAR config_key UK
        TEXT config_value
        TEXT description
        BOOLEAN is_active
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    
    USERS ||--|| USER_PROFILES : has
    USERS ||--|| USER_SETTINGS : has
    USERS ||--o{ SETS : owns
    SETS ||--o{ SET_ITEMS : contains
    SETS ||--o{ LEARNING_CYCLES : has
    LEARNING_CYCLES ||--o{ REVIEW_HISTORIES : contains
    USERS ||--o{ REMINDER_SCHEDULES : receives
    SETS ||--o{ REMINDER_SCHEDULES : scheduled_for
    USERS ||--o{ ACTIVITY_LOGS : generates
```

## 4. Relationship Details

### 4.1 One-to-One Relationships
- **User ↔ UserProfile**: Mỗi user có đúng 1 profile
- **User ↔ UserSettings**: Mỗi user có đúng 1 settings

### 4.2 One-to-Many Relationships
- **User → Sets**: Mỗi user có thể có nhiều sets
- **Set → SetItems**: Mỗi set có thể có nhiều items
- **Set → LearningCycles**: Mỗi set có thể có nhiều cycles
- **LearningCycle → ReviewHistories**: Mỗi cycle có thể có nhiều reviews
- **User → ReminderSchedules**: Mỗi user có thể có nhiều reminders
- **Set → ReminderSchedules**: Mỗi set có thể có nhiều reminders
- **User → ActivityLogs**: Mỗi user có thể có nhiều activity logs

### 4.3 Cardinality Rules
- **Mandatory**: Tất cả relationships đều là mandatory (không có optional relationships)
- **Cascade Delete**: Khi xóa parent record, tất cả child records sẽ bị xóa
- **Soft Delete**: Chỉ áp dụng cho Users và Sets, các bảng khác sử dụng hard delete

## 5. Business Rules Implementation

### 5.1 User Management Rules
- Email phải duy nhất trong hệ thống
- User chỉ có thể access dữ liệu của mình
- Soft delete cho users để bảo toàn dữ liệu

### 5.2 Learning Management Rules
- Mỗi set thuộc về đúng 1 user
- Mỗi cycle có đúng 5 reviews
- Set status được update dựa trên performance
- Average score được tính từ review histories

### 5.3 Scheduling Rules
- Tối đa 3 reminders/user/ngày
- Reminder được tạo dựa trên cycle schedule
- Status tracking cho reminder delivery

### 5.4 Audit Rules
- Tất cả thay đổi quan trọng được log
- Activity logs không bao giờ bị xóa
- IP address và user agent được capture

## 6. Data Integrity Constraints

### 6.1 Primary Keys
- Tất cả tables đều có UUID primary key
- UUID được generate tự động

### 6.2 Foreign Keys
- Tất cả foreign keys đều có CASCADE DELETE
- Foreign keys được index để tối ưu performance

### 6.3 Unique Constraints
- Email trong users table
- Config_key trong system_configuration table
- Set_id + cycle_number trong learning_cycles table

### 6.4 Check Constraints
- Score phải từ 0-100 trong review_histories
- Word_count phải > 0 trong sets
- Daily_reminder_limit phải từ 1-10 trong user_settings 
