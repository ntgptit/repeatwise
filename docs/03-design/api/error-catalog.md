# Error Catalog

## 1. Tổng quan

### 1.1 Mục đích
Tài liệu này định nghĩa tất cả các mã lỗi và thông báo lỗi được sử dụng trong API RepeatWise, đảm bảo tính nhất quán và dễ hiểu cho developers.

### 1.2 Cấu trúc lỗi
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Thông báo lỗi bằng tiếng Anh",
    "details": [
      {
        "field": "field_name",
        "message": "Chi tiết lỗi cho field"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/v1/endpoint"
}
```

## 2. Authentication Errors

### 2.1 AUTH_001 - Invalid Credentials
- **Code**: `AUTH_001`
- **Message**: "Email hoặc mật khẩu không đúng"
- **HTTP Status**: 401
- **Description**: Thông tin đăng nhập không chính xác

### 2.2 AUTH_002 - Token Expired
- **Code**: `AUTH_002`
- **Message**: "Phiên đăng nhập đã hết hạn"
- **HTTP Status**: 401
- **Description**: JWT token đã hết hạn

### 2.3 AUTH_003 - Invalid Token
- **Code**: `AUTH_003`
- **Message**: "Token không hợp lệ"
- **HTTP Status**: 401
- **Description**: JWT token không đúng định dạng hoặc bị giả mạo

### 2.4 AUTH_004 - Account Suspended
- **Code**: `AUTH_004`
- **Message**: "Tài khoản đã bị tạm khóa"
- **HTTP Status**: 403
- **Description**: Tài khoản user bị suspend

### 2.5 AUTH_005 - Insufficient Permissions
- **Code**: `AUTH_005`
- **Message**: "Không có quyền truy cập"
- **HTTP Status**: 403
- **Description**: User không có quyền thực hiện hành động

## 3. Validation Errors

### 3.1 VAL_001 - Required Field Missing
- **Code**: `VAL_001`
- **Message**: "Trường {field} là bắt buộc"
- **HTTP Status**: 400
- **Description**: Thiếu trường bắt buộc

### 3.2 VAL_002 - Invalid Email Format
- **Code**: `VAL_002`
- **Message**: "Email không đúng định dạng"
- **HTTP Status**: 400
- **Description**: Email không hợp lệ

### 3.3 VAL_003 - Password Too Weak
- **Code**: `VAL_003`
- **Message**: "Mật khẩu phải có ít nhất 8 ký tự"
- **HTTP Status**: 400
- **Description**: Mật khẩu không đủ mạnh

### 3.4 VAL_004 - Invalid Date Format
- **Code**: `VAL_004`
- **Message**: "Định dạng ngày không hợp lệ"
- **HTTP Status**: 400
- **Description**: Ngày tháng không đúng định dạng

### 3.5 VAL_005 - Invalid Score Range
- **Code**: `VAL_005`
- **Message**: "Điểm số phải từ 0 đến 100"
- **HTTP Status**: 400
- **Description**: Điểm số nằm ngoài phạm vi cho phép

### 3.6 VAL_006 - Invalid Set Name Length
- **Code**: `VAL_006`
- **Message**: "Tên set phải từ 1 đến 100 ký tự"
- **HTTP Status**: 400
- **Description**: Tên set quá ngắn hoặc quá dài

## 4. Business Logic Errors

### 4.1 BUS_001 - Set Not Found
- **Code**: `BUS_001`
- **Message**: "Không tìm thấy set học tập"
- **HTTP Status**: 404
- **Description**: Set ID không tồn tại

### 4.2 BUS_002 - Cycle Not Found
- **Code**: `BUS_002`
- **Message**: "Không tìm thấy chu kỳ học tập"
- **HTTP Status**: 404
- **Description**: Cycle ID không tồn tại

### 4.3 BUS_003 - User Not Found
- **Code**: `BUS_003`
- **Message**: "Không tìm thấy người dùng"
- **HTTP Status**: 404
- **Description**: User ID không tồn tại

### 4.4 BUS_004 - Set Already Exists
- **Code**: `BUS_004`
- **Message**: "Set học tập đã tồn tại"
- **HTTP Status**: 409
- **Description**: Tên set đã được sử dụng

### 4.5 BUS_005 - Cycle Already Completed
- **Code**: `BUS_005`
- **Message**: "Chu kỳ học tập đã hoàn thành"
- **HTTP Status**: 409
- **Description**: Không thể cập nhật cycle đã hoàn thành

### 4.6 BUS_006 - Maximum Sets Per Day Exceeded
- **Code**: `BUS_006`
- **Message**: "Đã đạt giới hạn 3 set/ngày"
- **HTTP Status**: 422
- **Description**: Vượt quá giới hạn set học tập trong ngày

### 4.7 BUS_007 - Invalid Cycle Order
- **Code**: `BUS_007`
- **Message**: "Thứ tự chu kỳ không hợp lệ"
- **HTTP Status**: 422
- **Description**: Không thể bỏ qua chu kỳ trước đó

### 4.8 BUS_008 - Reminder Already Scheduled
- **Code**: `BUS_008`
- **Message**: "Nhắc nhở đã được lên lịch"
- **HTTP Status**: 409
- **Description**: Reminder đã tồn tại cho thời gian này

## 5. Database Errors

### 5.1 DB_001 - Database Connection Failed
- **Code**: `DB_001`
- **Message**: "Lỗi kết nối cơ sở dữ liệu"
- **HTTP Status**: 500
- **Description**: Không thể kết nối database

### 5.2 DB_002 - Constraint Violation
- **Code**: `DB_002`
- **Message**: "Vi phạm ràng buộc dữ liệu"
- **HTTP Status**: 400
- **Description**: Vi phạm foreign key hoặc unique constraint

### 5.3 DB_003 - Transaction Failed
- **Code**: `DB_003`
- **Message**: "Giao dịch cơ sở dữ liệu thất bại"
- **HTTP Status**: 500
- **Description**: Rollback transaction

## 6. External Service Errors

### 6.1 EXT_001 - Notification Service Unavailable
- **Code**: `EXT_001`
- **Message**: "Dịch vụ thông báo không khả dụng"
- **HTTP Status**: 503
- **Description**: Không thể gửi notification

### 6.2 EXT_002 - Email Service Failed
- **Code**: `EXT_002`
- **Message**: "Không thể gửi email"
- **HTTP Status**: 503
- **Description**: Lỗi gửi email

### 6.3 EXT_003 - Push Notification Failed
- **Code**: `EXT_003`
- **Message**: "Không thể gửi push notification"
- **HTTP Status**: 503
- **Description**: Lỗi gửi push notification

## 7. Rate Limiting Errors

### 7.1 RATE_001 - Rate Limit Exceeded
- **Code**: `RATE_001`
- **Message**: "Vượt quá giới hạn truy cập"
- **HTTP Status**: 429
- **Description**: Quá nhiều requests trong thời gian ngắn

### 7.2 RATE_002 - Too Many Login Attempts
- **Code**: `RATE_002`
- **Message**: "Quá nhiều lần đăng nhập thất bại"
- **HTTP Status**: 429
- **Description**: Tài khoản bị tạm khóa do đăng nhập sai nhiều lần

## 8. System Errors

### 8.1 SYS_001 - Internal Server Error
- **Code**: `SYS_001`
- **Message**: "Lỗi hệ thống nội bộ"
- **HTTP Status**: 500
- **Description**: Lỗi không xác định trong hệ thống

### 8.2 SYS_002 - Service Unavailable
- **Code**: `SYS_002`
- **Message**: "Dịch vụ tạm thời không khả dụng"
- **HTTP Status**: 503
- **Description**: Hệ thống đang bảo trì

### 8.3 SYS_003 - Configuration Error
- **Code**: `SYS_003`
- **Message**: "Lỗi cấu hình hệ thống"
- **HTTP Status**: 500
- **Description**: Cấu hình hệ thống không hợp lệ

## 9. File Upload Errors

### 9.1 FILE_001 - File Too Large
- **Code**: `FILE_001`
- **Message**: "File quá lớn"
- **HTTP Status**: 400
- **Description**: File vượt quá kích thước cho phép

### 9.2 FILE_002 - Invalid File Type
- **Code**: `FILE_002`
- **Message**: "Loại file không được hỗ trợ"
- **HTTP Status**: 400
- **Description**: File type không được cho phép

### 9.3 FILE_003 - File Upload Failed
- **Code**: `FILE_003`
- **Message**: "Tải file lên thất bại"
- **HTTP Status**: 500
- **Description**: Lỗi trong quá trình upload file

## 10. Implementation Guidelines

### 10.1 Error Handling Strategy
1. **Validation First**: Validate input trước khi xử lý business logic
2. **Graceful Degradation**: Xử lý lỗi một cách graceful
3. **Detailed Logging**: Log chi tiết lỗi để debug
4. **User-Friendly Messages**: Thông báo lỗi dễ hiểu cho user

### 10.2 Error Response Headers
```
X-Error-Code: AUTH_001
X-Error-Message: Email hoặc mật khẩu không đúng
X-Request-ID: abc123-def456
```

### 10.3 Error Logging Format
```json
{
  "timestamp": "2024-01-01T00:00:00Z",
  "level": "ERROR",
  "code": "AUTH_001",
  "message": "Email hoặc mật khẩu không đúng",
  "user_id": "user-uuid",
  "ip_address": "192.168.1.1",
  "user_agent": "Mozilla/5.0...",
  "request_path": "/api/v1/auth/login",
  "request_method": "POST",
  "stack_trace": "..."
}
```

### 10.4 Error Recovery
1. **Retry Logic**: Tự động retry cho transient errors
2. **Circuit Breaker**: Ngăn chặn cascade failures
3. **Fallback Mechanisms**: Cung cấp alternative responses
4. **Monitoring**: Alert cho critical errors 
