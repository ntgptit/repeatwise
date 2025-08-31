# Data Dictionary

## 1. User Management Data

### 1.1 User Entity

#### user_id
- **Type**: UUID
- **Description**: Định danh duy nhất của user
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**: 
  - Must be valid UUID format
  - Must be unique across all users
  - Generated automatically on user creation

#### email
- **Type**: VARCHAR(255)
- **Description**: Email address của user, dùng làm username
- **Domain Values**: Valid email format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid email format
  - Must be unique across all users
  - Case insensitive
  - Maximum length: 255 characters

#### password_hash
- **Type**: VARCHAR(255)
- **Description**: Mã hóa password của user
- **Domain Values**: BCrypt hash
- **Nullable**: No
- **Validation Rules**:
  - Must be BCrypt hash
  - Minimum length: 60 characters
  - Maximum length: 255 characters

#### full_name
- **Type**: VARCHAR(100)
- **Description**: Họ tên đầy đủ của user
- **Domain Values**: Text string
- **Nullable**: No
- **Validation Rules**:
  - Must not be empty
  - Maximum length: 100 characters
  - Trim whitespace

#### preferred_language
- **Type**: ENUM('VI', 'EN')
- **Description**: Ngôn ngữ ưa thích của user
- **Domain Values**: VI (Vietnamese), EN (English)
- **Nullable**: No
- **Default Value**: 'VI'
- **Validation Rules**:
  - Must be one of: VI, EN

#### timezone
- **Type**: VARCHAR(50)
- **Description**: Timezone của user
- **Domain Values**: Valid timezone identifier (e.g., 'Asia/Ho_Chi_Minh')
- **Nullable**: No
- **Default Value**: 'Asia/Ho_Chi_Minh'
- **Validation Rules**:
  - Must be valid timezone identifier
  - Maximum length: 50 characters

#### default_reminder_time
- **Type**: TIME
- **Description**: Thời gian nhắc nhở mặc định hàng ngày
- **Domain Values**: Time in HH:MM format
- **Nullable**: No
- **Default Value**: '09:00'
- **Validation Rules**:
  - Must be valid time format
  - Range: 00:00 to 23:59

#### status
- **Type**: ENUM('active', 'inactive', 'suspended')
- **Description**: Trạng thái tài khoản user
- **Domain Values**: active, inactive, suspended
- **Nullable**: No
- **Default Value**: 'active'
- **Validation Rules**:
  - Must be one of: active, inactive, suspended

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo tài khoản
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on user creation
  - Must be current timestamp

#### updated_at
- **Type**: TIMESTAMP
- **Description**: Thời gian cập nhật cuối cùng
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-updated on any change
  - Must be current timestamp

#### deleted_at
- **Type**: TIMESTAMP
- **Description**: Thời gian soft delete
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: Yes
- **Validation Rules**:
  - NULL if not deleted
  - Set to current timestamp on soft delete

## 2. Set Management Data

### 2.1 Set Entity

#### set_id
- **Type**: UUID
- **Description**: Định danh duy nhất của set
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format
  - Must be unique across all sets
  - Generated automatically on set creation

#### user_id
- **Type**: UUID
- **Description**: Foreign key đến user
- **Domain Values**: Valid user_id
- **Nullable**: No
- **Validation Rules**:
  - Must reference existing user
  - Cascade delete with user

#### name
- **Type**: VARCHAR(100)
- **Description**: Tên của set học tập
- **Domain Values**: Text string
- **Nullable**: No
- **Validation Rules**:
  - Must not be empty
  - Maximum length: 100 characters
  - Trim whitespace

#### description
- **Type**: VARCHAR(500)
- **Description**: Mô tả chi tiết của set
- **Domain Values**: Text string
- **Nullable**: Yes
- **Validation Rules**:
  - Maximum length: 500 characters
  - Trim whitespace

#### category
- **Type**: ENUM('vocabulary', 'grammar', 'mixed', 'other')
- **Description**: Phân loại của set
- **Domain Values**: vocabulary, grammar, mixed, other
- **Nullable**: No
- **Default Value**: 'vocabulary'
- **Validation Rules**:
  - Must be one of: vocabulary, grammar, mixed, other

#### word_count
- **Type**: INTEGER
- **Description**: Số lượng từ vựng trong set
- **Domain Values**: Positive integers
- **Nullable**: No
- **Validation Rules**:
  - Must be greater than 0
  - Maximum value: 10000

#### status
- **Type**: ENUM('not_started', 'learning', 'reviewing', 'mastered')
- **Description**: Trạng thái hiện tại của set
- **Domain Values**: not_started, learning, reviewing, mastered
- **Nullable**: No
- **Default Value**: 'not_started'
- **Validation Rules**:
  - Must be one of: not_started, learning, reviewing, mastered

#### current_cycle
- **Type**: INTEGER
- **Description**: Số thứ tự chu kỳ hiện tại
- **Domain Values**: Positive integers
- **Nullable**: No
- **Default Value**: 1
- **Validation Rules**:
  - Must be greater than 0
  - Auto-incremented when cycle completes

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo set
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on set creation
  - Must be current timestamp

#### updated_at
- **Type**: TIMESTAMP
- **Description**: Thời gian cập nhật cuối cùng
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-updated on any change
  - Must be current timestamp

#### deleted_at
- **Type**: TIMESTAMP
- **Description**: Thời gian soft delete
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: Yes
- **Validation Rules**:
  - NULL if not deleted
  - Set to current timestamp on soft delete

## 3. Review History Data

### 3.1 Review History Entity

#### review_id
- **Type**: UUID
- **Description**: Định danh duy nhất của lần ôn tập
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format
  - Must be unique across all reviews
  - Generated automatically on review creation

#### set_id
- **Type**: UUID
- **Description**: Foreign key đến set
- **Domain Values**: Valid set_id
- **Nullable**: No
- **Validation Rules**:
  - Must reference existing set
  - Cascade delete with set

#### cycle_number
- **Type**: INTEGER
- **Description**: Số thứ tự chu kỳ
- **Domain Values**: Positive integers
- **Nullable**: No
- **Validation Rules**:
  - Must be greater than 0
  - Must match set.current_cycle

#### review_number
- **Type**: INTEGER
- **Description**: Số thứ tự lần ôn trong chu kỳ (1-5)
- **Domain Values**: 1, 2, 3, 4, 5
- **Nullable**: No
- **Validation Rules**:
  - Must be between 1 and 5
  - Must be unique within cycle

#### score
- **Type**: INTEGER
- **Description**: Điểm số từ 0-100%
- **Domain Values**: 0-100
- **Nullable**: Yes (if skipped)
- **Validation Rules**:
  - Must be between 0 and 100
  - NULL if status is 'skipped'

#### status
- **Type**: ENUM('completed', 'skipped')
- **Description**: Trạng thái lần ôn tập
- **Domain Values**: completed, skipped
- **Nullable**: No
- **Default Value**: 'completed'
- **Validation Rules**:
  - Must be one of: completed, skipped

#### skip_reason
- **Type**: ENUM('forgot', 'busy', 'other')
- **Description**: Lý do bỏ qua lần ôn
- **Domain Values**: forgot, busy, other
- **Nullable**: Yes
- **Validation Rules**:
  - Required if status is 'skipped'
  - Must be one of: forgot, busy, other

#### note
- **Type**: VARCHAR(500)
- **Description**: Ghi chú của user
- **Domain Values**: Text string
- **Nullable**: Yes
- **Validation Rules**:
  - Maximum length: 500 characters
  - Trim whitespace

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo review
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on review creation
  - Must be current timestamp

## 4. Reminder Schedule Data

### 4.1 Reminder Schedule Entity

#### reminder_id
- **Type**: UUID
- **Description**: Định danh duy nhất của reminder
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format
  - Must be unique across all reminders
  - Generated automatically on reminder creation

#### set_id
- **Type**: UUID
- **Description**: Foreign key đến set
- **Domain Values**: Valid set_id
- **Nullable**: No
- **Validation Rules**:
  - Must reference existing set
  - Cascade delete with set

#### user_id
- **Type**: UUID
- **Description**: Foreign key đến user
- **Domain Values**: Valid user_id
- **Nullable**: No
- **Validation Rules**:
  - Must reference existing user
  - Must match set.user_id

#### remind_date
- **Type**: DATE
- **Description**: Ngày nhắc nhở
- **Domain Values**: Valid date
- **Nullable**: No
- **Validation Rules**:
  - Must be valid date
  - Must be in the future when created

#### status
- **Type**: ENUM('pending', 'sent', 'done', 'skipped', 'rescheduled', 'cancelled')
- **Description**: Trạng thái reminder
- **Domain Values**: pending, sent, done, skipped, rescheduled, cancelled
- **Nullable**: No
- **Default Value**: 'pending'
- **Validation Rules**:
  - Must be one of: pending, sent, done, skipped, rescheduled, cancelled

#### reschedule_count
- **Type**: INTEGER
- **Description**: Số lần đã reschedule
- **Domain Values**: 0-2
- **Nullable**: No
- **Default Value**: 0
- **Validation Rules**:
  - Must be between 0 and 2
  - Auto-incremented on reschedule

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo reminder
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on reminder creation
  - Must be current timestamp

#### updated_at
- **Type**: TIMESTAMP
- **Description**: Thời gian cập nhật cuối cùng
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-updated on any change
  - Must be current timestamp

## 5. Activity Log Data

### 5.1 Activity Log Entity

#### log_id
- **Type**: UUID
- **Description**: Định danh duy nhất của log entry
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format
  - Must be unique across all logs
  - Generated automatically on log creation

#### user_id
- **Type**: UUID
- **Description**: Foreign key đến user
- **Domain Values**: Valid user_id
- **Nullable**: No
- **Validation Rules**:
  - Must reference existing user

#### action_type
- **Type**: ENUM('create', 'update', 'delete', 'score_change', 'reschedule')
- **Description**: Loại hành động
- **Domain Values**: create, update, delete, score_change, reschedule
- **Nullable**: No
- **Validation Rules**:
  - Must be one of: create, update, delete, score_change, reschedule

#### entity_type
- **Type**: ENUM('user', 'set', 'review', 'reminder')
- **Description**: Loại entity bị thay đổi
- **Domain Values**: user, set, review, reminder
- **Nullable**: No
- **Validation Rules**:
  - Must be one of: user, set, review, reminder

#### entity_id
- **Type**: UUID
- **Description**: ID của entity bị thay đổi
- **Domain Values**: Valid UUID
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format

#### old_value
- **Type**: JSON
- **Description**: Giá trị cũ (nếu có)
- **Domain Values**: JSON object
- **Nullable**: Yes
- **Validation Rules**:
  - Must be valid JSON format

#### new_value
- **Type**: JSON
- **Description**: Giá trị mới
- **Domain Values**: JSON object
- **Nullable**: No
- **Validation Rules**:
  - Must be valid JSON format

#### reason
- **Type**: VARCHAR(500)
- **Description**: Lý do thay đổi
- **Domain Values**: Text string
- **Nullable**: Yes
- **Validation Rules**:
  - Maximum length: 500 characters
  - Trim whitespace

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo log
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on log creation
  - Must be current timestamp

## 6. System Configuration Data

### 6.1 System Configuration Entity

#### config_id
- **Type**: UUID
- **Description**: Định danh duy nhất của config
- **Domain Values**: UUID v4 format
- **Nullable**: No
- **Validation Rules**:
  - Must be valid UUID format
  - Must be unique across all configs
  - Generated automatically on config creation

#### config_key
- **Type**: VARCHAR(100)
- **Description**: Khóa cấu hình
- **Domain Values**: Text string
- **Nullable**: No
- **Validation Rules**:
  - Must not be empty
  - Maximum length: 100 characters
  - Must be unique
  - Trim whitespace

#### config_value
- **Type**: TEXT
- **Description**: Giá trị cấu hình
- **Domain Values**: Text string
- **Nullable**: No
- **Validation Rules**:
  - Must not be empty
  - Maximum length: 10000 characters

#### description
- **Type**: VARCHAR(500)
- **Description**: Mô tả cấu hình
- **Domain Values**: Text string
- **Nullable**: Yes
- **Validation Rules**:
  - Maximum length: 500 characters
  - Trim whitespace

#### is_active
- **Type**: BOOLEAN
- **Description**: Trạng thái hoạt động
- **Domain Values**: true, false
- **Nullable**: No
- **Default Value**: true
- **Validation Rules**:
  - Must be boolean value

#### created_at
- **Type**: TIMESTAMP
- **Description**: Thời gian tạo config
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-generated on config creation
  - Must be current timestamp

#### updated_at
- **Type**: TIMESTAMP
- **Description**: Thời gian cập nhật cuối cùng
- **Domain Values**: ISO 8601 timestamp
- **Nullable**: No
- **Validation Rules**:
  - Auto-updated on any change
  - Must be current timestamp

## 7. Default Configuration Values

### 7.1 SRS Algorithm Parameters

#### srs.base_delay
- **Type**: INTEGER
- **Value**: 30
- **Description**: Số ngày mặc định giữa các chu kỳ
- **Unit**: Days
- **Range**: 7-90

#### srs.penalty
- **Type**: DECIMAL(3,2)
- **Value**: 0.20
- **Description**: Hệ số giảm delay theo điểm số
- **Range**: 0.01-1.00

#### srs.scaling
- **Type**: DECIMAL(3,2)
- **Value**: 0.02
- **Description**: Hệ số tăng delay theo số từ
- **Range**: 0.01-0.10

#### srs.min_delay
- **Type**: INTEGER
- **Value**: 7
- **Description**: Delay tối thiểu
- **Unit**: Days
- **Range**: 1-30

#### srs.max_delay
- **Type**: INTEGER
- **Value**: 90
- **Description**: Delay tối đa
- **Unit**: Days
- **Range**: 30-365

### 7.2 Reminder Settings

#### reminder.max_daily_sets
- **Type**: INTEGER
- **Value**: 3
- **Description**: Số set tối đa mỗi ngày
- **Range**: 1-10

#### reminder.default_time
- **Type**: TIME
- **Value**: '09:00'
- **Description**: Thời gian nhắc nhở mặc định
- **Format**: HH:MM

#### reminder.reschedule_limit
- **Type**: INTEGER
- **Value**: 2
- **Description**: Số lần reschedule tối đa
- **Range**: 0-5

### 7.3 System Limits

#### system.max_sets_per_user
- **Type**: INTEGER
- **Value**: 100
- **Description**: Số set tối đa mỗi user
- **Range**: 10-1000

#### system.max_word_count
- **Type**: INTEGER
- **Value**: 10000
- **Description**: Số từ tối đa mỗi set
- **Range**: 100-50000

#### system.session_timeout
- **Type**: INTEGER
- **Value**: 3600
- **Description**: Thời gian timeout session
- **Unit**: Seconds
- **Range**: 300-86400

## 8. Data Validation Summary

### 8.1 Required Fields
- All UUID fields must be valid UUID v4 format
- All email fields must be valid email format
- All timestamp fields must be valid ISO 8601 format
- All enum fields must contain valid values
- All integer fields must be within specified ranges

### 8.2 Business Rules Validation
- User can only access their own data
- Set must belong to authenticated user
- Review must belong to existing set
- Reminder must belong to existing set and user
- Cycle number must match set current cycle
- Review number must be 1-5 within cycle

### 8.3 Data Integrity Constraints
- Foreign key constraints must be maintained
- Unique constraints must be enforced
- Check constraints must be validated
- Default values must be applied
- Audit fields must be auto-populated 
