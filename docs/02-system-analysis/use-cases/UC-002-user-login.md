# UC-002: User Login

## 1. Use Case Information

**Use Case ID**: UC-002
**Use Case Name**: User Login
**Primary Actor**: Student (Người học)
**Secondary Actors**: None
**Priority**: High
**Complexity**: Low

## 2. Brief Description

Student đăng nhập vào hệ thống RepeatWise bằng email và password. Hệ thống validate thông tin đăng nhập và tạo session cho user.

## 3. Preconditions

- Student đã có tài khoản trong hệ thống
- Mobile app đã được cài đặt
- Có kết nối internet
- User account không bị khóa hoặc suspended

## 4. Main Flow

### Step 1: Access Login Form
**Actor Action**: Student mở app và chọn "Đăng nhập"
**System Response**: Hiển thị form đăng nhập với email và password fields

### Step 2: Input Credentials
**Actor Action**: Student nhập:
- Email address
- Password
**System Response**: Validate real-time input và hiển thị feedback

### Step 3: Submit Login
**Actor Action**: Student nhấn "Đăng nhập"
**System Response**: 
- Validate email và password
- Check account status
- Verify password với BCrypt
- Generate JWT token
- Create user session

### Step 4: Complete Login
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo đăng nhập thành công
- Chuyển user đến dashboard
- Load user preferences và settings

## 5. Alternative Flows

### A1: Invalid Email Format
**Trigger**: Email không đúng format
**Steps**:
1. System hiển thị thông báo lỗi: "Email không đúng định dạng"
2. System highlight trường email
3. Student nhập lại email
4. Return to Step 2

### A2: Email Not Found
**Trigger**: Email không tồn tại trong hệ thống
**Steps**:
1. System hiển thị thông báo lỗi: "Email hoặc mật khẩu không đúng"
2. System highlight trường email
3. Student có thể:
   - Nhập lại email
   - Chọn "Đăng ký" nếu chưa có tài khoản
4. Return to Step 2

### A3: Invalid Password
**Trigger**: Password không đúng
**Steps**:
1. System hiển thị thông báo lỗi: "Email hoặc mật khẩu không đúng"
2. System highlight trường password
3. Student nhập lại password
4. Return to Step 2

### A4: Account Locked
**Trigger**: Account bị khóa do nhiều lần đăng nhập sai
**Steps**:
1. System hiển thị thông báo: "Tài khoản đã bị khóa do nhiều lần đăng nhập sai"
2. System hiển thị thời gian unlock
3. Student có thể:
   - Chờ unlock tự động
   - Chọn "Quên mật khẩu"
4. Return to Step 1

### A5: Account Suspended
**Trigger**: Account bị suspended bởi admin
**Steps**:
1. System hiển thị thông báo: "Tài khoản đã bị tạm khóa"
2. System hiển thị lý do suspension
3. Student có thể liên hệ support
4. Return to Step 1

### A6: Network Connection Error
**Trigger**: Mất kết nối internet
**Steps**:
1. System hiển thị thông báo lỗi kết nối
2. Student có thể thử lại khi có kết nối
3. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- User được đăng nhập thành công
- JWT token được tạo và lưu
- User session được tạo
- User được chuyển đến dashboard
- User preferences được load

### Failure Post Conditions
- User không được đăng nhập
- Error message được hiển thị
- Form data được giữ lại (email)
- User có thể thử lại hoặc chọn "Quên mật khẩu"

## 7. Business Rules

### BR-020: User Authentication
- User phải có email duy nhất
- Password phải được mã hóa với BCrypt
- Session timeout = 1 giờ

### BR-021: Authorization
- User chỉ có thể access dữ liệu của mình
- Role-based access control

### BR-022: Account Security
- Account bị khóa sau 5 lần đăng nhập sai
- Thời gian khóa: 30 phút
- Admin có thể suspend account

## 8. Data Requirements

### Input Data
- **Email** (string, required, email format)
- **Password** (string, required)

### Output Data
- **JWT Token** (string)
- **User ID** (UUID)
- **Login Status** (success/error)
- **Error Messages** (if any)
- **Session Information** (expiry time, permissions)

## 9. Non-Functional Requirements

### Performance
- Login process < 3 giây
- Token generation < 1 giây
- Form validation < 500ms

### Security
- Password được verify với BCrypt
- JWT token có expiry time
- Rate limiting: 5 attempts per 15 minutes per IP
- Account lockout after failed attempts

### Usability
- Form validation real-time
- Clear error messages
- Remember email option
- Forgot password link

## 10. Acceptance Criteria

### AC-001: Successful Login
**Given** user có tài khoản hợp lệ
**When** user nhập email và password đúng
**Then** user được đăng nhập thành công
**And** JWT token được tạo
**And** user được chuyển đến dashboard

### AC-002: Invalid Credentials
**Given** user nhập thông tin không đúng
**When** user submit form
**Then** system hiển thị thông báo lỗi chung
**And** form không được submit

### AC-003: Account Locked
**Given** user đã đăng nhập sai 5 lần
**When** user thử đăng nhập lần nữa
**Then** system hiển thị thông báo account bị khóa
**And** user không thể đăng nhập trong 30 phút

### AC-004: Network Error
**Given** mất kết nối internet
**When** user thử đăng nhập
**Then** system hiển thị thông báo lỗi kết nối
**And** user có thể thử lại khi có kết nối

## 11. Test Cases

### TC-001: Valid Login
**Test Data**: Valid email and password
**Expected Result**: Login successful, JWT token generated, redirect to dashboard

### TC-002: Invalid Email
**Test Data**: Invalid email format
**Expected Result**: Validation error, form not submitted

### TC-003: Email Not Found
**Test Data**: Email not in database
**Expected Result**: Generic error message, form not submitted

### TC-004: Wrong Password
**Test Data**: Valid email, wrong password
**Expected Result**: Generic error message, form not submitted

### TC-005: Account Locked
**Test Data**: Account locked due to failed attempts
**Expected Result**: Account locked message, cannot login

### TC-006: Network Timeout
**Test Data**: Simulate network timeout
**Expected Result**: Network error message, retry option

## 12. Related Use Cases

- **UC-001**: User Registration
- **UC-003**: User Profile Management
- **UC-004**: Password Reset
- **UC-019**: View Learning Statistics

## 13. Notes

### Implementation Notes
- Sử dụng Spring Security cho authentication
- Implement JWT token generation
- Add rate limiting cho login attempts
- Log all login attempts for security

### Future Enhancements
- Two-factor authentication
- Social login (Google, Facebook)
- Biometric authentication
- Remember me functionality
