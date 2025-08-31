# UC-017: Manage Notification Preferences

## 1. Use Case Information

**Use Case ID**: UC-017
**Use Case Name**: Manage Notification Preferences
**Primary Actor**: Student (Người học)
**Secondary Actors**: Notification Service
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student quản lý cài đặt thông báo để tùy chỉnh cách nhận reminder và notification từ hệ thống.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có tài khoản active
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Settings
**Actor Action**: Student mở màn hình Settings
**System Response**: Hiển thị danh sách các cài đặt có thể thay đổi

### Step 2: Select Notification Settings
**Actor Action**: Student chọn "Cài đặt thông báo"
**System Response**: Hiển thị form cài đặt notification với các tùy chọn hiện tại

### Step 3: Configure Reminder Settings
**Actor Action**: Student cấu hình reminder settings:
- Thời gian nhắc nhở mặc định
- Số lượng reminder tối đa/ngày
- Khoảng cách giữa các reminder
**System Response**: Validate và hiển thị preview

### Step 4: Configure Notification Types
**Actor Action**: Student bật/tắt các loại notification:
- Push notification
- Email notification
- In-app notification
- Sound alerts
**System Response**: Cập nhật UI theo lựa chọn

### Step 5: Configure Quiet Hours
**Actor Action**: Student cấu hình giờ yên lặng:
- Bắt đầu quiet hours
- Kết thúc quiet hours
- Timezone
**System Response**: Validate và hiển thị preview

### Step 6: Configure Frequency
**Actor Action**: Student chọn tần suất notification:
- Ngay lập tức
- Tổng hợp hàng ngày
- Tổng hợp hàng tuần
**System Response**: Hiển thị preview schedule

### Step 7: Save Preferences
**Actor Action**: Student nhấn "Lưu cài đặt"
**System Response**: 
- Validate tất cả settings
- Hiển thị loading indicator

### Step 8: Update System Settings
**Actor Action**: System tự động
**System Response**:
- Lưu notification preferences
- Cập nhật user profile
- Áp dụng settings mới

### Step 9: Send Confirmation
**Actor Action**: System tự động
**System Response**:
- Gửi notification xác nhận (nếu được bật)
- Hiển thị thông báo thành công
- Cập nhật UI

## 5. Alternative Flows

### A1: Cancel Changes
**Trigger**: Student hủy bỏ thay đổi
**Steps**:
1. System hiển thị dialog xác nhận hủy
2. Student xác nhận hủy
3. System quay lại settings cũ
4. Return to Step 1

### A2: Network Connection Error
**Trigger**: Mất kết nối khi save settings
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu settings locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Hủy bỏ thay đổi
4. Return to Step 1

### A3: Invalid Time Settings
**Trigger**: Quiet hours không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Thời gian không hợp lệ"
2. System highlight trường lỗi
3. Student sửa thời gian
4. Return to Step 5

### A4: Disable All Notifications
**Trigger**: Student tắt tất cả notification
**Steps**:
1. System hiển thị cảnh báo: "Bạn sẽ không nhận được reminder"
2. System yêu cầu xác nhận đặc biệt
3. Student xác nhận
4. Continue to Step 7

### A5: Reset to Default
**Trigger**: Student chọn "Khôi phục mặc định"
**Steps**:
1. System hiển thị dialog xác nhận reset
2. Student xác nhận
3. System reset tất cả settings về default
4. Continue to Step 7

## 6. Post Conditions

### Success Post Conditions
- Notification preferences được lưu
- User profile được cập nhật
- Settings mới được áp dụng
- Confirmation notification được gửi (nếu được bật)
- UI được cập nhật

### Failure Post Conditions
- Settings không thay đổi
- Error message được hiển thị
- Student có thể thử lại hoặc hủy bỏ

## 7. Business Rules

### BR-042: Notification Management
- Student có thể tùy chỉnh tất cả notification settings
- Settings được áp dụng ngay lập tức
- Quiet hours không gửi notification
- Tối đa 3 reminder/ngày theo user preference

### BR-043: Notification Types
- Push notification yêu cầu permission
- Email notification yêu cầu email verified
- In-app notification luôn available
- Sound alerts tùy thuộc device settings

### BR-044: Time Settings
- Quiet hours phải có start < end
- Timezone phải hợp lệ
- Default reminder time phải trong active hours
- Settings được validate trước khi save

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Default Reminder Time** (time, required)
- **Max Daily Reminders** (integer, required, 1-5)
- **Notification Types** (array, required)
- **Quiet Hours Start** (time, optional)
- **Quiet Hours End** (time, optional)
- **Timezone** (string, required)
- **Notification Frequency** (enum, required)

### Output Data
- **Settings ID** (UUID)
- **Update Status** (success/error)
- **Applied Settings** (object)
- **Validation Messages** (array)

## 9. Non-Functional Requirements

### Performance
- Settings save < 2 giây
- Settings load < 1 giây
- Validation < 500ms

### Security
- Validate user ownership
- Log all settings changes
- Prevent unauthorized access

### Usability
- Intuitive settings interface
- Real-time validation
- Clear feedback
- Easy reset option

## 10. Acceptance Criteria

### AC-001: Successful Settings Save
**Given** student thay đổi notification settings
**When** student save settings hợp lệ
**Then** settings được lưu thành công
**And** settings mới được áp dụng

### AC-002: Invalid Time Validation
**Given** student nhập quiet hours không hợp lệ
**When** student save settings
**Then** validation error được hiển thị
**And** settings không được lưu

### AC-003: Disable All Notifications
**Given** student tắt tất cả notification
**When** student save settings
**Then** warning được hiển thị
**And** confirmation được yêu cầu

### AC-004: Reset to Default
**Given** student chọn reset settings
**When** student xác nhận reset
**Then** settings được reset về default
**And** confirmation được hiển thị

### AC-005: Network Error Handling
**Given** mất kết nối internet
**When** student save settings
**Then** error message được hiển thị
**And** retry option được cung cấp

## 11. Test Cases

### TC-001: Normal Settings Save
**Test Data**: Valid notification settings
**Expected Result**: Settings saved, applied immediately

### TC-002: Invalid Quiet Hours
**Test Data**: Start time > end time
**Expected Result**: Validation error, settings not saved

### TC-003: Disable All Notifications
**Test Data**: All notification types disabled
**Expected Result**: Warning displayed, confirmation required

### TC-004: Reset to Default
**Test Data**: Reset action
**Expected Result**: Settings reset to default values

### TC-005: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

## 12. Related Use Cases

- **UC-003**: User Profile Management
- **UC-016**: Reschedule Reminder
- **UC-020**: Manage Learning Preferences
- **UC-021**: View System Settings

## 13. Notes

### Implementation Notes
- Implement notification permission handling
- Add settings validation logic
- Implement quiet hours logic
- Add settings change logging

### Future Enhancements
- Smart notification timing
- Personalized notification content
- Advanced quiet hours rules
- Notification analytics
