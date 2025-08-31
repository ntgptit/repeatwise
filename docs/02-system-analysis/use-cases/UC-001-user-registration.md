# UC-001: User Registration

## 1. Use Case Information

**Use Case ID**: UC-001
**Use Case Name**: User Registration
**Primary Actor**: Student (Người học)
**Secondary Actors**: Email Service, Notification Service
**Priority**: High
**Complexity**: Medium

## 2. Brief Description

Student tạo tài khoản mới để sử dụng ứng dụng RepeatWise. Hệ thống validate thông tin, tạo user account và gửi email xác nhận.

## 3. Preconditions

- Student chưa có tài khoản trong hệ thống
- Mobile app đã được cài đặt
- Có kết nối internet
- Email service hoạt động bình thường

## 4. Main Flow

### Step 1: Access Registration Form
**Actor Action**: Student mở app và chọn "Đăng ký"
**System Response**: Hiển thị form đăng ký với các trường bắt buộc

### Step 2: Input Basic Information
**Actor Action**: Student nhập thông tin cơ bản:
- Email address
- Password (8-20 ký tự)
- Confirm password
- Họ tên đầy đủ
**System Response**: Validate real-time input và hiển thị feedback

### Step 3: Input Preferences
**Actor Action**: Student chọn preferences:
- Ngôn ngữ ưa thích (VI/EN)
- Timezone
- Thời gian nhắc nhở mặc định
**System Response**: Hiển thị preview settings

### Step 4: Submit Registration
**Actor Action**: Student nhấn "Đăng ký"
**System Response**: 
- Validate tất cả thông tin
- Kiểm tra email không trùng lặp
- Tạo user account với UUID
- Hash password với BCrypt
- Set status = 'active'

### Step 5: Send Confirmation Email
**Actor Action**: System tự động
**System Response**:
- Tạo email xác nhận với verification link
- Gửi email qua Email Service
- Log email delivery status

### Step 6: Complete Registration
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Chuyển user đến màn hình chính
- Tạo welcome tour cho user mới

## 5. Alternative Flows

### A1: Email Already Exists
**Trigger**: Email đã tồn tại trong hệ thống
**Steps**:
1. System hiển thị thông báo lỗi: "Email đã được sử dụng"
2. System highlight trường email
3. Student có thể:
   - Nhập email khác
   - Chọn "Đăng nhập" nếu đã có tài khoản
4. Return to Step 2

### A2: Password Validation Failed
**Trigger**: Password không đáp ứng yêu cầu
**Steps**:
1. System hiển thị yêu cầu password:
   - Tối thiểu 8 ký tự
   - Tối đa 20 ký tự
   - Có ít nhất 1 chữ hoa
   - Có ít nhất 1 chữ thường
   - Có ít nhất 1 số
2. System highlight trường password
3. Student nhập password mới
4. Return to Step 2

### A3: Confirm Password Mismatch
**Trigger**: Confirm password không khớp với password
**Steps**:
1. System hiển thị thông báo lỗi: "Mật khẩu xác nhận không khớp"
2. System highlight trường confirm password
3. Student nhập lại confirm password
4. Return to Step 2

### A4: Network Connection Error
**Trigger**: Mất kết nối internet trong quá trình đăng ký
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. System lưu draft form data locally
3. Student có thể:
   - Thử lại khi có kết nối
   - Lưu form để đăng ký sau
4. Return to Step 1

### A5: Email Service Unavailable
**Trigger**: Email service không khả dụng
**Steps**:
1. System tạo user account thành công
2. System log email delivery failure
3. System hiển thị thông báo: "Tài khoản đã tạo, email xác nhận sẽ được gửi sau"
4. System retry email delivery sau 1 giờ
5. Continue to Step 6

## 6. Post Conditions

### Success Post Conditions
- User account được tạo thành công với UUID
- Password được mã hóa với BCrypt
- User có thể đăng nhập vào hệ thống
- Email xác nhận được gửi (hoặc queued)
- User được chuyển đến màn hình chính
- Welcome tour được hiển thị

### Failure Post Conditions
- User account không được tạo
- Form data được giữ lại (nếu có lỗi validation)
- Error message được hiển thị
- User có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-020: User Authentication
- User phải có email duy nhất
- Password phải được mã hóa với BCrypt
- Session timeout = 1 giờ

### BR-021: Authorization
- User chỉ có thể access dữ liệu của mình
- Role-based access control

### BR-015: UUID Usage
- Tất cả ID sử dụng UUID format
- UUID được generate trước khi insert

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- created_at = now() khi tạo mới

## 8. Data Requirements

### Input Data
- **Email** (string, required, email format)
- **Password** (string, required, 8-20 chars)
- **Confirm Password** (string, required, match password)
- **Full Name** (string, required, ≤ 100 chars)
- **Preferred Language** (enum: VI, EN)
- **Timezone** (string, required)
- **Default Reminder Time** (time, required)

### Output Data
- **User ID** (UUID)
- **Registration Status** (success/error)
- **Error Messages** (if any)
- **Email Confirmation Status** (sent/failed/queued)

## 9. Non-Functional Requirements

### Performance
- Registration process < 5 giây
- Email delivery < 30 giây
- Form validation < 1 giây

### Security
- Password được mã hóa với BCrypt (cost = 12)
- Email verification required
- Rate limiting: 5 attempts per hour per IP

### Usability
- Form validation real-time
- Clear error messages
- Progress indicator
- Auto-save draft

## 10. Acceptance Criteria

### AC-001: Successful Registration
**Given** student chưa có tài khoản
**When** student nhập thông tin đăng ký hợp lệ
**Then** tài khoản được tạo thành công
**And** email xác nhận được gửi
**And** user có thể đăng nhập

### AC-002: Email Validation
**Given** student nhập email không hợp lệ
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-003: Password Validation
**Given** student nhập password không đủ mạnh
**When** student submit form
**Then** system hiển thị yêu cầu password
**And** form không được submit

### AC-004: Duplicate Email
**Given** email đã tồn tại trong hệ thống
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form không được submit

### AC-005: Network Error Handling
**Given** mất kết nối internet
**When** student submit form
**Then** system hiển thị thông báo lỗi
**And** form data được lưu locally

## 11. Test Cases

### TC-001: Valid Registration
**Test Data**: Valid email, strong password, complete information
**Expected Result**: Account created, email sent, redirect to main screen

### TC-002: Invalid Email Format
**Test Data**: Invalid email format
**Expected Result**: Validation error, form not submitted

### TC-003: Weak Password
**Test Data**: Password < 8 characters
**Expected Result**: Password requirements displayed

### TC-004: Duplicate Email
**Test Data**: Email already exists
**Expected Result**: Duplicate email error

### TC-005: Network Timeout
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-002**: User Login
- **UC-003**: User Profile Management
- **UC-004**: Password Reset
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Sử dụng Spring Security cho password encoding
- Implement email verification flow
- Add rate limiting cho registration attempts
- Log all registration attempts for security

### Future Enhancements
- Social login (Google, Facebook)
- Phone number verification
- Two-factor authentication
- Referral system
