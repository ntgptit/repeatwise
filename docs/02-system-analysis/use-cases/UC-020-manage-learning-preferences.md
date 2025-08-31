# UC-020: Manage Learning Preferences

## 1. Use Case Information

**Use Case ID**: UC-020
**Use Case Name**: Manage Learning Preferences
**Primary Actor**: Student (Người học)
**Secondary Actors**: Learning Service
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student quản lý cài đặt học tập để tùy chỉnh thuật toán SRS, thời gian học và các tham số khác ảnh hưởng đến trải nghiệm học tập.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- Student có tài khoản active
- Có kết nối internet

## 4. Main Flow

### Step 1: Access Learning Settings
**Actor Action**: Student mở màn hình Settings và chọn "Cài đặt học tập"
**System Response**: Hiển thị form cài đặt learning preferences

### Step 2: Configure SRS Algorithm
**Actor Action**: Student cấu hình thuật toán SRS:
- Base delay (số ngày mặc định)
- Penalty factor (hệ số giảm delay)
- Scaling factor (hệ số tăng delay)
- Minimum delay (delay tối thiểu)
- Maximum delay (delay tối đa)
**System Response**: Validate và hiển thị preview

### Step 3: Configure Learning Schedule
**Actor Action**: Student cấu hình lịch học:
- Thời gian học ưa thích
- Số set tối đa/ngày
- Khoảng cách giữa các phiên học
- Ngày nghỉ trong tuần
**System Response**: Hiển thị preview schedule

### Step 4: Configure Difficulty Settings
**Actor Action**: Student cấu hình độ khó:
- Mức độ khó mặc định
- Tự động điều chỉnh độ khó
- Ngưỡng điểm để tăng/giảm độ khó
- Số lần ôn tối thiểu trước khi điều chỉnh
**System Response**: Validate và hiển thị preview

### Step 5: Configure Review Settings
**Actor Action**: Student cấu hình cài đặt ôn tập:
- Số lần ôn trong chu kỳ
- Thời gian tối đa cho mỗi phiên ôn
- Cho phép skip review
- Tự động reschedule khi quá hạn
**System Response**: Hiển thị preview settings

### Step 6: Configure Progress Tracking
**Actor Action**: Student cấu hình theo dõi tiến trình:
- Cập nhật progress real-time
- Hiển thị detailed statistics
- Gửi progress report
- Cảnh báo khi performance giảm
**System Response**: Hiển thị preview options

### Step 7: Save Preferences
**Actor Action**: Student nhấn "Lưu cài đặt"
**System Response**: 
- Validate tất cả settings
- Hiển thị loading indicator

### Step 8: Update System Settings
**Actor Action**: System tự động
**System Response**:
- Lưu learning preferences
- Cập nhật user profile
- Áp dụng settings mới cho các set hiện tại

### Step 9: Send Confirmation
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Cập nhật UI
- Log settings change

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

### A3: Invalid SRS Parameters
**Trigger**: Tham số SRS không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Tham số không hợp lệ"
2. System highlight trường lỗi
3. Student sửa tham số
4. Return to Step 2

### A4: Reset to Default
**Trigger**: Student chọn "Khôi phục mặc định"
**Steps**:
1. System hiển thị dialog xác nhận reset
2. Student xác nhận
3. System reset tất cả settings về default
4. Continue to Step 7

### A5: Advanced Settings
**Trigger**: Student chọn "Cài đặt nâng cao"
**Steps**:
1. System hiển thị thêm options nâng cao
2. Student cấu hình advanced settings
3. System validate advanced settings
4. Continue to Step 7

## 6. Post Conditions

### Success Post Conditions
- Learning preferences được lưu
- User profile được cập nhật
- Settings mới được áp dụng cho các set
- Confirmation được hiển thị
- UI được cập nhật

### Failure Post Conditions
- Settings không thay đổi
- Error message được hiển thị
- Student có thể thử lại hoặc hủy bỏ

## 7. Business Rules

### BR-048: Learning Preferences
- Student có thể tùy chỉnh tất cả learning settings
- Settings được áp dụng cho set mới và set hiện tại
- SRS parameters phải hợp lệ (min < max)
- Settings được validate trước khi save

### BR-049: SRS Algorithm
- Base delay: 7-90 ngày
- Penalty factor: 0.1-0.5
- Scaling factor: 0.01-0.1
- Minimum delay: 1-7 ngày
- Maximum delay: 30-365 ngày

### BR-050: Learning Schedule
- Thời gian học phải trong active hours
- Số set tối đa: 1-10 set/ngày
- Khoảng cách tối thiểu: 1 giờ
- Ngày nghỉ: tối đa 3 ngày/tuần

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **SRS Parameters** (object, required)
- **Learning Schedule** (object, required)
- **Difficulty Settings** (object, required)
- **Review Settings** (object, required)
- **Progress Tracking** (object, required)
- **User ID** (UUID, required)

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
**Given** student thay đổi learning preferences
**When** student save settings hợp lệ
**Then** settings được lưu thành công
**And** settings mới được áp dụng

### AC-002: Invalid SRS Parameters
**Given** student nhập SRS parameters không hợp lệ
**When** student save settings
**Then** validation error được hiển thị
**And** settings không được lưu

### AC-003: Reset to Default
**Given** student chọn reset settings
**When** student xác nhận reset
**Then** settings được reset về default
**And** confirmation được hiển thị

### AC-004: Advanced Settings
**Given** student chọn advanced settings
**When** student cấu hình advanced options
**Then** advanced settings được lưu
**And** applied to learning algorithm

### AC-005: Network Error
**Given** mất kết nối internet
**When** student save settings
**Then** error message được hiển thị
**And** retry option được cung cấp

## 11. Test Cases

### TC-001: Normal Settings Save
**Test Data**: Valid learning preferences
**Expected Result**: Settings saved, applied immediately

### TC-002: Invalid SRS Parameters
**Test Data**: Min delay > max delay
**Expected Result**: Validation error, settings not saved

### TC-003: Reset to Default
**Test Data**: Reset action
**Expected Result**: Settings reset to default values

### TC-004: Advanced Settings
**Test Data**: Advanced learning options
**Expected Result**: Advanced settings saved and applied

### TC-005: Network Error
**Test Data**: Simulate network timeout
**Expected Result**: Error message, retry option

## 12. Related Use Cases

- **UC-003**: User Profile Management
- **UC-010**: Start Learning Cycle
- **UC-011**: Perform Review Session
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Implement SRS algorithm validation
- Add learning preferences persistence
- Implement settings application logic
- Add preferences change logging

### Future Enhancements
- AI-powered learning optimization
- Personalized learning paths
- Adaptive difficulty adjustment
- Learning analytics insights
