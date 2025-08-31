# UC-004: Password Reset

## 1. Use Case Information

**Use Case ID**: UC-004
**Use Case Name**: Password Reset
**Primary Actor**: Student (Người học)
**Secondary Actors**: Email Service
**Priority**: High
**Complexity**: Medium

## 2. Brief Description

Student yêu cầu reset password khi quên mật khẩu. Hệ thống gửi email với link reset, user tạo password mới và xác nhận thay đổi.

## 3. Preconditions

- Student có tài khoản trong hệ thống
- Email service hoạt động bình thường
- User account không bị suspended

## 4. Main Flow

### Step 1: Access Password Reset
**Actor Action**: Student chọn "Quên mật khẩu" từ trang đăng nhập
**System Response**: Hiển thị form nhập email để reset password

### Step 2: Input Email Address
**Actor Action**: Student nhập email address đã đăng ký
**System Response**: Validate email format và hiển thị feedback

### Step 3: Submit Reset Request
**Actor Action**: Student nhấn "Gửi link reset"
**System Response**:
- Validate email tồn tại trong hệ thống
- Generate reset token
- Tạo reset link với expiry time
- Gửi email với reset link

### Step 4: Receive Reset Email
**Actor Action**: Student kiểm tra email và click vào reset link
**System Response**: Validate reset token và hiển thị form tạo password mới

### Step 5: Create New Password
**Actor Action**: Student nhập:
- Password mới (8-20 ký tự)
- Confirm password
**System Response**: Validate password strength và match

### Step 6: Submit New Password
**Actor Action**: Student nhấn "Đặt lại mật khẩu"
**System Response**:
- Validate password requirements
- Update password với BCrypt hash
- Invalidate reset token
- Log password change

### Step 7: Complete Password Reset
**Actor Action**: System tự động
**System Response**:
- Hiển thị thông báo thành công
- Chuyển user đến trang đăng nhập
- Gửi email xác nhận thay đổi

## 5. Alternative Flows

### A1: Email Not Found
**Trigger**: Email không tồn tại trong hệ thống
**Steps**:
1. System hiển thị thông báo: "Email không tồn tại trong hệ thống"
2. Student có thể:
   - Nhập email khác
   - Chọn "Đăng ký" nếu chưa có tài khoản
3. Return to Step 2

### A2: Invalid Email Format
**Trigger**: Email không đúng định dạng
**Steps**:
1. System hiển thị thông báo lỗi: "Email không đúng định dạng"
2. System highlight trường email
3. Student nhập lại email
4. Return to Step 2

### A3: Reset Token Expired
**Trigger**: Reset link đã hết hạn
**Steps**:
1. System hiển thị thông báo: "Link reset đã hết hạn"
2. System hiển thị tùy chọn gửi lại link mới
3. Student có thể yêu cầu link mới
4. Return to Step 1

### A4: Invalid Reset Token
**Trigger**: Reset token không hợp lệ hoặc đã được sử dụng
**Steps**:
1. System hiển thị thông báo: "Link reset không hợp lệ"
2. System hiển thị tùy chọn gửi lại link mới
3. Student có thể yêu cầu link mới
4. Return to Step 1

### A5: Weak Password
**Trigger**: Password mới không đủ mạnh
**Steps**:
1. System hiển thị thông báo lỗi: "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số"
2. System highlight trường password
3. Student nhập lại password mới
4. Return to Step 5

### A6: Password Mismatch
**Trigger**: Confirm password không khớp
**Steps**:
1. System hiển thị thông báo lỗi: "Mật khẩu xác nhận không khớp"
2. System highlight trường confirm password
3. Student nhập lại confirm password
4. Return to Step 5

### A7: Email Service Error
**Trigger**: Không thể gửi email
**Steps**:
1. System hiển thị thông báo: "Không thể gửi email, vui lòng thử lại sau"
2. System log error cho admin
3. Student có thể thử lại sau
4. Return to Step 1

### A8: Account Suspended
**Trigger**: Tài khoản bị suspended
**Steps**:
1. System hiển thị thông báo: "Tài khoản đã bị tạm khóa, không thể reset password"
2. System hiển thị lý do suspension
3. Student có thể liên hệ support
4. Return to Step 1

## 6. Post Conditions

### Success Post Conditions
- Password được thay đổi thành công
- Reset token được invalidate
- Email xác nhận được gửi
- User có thể đăng nhập với password mới

### Failure Post Conditions
- Password không được thay đổi
- Error message được hiển thị
- User có thể thử lại hoặc liên hệ support

## 7. Business Rules

### BR-020: User Authentication
- User phải có email hợp lệ trong hệ thống
- Password phải được mã hóa với BCrypt
- Reset token có expiry time = 1 giờ

### BR-021: Password Security
- Password phải có ít nhất 8 ký tự
- Password phải bao gồm chữ hoa, chữ thường, số
- Không được sử dụng password cũ
- Reset token chỉ được sử dụng 1 lần

### BR-022: Email Security
- Reset link chỉ hợp lệ trong 1 giờ
- Mỗi email chỉ có 1 reset token active
- Log tất cả reset attempts

### BR-016: Audit Fields
- Bắt buộc có created_at, updated_at
- updated_at = now() khi thay đổi password
- Lưu lịch sử password changes

## 8. Data Requirements

### Input Data
- **Email** (string, required, email format)
- **New Password** (string, required, 8-20 chars)
- **Confirm Password** (string, required)
- **Reset Token** (string, required, from email link)

### Output Data
- **Reset Status** (success/error)
- **Reset Token** (generated for email)
- **Error Messages** (if any)
- **Email Content** (reset link, instructions)

## 9. Non-Functional Requirements

### Performance
- Reset request processing < 3 giây
- Email sending < 30 giây
- Password update < 2 giây

### Security
- Reset token có expiry time
- Password được hash với BCrypt
- Rate limiting cho reset requests
- Log all reset activities

### Usability
- Clear instructions trong email
- Password strength indicator
- Real-time validation feedback
- Mobile-friendly reset link

## 10. Acceptance Criteria

### AC-001: Successful Password Reset
**Given** user có email hợp lệ
**When** user yêu cầu reset password
**Then** reset email được gửi
**And** user có thể tạo password mới
**And** password được thay đổi thành công

### AC-002: Invalid Email
**Given** user nhập email không tồn tại
**When** user submit reset request
**Then** system hiển thị thông báo email không tồn tại
**And** reset email không được gửi

### AC-003: Expired Reset Token
**Given** reset link đã hết hạn
**When** user click vào link
**Then** system hiển thị thông báo link hết hạn
**And** user được yêu cầu tạo link mới

### AC-004: Weak Password
**Given** user nhập password không đủ mạnh
**When** user submit new password
**Then** system hiển thị thông báo lỗi
**And** password không được thay đổi

### AC-005: Password Mismatch
**Given** confirm password không khớp
**When** user submit new password
**Then** system hiển thị thông báo lỗi
**And** password không được thay đổi

## 11. Test Cases

### TC-001: Valid Password Reset
**Test Data**: Valid email, strong password
**Expected Result**: Reset email sent, password changed successfully

### TC-002: Email Not Found
**Test Data**: Non-existent email
**Expected Result**: Error message, no email sent

### TC-003: Invalid Email Format
**Test Data**: Invalid email format
**Expected Result**: Validation error, no email sent

### TC-004: Expired Token
**Test Data**: Expired reset token
**Expected Result**: Token expired message, request new link

### TC-005: Weak Password
**Test Data**: Password < 8 characters
**Expected Result**: Password strength error, password not changed

### TC-006: Email Service Down
**Test Data**: Simulate email service failure
**Expected Result**: Service error message, retry option

## 12. Related Use Cases

- **UC-001**: User Registration
- **UC-002**: User Login
- **UC-003**: User Profile Management
- **UC-023**: Manage System Configuration

## 13. Notes

### Implementation Notes
- Generate secure random reset token
- Implement rate limiting (max 3 requests/hour per email)
- Use BCrypt với salt rounds = 12
- Log all reset attempts for security monitoring

### Future Enhancements
- SMS-based password reset
- Two-factor authentication
- Password history tracking
- Security questions

### UI/UX Considerations
- Clear, step-by-step instructions
- Password strength meter
- Mobile-responsive design
- Clear error messages
- Progress indicators
