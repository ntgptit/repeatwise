# US-002: User Login

## 1. User Story

**As a** Student (Người học)
**I want to** đăng nhập vào hệ thống RepeatWise bằng email và password
**So that** tôi có thể truy cập vào tài khoản và bắt đầu học tập

## 2. Business Value

- **User Value**: Cho phép user truy cập nhanh chóng và an toàn vào tài khoản cá nhân
- **Business Value**: Tăng tỷ lệ user retention và engagement, đảm bảo tính bảo mật hệ thống

## 3. Acceptance Criteria

### AC-001: Successful Login
**Given** tôi có tài khoản hợp lệ trong hệ thống
**When** tôi nhập email và password đúng
**And** tôi nhấn nút "Đăng nhập"
**Then** tôi được đăng nhập thành công
**And** hệ thống tạo JWT token
**And** tôi được chuyển đến dashboard
**And** thông tin tài khoản được load

### AC-002: Invalid Email Format
**Given** tôi nhập email không đúng định dạng
**When** tôi nhấn nút "Đăng nhập"
**Then** hệ thống hiển thị thông báo lỗi: "Email không đúng định dạng"
**And** trường email được highlight màu đỏ
**And** form không được submit

### AC-003: Email Not Found
**Given** tôi nhập email không tồn tại trong hệ thống
**When** tôi nhấn nút "Đăng nhập"
**Then** hệ thống hiển thị thông báo lỗi: "Email hoặc mật khẩu không đúng"
**And** form không được submit
**And** tôi có thể chọn "Đăng ký" nếu chưa có tài khoản

### AC-004: Wrong Password
**Given** tôi nhập email đúng nhưng password sai
**When** tôi nhấn nút "Đăng nhập"
**Then** hệ thống hiển thị thông báo lỗi: "Email hoặc mật khẩu không đúng"
**And** trường password được clear
**And** form không được submit

### AC-005: Account Locked
**Given** tài khoản của tôi đã bị khóa do nhiều lần đăng nhập sai
**When** tôi thử đăng nhập
**Then** hệ thống hiển thị thông báo: "Tài khoản đã bị khóa do nhiều lần đăng nhập sai"
**And** hiển thị thời gian unlock
**And** tôi không thể đăng nhập trong 30 phút

### AC-006: Account Suspended
**Given** tài khoản của tôi đã bị tạm khóa bởi admin
**When** tôi thử đăng nhập
**Then** hệ thống hiển thị thông báo: "Tài khoản đã bị tạm khóa"
**And** hiển thị lý do suspension
**And** tôi được hướng dẫn liên hệ support

### AC-007: Empty Fields
**Given** tôi để trống email hoặc password
**When** tôi nhấn nút "Đăng nhập"
**Then** hệ thống hiển thị thông báo: "Vui lòng nhập đầy đủ thông tin"
**And** trường bị trống được highlight
**And** form không được submit

### AC-008: Network Error
**Given** mất kết nối internet
**When** tôi thử đăng nhập
**Then** hệ thống hiển thị thông báo: "Lỗi kết nối, vui lòng thử lại"
**And** tôi có thể thử lại khi có kết nối

### AC-009: Remember Email
**Given** tôi đã chọn "Ghi nhớ email"
**When** tôi quay lại trang đăng nhập
**Then** email của tôi được tự động điền
**And** tôi chỉ cần nhập password

### AC-010: Forgot Password Link
**Given** tôi quên password
**When** tôi nhấn "Quên mật khẩu"
**Then** tôi được chuyển đến trang reset password
**And** tôi có thể nhập email để nhận link reset

### AC-011: Login Rate Limiting
**Given** tôi đã thử đăng nhập sai 5 lần trong 15 phút
**When** tôi thử đăng nhập lần nữa
**Then** hệ thống hiển thị thông báo: "Quá nhiều lần thử đăng nhập sai"
**And** tôi phải chờ 15 phút mới được thử lại

### AC-012: Session Management
**Given** tôi đã đăng nhập thành công
**When** tôi không hoạt động trong 1 giờ
**Then** hệ thống tự động logout
**And** tôi phải đăng nhập lại

## 4. Definition of Ready (DoR)

- [ ] UI/UX design đã được approve
- [ ] API endpoints đã được định nghĩa
- [ ] Database schema đã được thiết kế
- [ ] Security requirements đã được xác định
- [ ] Test cases đã được viết
- [ ] Error handling scenarios đã được xác định
- [ ] Performance requirements đã được định nghĩa

## 5. Definition of Done (DoD)

- [ ] User có thể đăng nhập thành công với email và password hợp lệ
- [ ] Tất cả validation rules được implement
- [ ] Error messages được hiển thị rõ ràng và user-friendly
- [ ] JWT token được tạo và lưu trữ an toàn
- [ ] Rate limiting được implement
- [ ] Account lockout mechanism hoạt động
- [ ] Session timeout được implement
- [ ] Unit tests đạt coverage > 90%
- [ ] Integration tests pass
- [ ] Security tests pass
- [ ] Performance tests đạt yêu cầu
- [ ] Code review được approve
- [ ] Documentation được cập nhật

## 6. Story Points

**Story Points**: 8

**Lý do**: 
- Medium complexity do cần implement nhiều validation rules
- Security requirements phức tạp (JWT, rate limiting, lockout)
- Error handling scenarios nhiều
- Integration với authentication system

## 7. Dependencies

- **Technical Dependencies**:
  - Authentication service
  - JWT token service
  - User management system
  - Database schema
  - Security framework

- **Business Dependencies**:
  - User registration system
  - Password reset functionality
  - Admin account management

## 8. Risk Assessment

### High Risk
- **Security vulnerabilities**: JWT implementation, password storage
- **Mitigation**: Code review, security testing, follow OWASP guidelines

### Medium Risk
- **Performance issues**: Database queries, token validation
- **Mitigation**: Performance testing, caching, optimization

### Low Risk
- **UI/UX issues**: Form validation, error messages
- **Mitigation**: User testing, iterative design

## 9. Test Scenarios

### Happy Path
1. User nhập email và password đúng → Login thành công
2. User chọn "Ghi nhớ email" → Email được lưu
3. User sử dụng "Quên mật khẩu" → Chuyển đến reset page

### Error Scenarios
1. User nhập email sai format → Validation error
2. User nhập email không tồn tại → Generic error message
3. User nhập password sai → Generic error message
4. User thử đăng nhập quá nhiều lần → Account lockout
5. User đăng nhập với account suspended → Suspension message
6. Mất kết nối internet → Network error message

### Edge Cases
1. User để trống fields → Required field validation
2. User copy-paste email với spaces → Trim whitespace
3. User sử dụng special characters → Input sanitization
4. User đăng nhập từ multiple devices → Session management

## 10. Success Metrics

### Functional Metrics
- **Login Success Rate**: > 95%
- **Error Rate**: < 5%
- **Account Lockout Rate**: < 1%

### Performance Metrics
- **Login Response Time**: < 3 giây
- **Token Generation Time**: < 1 giây
- **Form Validation Time**: < 500ms

### Security Metrics
- **Failed Login Attempts**: Tracked and logged
- **Account Lockouts**: Monitored
- **Suspicious Activity**: Detected and flagged

### User Experience Metrics
- **Time to Login**: < 30 giây
- **Error Message Clarity**: User feedback score > 4/5
- **Support Tickets**: < 2% related to login issues
