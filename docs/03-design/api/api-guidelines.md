# API Guidelines

## 1. Tổng quan

### 1.1 Mục đích
Tài liệu này định nghĩa các nguyên tắc và quy ước thiết kế API cho hệ thống RepeatWise, đảm bảo tính nhất quán, dễ sử dụng và bảo trì.

### 1.2 Phạm vi áp dụng
- REST API cho mobile application
- Spring Boot backend services
- Tất cả các endpoint trong hệ thống

## 2. Nguyên tắc thiết kế

### 2.1 RESTful Design
- Tuân thủ nguyên tắc REST
- Sử dụng HTTP methods đúng mục đích:
  - `GET`: Lấy dữ liệu
  - `POST`: Tạo mới
  - `PUT`: Cập nhật toàn bộ resource
  - `PATCH`: Cập nhật một phần resource
  - `DELETE`: Xóa resource

### 2.2 URL Design
```
/api/v1/{resource}/{id}/{sub-resource}
```

**Quy ước đặt tên:**
- Sử dụng kebab-case cho URL paths
- Sử dụng camelCase cho query parameters
- Sử dụng snake_case cho response fields

**Ví dụ:**
```
GET /api/v1/learning-sets
GET /api/v1/learning-sets/{set-id}/cycles
GET /api/v1/users/{user-id}/statistics
```

### 2.3 HTTP Status Codes
- `200 OK`: Thành công
- `201 Created`: Tạo mới thành công
- `204 No Content`: Thành công, không có content
- `400 Bad Request`: Dữ liệu đầu vào không hợp lệ
- `401 Unauthorized`: Chưa xác thực
- `403 Forbidden`: Không có quyền truy cập
- `404 Not Found`: Resource không tồn tại
- `409 Conflict`: Xung đột dữ liệu
- `422 Unprocessable Entity`: Dữ liệu hợp lệ nhưng không thể xử lý
- `500 Internal Server Error`: Lỗi server

## 3. Request/Response Format

### 3.1 Request Headers
```
Content-Type: application/json
Authorization: Bearer {jwt-token}
Accept: application/json
Accept-Language: vi-VN
```

### 3.2 Response Format
```json
{
  "success": true,
  "data": {
    // Response data
  },
  "message": "Thành công",
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/v1/learning-sets"
}
```

### 3.3 Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Dữ liệu không hợp lệ",
    "details": [
      {
        "field": "email",
        "message": "Email không đúng định dạng"
      }
    ]
  },
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/v1/users"
}
```

## 4. Authentication & Authorization

### 4.1 JWT Authentication
- Sử dụng JWT token cho authentication
- Token có thời hạn 24 giờ
- Refresh token có thời hạn 7 ngày

### 4.2 Authorization
- Role-based access control (RBAC)
- User chỉ có thể truy cập dữ liệu của mình
- Admin có quyền truy cập toàn bộ hệ thống

## 5. Validation Rules

### 5.1 Input Validation
- Validate tất cả input từ client
- Sử dụng Bean Validation annotations
- Trả về lỗi chi tiết cho từng field

### 5.2 Business Rules Validation
- Kiểm tra business rules trước khi xử lý
- Validate cross-field dependencies
- Kiểm tra quyền truy cập

## 6. Pagination & Filtering

### 6.1 Pagination
```
GET /api/v1/learning-sets?page=0&size=20&sort=createdAt,desc
```

**Response:**
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true
}
```

### 6.2 Filtering
```
GET /api/v1/learning-sets?status=active&createdAfter=2024-01-01
```

## 7. Rate Limiting

### 7.1 Limits
- 100 requests/minute cho authenticated users
- 10 requests/minute cho unauthenticated users
- 1000 requests/hour cho mỗi user

### 7.2 Headers
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1640995200
```

## 8. Caching

### 8.1 Cache Headers
```
Cache-Control: max-age=3600
ETag: "abc123"
Last-Modified: Wed, 01 Jan 2024 00:00:00 GMT
```

### 8.2 Cacheable Resources
- GET requests cho static data
- User profile information
- System configuration

## 9. Logging & Monitoring

### 9.1 Request Logging
- Log tất cả API requests
- Include user ID, IP address, user agent
- Log response time và status code

### 9.2 Error Logging
- Log chi tiết lỗi với stack trace
- Include request context
- Alert cho critical errors

## 10. Versioning

### 10.1 URL Versioning
```
/api/v1/learning-sets
/api/v2/learning-sets
```

### 10.2 Backward Compatibility
- Maintain backward compatibility trong 6 tháng
- Deprecation notice trước khi breaking changes
- Migration guide cho major versions

## 11. Documentation

### 11.1 OpenAPI Specification
- Sử dụng OpenAPI 3.0
- Auto-generate từ code annotations
- Include examples và error responses

### 11.2 API Documentation
- Swagger UI cho interactive documentation
- Postman collection cho testing
- SDK examples cho mobile app

## 12. Security

### 12.1 Data Protection
- Encrypt sensitive data
- Use HTTPS cho tất cả communications
- Implement proper CORS policy

### 12.2 Input Sanitization
- Sanitize tất cả user input
- Prevent SQL injection
- Validate file uploads

## 13. Testing

### 13.1 Unit Tests
- Test tất cả service methods
- Mock external dependencies
- Achieve 80% code coverage

### 13.2 Integration Tests
- Test API endpoints
- Test database operations
- Test authentication flows

### 13.3 Performance Tests
- Load testing cho critical endpoints
- Stress testing cho database operations
- Monitor response times 
