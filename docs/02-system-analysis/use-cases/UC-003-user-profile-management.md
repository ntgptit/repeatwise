# UC-003: User Profile Management

## 1. Use Case Information

**Use Case ID**: UC-003
**Use Case Name**: User Profile Management
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: Medium
**Complexity**: Low

## 2. Brief Description

Student xem và cập nhật thông tin cá nhân, cài đặt tài khoản, và quản lý preferences. Hệ thống validate thông tin và cập nhật profile.

## 3. Preconditions

- Student đã đăng nhập vào hệ thống
- User có quyền truy cập profile management
- User account không bị khóa hoặc suspended

## 4. Main Flow

### Step 1: Access Profile Management
**Actor Action**: Student chọn "Hồ sơ cá nhân" từ menu
**System Response**: Hiển thị trang quản lý profile với thông tin hiện tại

### Step 2: View Current Profile
**Actor Action**: Student xem thông tin profile hiện tại
**System Response**: Hiển thị:
- Họ tên
- Email address
- Ngôn ngữ ưa thích
- Timezone
- Thời gian nhắc nhở mặc định
- Ngày tạo tài khoản
- Trạng thái tài khoản

### Step 3: Edit Profile Information
**Actor Action**: Student chọn "Chỉnh sửa" và cập nhật thông tin
**System Response**: Hiển thị form chỉnh sửa với dữ liệu hiện tại

### Step 4: Update Personal Information
**Actor Action**: Student cập nhật:
- Họ tên (≤ 100 ký tự)
- Ngôn ngữ ưa thích (VI/EN)
- Timezone
- Thời gian nhắc nhở mặc định
**System Response**: Validate real-time input và hiển thị feedback

### Step 5: Save Profile Changes
**Actor Action**: Student nhấn "Lưu thay đổi"
**System Response**:
- Validate tất cả thông tin
- Cập nhật user profile
- Lưu vào activity_logs
- Hiển thị thông báo thành công

### Step 6: Complete Profile Update
**Actor Action**: System tự động
**System Response**:
- Cập nhật updated_at timestamp
- Refresh profile display
- Hiển thị thông báo cập nhật thành công

## 5. Alternative Flows

### A1: Invalid Name Input
**Trigger**: Họ tên không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Họ tên không được để trống và tối đa 100 ký tự"
2. System highlight trường họ tên
3. Student nhập lại họ tên
4. Return to Step 4

### A2: Invalid Timezone Selection
**Trigger**: Timezone không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Vui lòng chọn timezone hợp lệ"
2. System highlight trường timezone
3. Student chọn lại timezone
4. Return to Step 4

### A3: Invalid Reminder Time
**Trigger**: Thời gian nhắc nhở không hợp lệ
**Steps**:
1. System hiển thị thông báo lỗi: "Thời gian nhắc nhở phải trong khoảng 00:00-23:59"
2. System highlight trường thời gian
3. Student chọn lại thời gian
4. Return to Step 4

### A4: No Changes Made
**Trigger**: Student không thay đổi thông tin nào
**Steps**:
1. System hiển thị thông báo: "Không có thay đổi nào được thực hiện"
2. Student có thể tiếp tục chỉnh sửa hoặc quay lại
3. Return to Step 2

### A5: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft changes locally
3. Student có thể thử lại khi có kết nối
4. Return to Step 1

### A6: Account Suspended
**Trigger**: Tài khoản bị suspended
**Steps**:
1. System hiển thị thông báo: "Tài khoản đã bị tạm khóa, không thể cập nhật profile"
2. System hiển thị lý do suspension
3. Student có thể liên hệ support
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Profile được cập nhật thành công
- Thông tin mới được lưu vào database
- Activity log được tạo
- User preferences được cập nhật

### Failure Post Conditions
- Profile không được thay đổi
- Error message được hiển thị
- User có thể thử lại hoặc hủy thay đổi

## 7. Business Rules

### BR-020: User Authentication
- User phải đăng nhập để truy cập profile
- User chỉ có thể chỉnh sửa profile của mình
- Session timeout = 1 giờ

### BR-021: User Authorization
- User chỉ có thể access dữ liệu của mình
- Role-based access control

### BR-023: Profile Validation
- Họ tên: bắt buộc, ≤ 100 ký tự
- Email: không được thay đổi (read-only)
- Ngôn ngữ: enum (VI/EN)
- Timezone: phải hợp lệ
- Thời gian nhắc nhở: 00:00-23:59

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- updated_at = now() khi cập nhật
- Lưu lịch sử thay đổi vào activity_logs

## 8. Data Requirements

### Input Data
- **User ID** (UUID, required)
- **Full Name** (string, required, ≤ 100 chars)
- **Language** (enum: VI/EN, required)
- **Timezone** (string, required, valid timezone)
- **Default Reminder Time** (time, required, HH:MM format)

### Output Data
- **Update Status** (success/error)
- **Updated Profile Info** (new values)
- **Error Messages** (if any)
- **Activity Log Entry** (who, when, what changed)

## 9. Non-Functional Requirements

### Performance
- Profile load < 2 giây
- Profile update < 3 giây
- Form validation < 500ms

### Security
- User chỉ có thể cập nhật profile của mình
- Validate user permissions
- Log all profile changes

### Usability
- Form validation real-time
- Clear error messages
- Auto-save draft changes
- Responsive design

## 10. Acceptance Criteria

### AC-001: Successful Profile Update
**Given** user có thông tin profile hợp lệ
**When** user cập nhật thông tin
**Then** profile được cập nhật thành công
**And** thông tin mới được lưu
**And** activity log được tạo

### AC-002: Invalid Name Input
**Given** user nhập họ tên không hợp lệ
**When** user submit form
**Then** system hiển thị thông báo lỗi
**And** profile không được cập nhật

### AC-003: Timezone Validation
**Given** user chọn timezone không hợp lệ
**When** user submit form
**Then** system hiển thị thông báo lỗi
**And** user được yêu cầu chọn timezone khác

### AC-004: No Changes Made
**Given** user không thay đổi thông tin nào
**When** user submit form
**Then** system hiển thị thông báo không có thay đổi
**And** profile không được cập nhật

### AC-005: Network Error
**Given** mất kết nối internet
**When** user thử cập nhật profile
**Then** system hiển thị thông báo lỗi kết nối
**And** draft changes được lưu locally

## 11. Test Cases

### TC-001: Valid Profile Update
**Test Data**: Valid name, language, timezone, reminder time
**Expected Result**: Profile updated successfully, activity logged

### TC-002: Invalid Name
**Test Data**: Empty name or name > 100 characters
**Expected Result**: Validation error, profile not updated

### TC-003: Invalid Timezone
**Test Data**: Invalid timezone string
**Expected Result**: Validation error, profile not updated

### TC-004: Invalid Reminder Time
**Test Data**: Time outside 00:00-23:59 range
**Expected Result**: Validation error, profile not updated

### TC-005: No Changes
**Test Data**: Same values as current profile
**Expected Result**: No changes message, profile not updated

### TC-006: Network Timeout
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-002**: User Login
- **UC-004**: Password Reset
- **UC-015**: Receive Reminder
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Email không được phép thay đổi (read-only)
- Validate timezone format và existence
- Implement auto-save cho draft changes
- Log all profile changes for audit

### Future Enhancements
- Profile picture upload
- Social media integration
- Advanced notification preferences
- Privacy settings

### UI/UX Considerations
- Clean, intuitive form design
- Real-time validation feedback
- Clear success/error messages
- Responsive design for mobile
- Auto-save indicators
