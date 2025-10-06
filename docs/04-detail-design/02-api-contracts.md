# API Contracts - RepeatWise

## 1. Overview

API Contracts định nghĩa chi tiết các endpoints, request/response schemas, error handling và authentication cho RepeatWise API. Tài liệu này cung cấp thông tin đầy đủ để developer implement và QA test API.

## 2. Base Configuration

### 2.1 Base URL
- **Production**: `https://api.repeatwise.com/api/v1`
- **Staging**: `https://staging-api.repeatwise.com/api/v1`
- **Development**: `http://localhost:8080/api/v1`

### 2.2 Authentication
- **Type**: JWT Bearer Token
- **Header**: `Authorization: Bearer <token>`
- **Token Expiry**: 1 hour
- **Refresh Token**: 30 days

### 2.3 Rate Limiting
- **Authenticated Users**: 100 requests/minute
- **Unauthenticated Users**: 10 requests/minute
- **Headers**: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`

### 2.4 Content Type
- **Request**: `application/json`
- **Response**: `application/json`
- **Charset**: `UTF-8`

## 3. Common Response Formats

### 3.1 Success Response
```json
{
  "success": true,
  "data": <response_data>,
  "message": "Operation completed successfully",
  "timestamp": "2024-12-19T10:30:00Z",
  "path": "/api/v1/endpoint"
}
```

### 3.2 Error Response
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Email format is invalid"
      }
    ]
  },
  "timestamp": "2024-12-19T10:30:00Z",
  "path": "/api/v1/endpoint"
}
```

### 3.3 Pagination Response
```json
{
  "success": true,
  "data": {
    "content": [<array_of_items>],
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
    "first": true,
    "numberOfElements": 20,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "empty": false
  }
}
```

## 4. Authentication Endpoints

### 4.1 POST /auth/register

#### 4.1.1 Request
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "preferredLanguage": "VI",
  "timezone": "Asia/Ho_Chi_Minh",
  "defaultReminderTime": "09:00"
}
```

#### 4.1.2 Response (201 Created)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "preferredLanguage": "VI",
    "timezone": "Asia/Ho_Chi_Minh",
    "defaultReminderTime": "09:00",
    "status": "active",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T10:30:00Z"
  },
  "message": "User registered successfully"
}
```

#### 4.1.3 Error Responses
- **400 Bad Request**: Validation errors
- **409 Conflict**: Email already exists
- **422 Unprocessable Entity**: Business rule violations

### 4.2 POST /auth/login

#### 4.2.1 Request
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### 4.2.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "fullName": "Nguyễn Văn A",
      "preferredLanguage": "VI",
      "timezone": "Asia/Ho_Chi_Minh",
      "defaultReminderTime": "09:00",
      "status": "active"
    }
  },
  "message": "Login successful"
}
```

#### 4.2.3 Error Responses
- **401 Unauthorized**: Invalid credentials
- **400 Bad Request**: Validation errors
- **423 Locked**: Account locked

### 4.3 POST /auth/refresh

#### 4.3.1 Request
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 4.3.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "message": "Token refreshed successfully"
}
```

#### 4.3.3 Error Responses
- **401 Unauthorized**: Invalid refresh token
- **400 Bad Request**: Validation errors

## 5. User Management Endpoints

### 5.1 GET /users/profile

#### 5.1.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Parameters**: None

#### 5.1.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "preferredLanguage": "VI",
    "timezone": "Asia/Ho_Chi_Minh",
    "defaultReminderTime": "09:00",
    "status": "active",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T10:30:00Z"
  }
}
```

#### 5.1.3 Error Responses
- **401 Unauthorized**: Invalid or expired token
- **404 Not Found**: User not found

### 5.2 PUT /users/profile

#### 5.2.1 Request
```json
{
  "fullName": "Nguyễn Văn B",
  "preferredLanguage": "EN",
  "timezone": "UTC",
  "defaultReminderTime": "10:00"
}
```

#### 5.2.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn B",
    "preferredLanguage": "EN",
    "timezone": "UTC",
    "defaultReminderTime": "10:00",
    "status": "active",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T11:00:00Z"
  },
  "message": "Profile updated successfully"
}
```

#### 5.2.3 Error Responses
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid token
- **404 Not Found**: User not found

## 6. Learning Sets Endpoints

### 6.1 GET /learning-sets

#### 6.1.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 20, max: 100)
  - `sort`: Sort field (default: "createdAt,desc")
  - `status`: Filter by status (active, paused, completed)

#### 6.1.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440001",
        "name": "Set học từ vựng",
        "description": "Set học từ vựng tiếng Anh",
        "status": "active",
        "createdAt": "2024-12-19T10:30:00Z",
        "updatedAt": "2024-12-19T10:30:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "empty": false
  }
}
```

#### 6.1.3 Error Responses
- **401 Unauthorized**: Invalid token
- **400 Bad Request**: Invalid query parameters

### 6.2 POST /learning-sets

#### 6.2.1 Request
```json
{
  "name": "Set học từ vựng",
  "description": "Set học từ vựng tiếng Anh"
}
```

#### 6.2.2 Response (201 Created)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Set học từ vựng",
    "description": "Set học từ vựng tiếng Anh",
    "status": "active",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T10:30:00Z"
  },
  "message": "Learning set created successfully"
}
```

#### 6.2.3 Error Responses
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid token
- **422 Unprocessable Entity**: Business rule violations

### 6.3 GET /learning-sets/{setId}

#### 6.3.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `setId` (UUID)

#### 6.3.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Set học từ vựng",
    "description": "Set học từ vựng tiếng Anh",
    "status": "active",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T10:30:00Z",
    "cycles": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "orderNumber": 1,
        "status": "pending",
        "scheduledAt": "2024-12-20T09:00:00Z",
        "completedAt": null,
        "score": null
      }
    ],
    "statistics": {
      "setId": "550e8400-e29b-41d4-a716-446655440001",
      "totalCycles": 5,
      "completedCycles": 0,
      "averageScore": null,
      "bestScore": null,
      "worstScore": null,
      "studyTime": 0
    }
  }
}
```

#### 6.3.3 Error Responses
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Set not found
- **403 Forbidden**: Access denied

### 6.4 PUT /learning-sets/{setId}

#### 6.4.1 Request
```json
{
  "name": "Set học từ vựng cập nhật",
  "description": "Set học từ vựng tiếng Anh cập nhật",
  "status": "paused"
}
```

#### 6.4.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Set học từ vựng cập nhật",
    "description": "Set học từ vựng tiếng Anh cập nhật",
    "status": "paused",
    "createdAt": "2024-12-19T10:30:00Z",
    "updatedAt": "2024-12-19T11:00:00Z"
  },
  "message": "Learning set updated successfully"
}
```

#### 6.4.3 Error Responses
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Set not found
- **403 Forbidden**: Access denied

### 6.5 DELETE /learning-sets/{setId}

#### 6.5.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `setId` (UUID)

#### 6.5.2 Response (204 No Content)
- **Body**: Empty

#### 6.5.3 Error Responses
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Set not found
- **403 Forbidden**: Access denied

## 7. Learning Cycles Endpoints

### 7.1 GET /learning-sets/{setId}/cycles

#### 7.1.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `setId` (UUID)
- **Query Parameters**:
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 20, max: 100)

#### 7.1.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440002",
        "orderNumber": 1,
        "status": "pending",
        "scheduledAt": "2024-12-20T09:00:00Z",
        "completedAt": null,
        "score": null
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "empty": false
  }
}
```

#### 7.1.3 Error Responses
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Set not found
- **403 Forbidden**: Access denied

### 7.2 GET /learning-sets/{setId}/cycles/{cycleId}

#### 7.2.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: 
  - `setId` (UUID)
  - `cycleId` (UUID)

#### 7.2.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "orderNumber": 1,
    "status": "pending",
    "scheduledAt": "2024-12-20T09:00:00Z",
    "completedAt": null,
    "score": null,
    "set": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Set học từ vựng",
      "description": "Set học từ vựng tiếng Anh",
      "status": "active"
    }
  }
}
```

#### 7.2.3 Error Responses
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Cycle not found
- **403 Forbidden**: Access denied

### 7.3 PUT /learning-sets/{setId}/cycles/{cycleId}

#### 7.3.1 Request
```json
{
  "status": "completed",
  "score": 85
}
```

#### 7.3.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "orderNumber": 1,
    "status": "completed",
    "scheduledAt": "2024-12-20T09:00:00Z",
    "completedAt": "2024-12-20T09:30:00Z",
    "score": 85
  },
  "message": "Cycle updated successfully"
}
```

#### 7.3.3 Error Responses
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Cycle not found
- **403 Forbidden**: Access denied

## 8. Statistics Endpoints

### 8.1 GET /statistics

#### 8.1.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `period`: Time period (week, month, year, all, default: month)

#### 8.1.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "totalSets": 10,
    "activeSets": 8,
    "completedSets": 2,
    "totalCycles": 50,
    "completedCycles": 30,
    "averageScore": 75.5,
    "studyStreak": 7,
    "period": "month"
  }
}
```

#### 8.1.3 Error Responses
- **401 Unauthorized**: Invalid token
- **400 Bad Request**: Invalid period parameter

### 8.2 GET /statistics/learning-sets/{setId}

#### 8.2.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `setId` (UUID)

#### 8.2.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "setId": "550e8400-e29b-41d4-a716-446655440001",
    "totalCycles": 5,
    "completedCycles": 3,
    "averageScore": 80.0,
    "bestScore": 95,
    "worstScore": 65,
    "studyTime": 120
  }
}
```

#### 8.2.3 Error Responses
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Set not found
- **403 Forbidden**: Access denied

## 9. Reminders Endpoints

### 9.1 GET /reminders

#### 9.1.1 Request
- **Headers**: `Authorization: Bearer <token>`
- **Query Parameters**:
  - `status`: Filter by status (pending, completed, skipped)
  - `page`: Page number (default: 0)
  - `size`: Page size (default: 20, max: 100)

#### 9.1.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "550e8400-e29b-41d4-a716-446655440003",
        "set": {
          "id": "550e8400-e29b-41d4-a716-446655440001",
          "name": "Set học từ vựng",
          "description": "Set học từ vựng tiếng Anh",
          "status": "active"
        },
        "cycle": {
          "id": "550e8400-e29b-41d4-a716-446655440002",
          "orderNumber": 1,
          "status": "pending",
          "scheduledAt": "2024-12-20T09:00:00Z",
          "completedAt": null,
          "score": null
        },
        "scheduledAt": "2024-12-20T09:00:00Z",
        "status": "pending",
        "type": "push"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "last": true,
    "first": true,
    "numberOfElements": 1,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false
    },
    "empty": false
  }
}
```

#### 9.1.3 Error Responses
- **401 Unauthorized**: Invalid token
- **400 Bad Request**: Invalid query parameters

### 9.2 POST /reminders/{reminderId}/reschedule

#### 9.2.1 Request
```json
{
  "scheduledAt": "2024-12-21T10:00:00Z"
}
```

#### 9.2.2 Response (200 OK)
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "set": {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Set học từ vựng",
      "description": "Set học từ vựng tiếng Anh",
      "status": "active"
    },
    "cycle": {
      "id": "550e8400-e29b-41d4-a716-446655440002",
      "orderNumber": 1,
      "status": "pending",
      "scheduledAt": "2024-12-21T10:00:00Z",
      "completedAt": null,
      "score": null
    },
    "scheduledAt": "2024-12-21T10:00:00Z",
    "status": "pending",
    "type": "push"
  },
  "message": "Reminder rescheduled successfully"
}
```

#### 9.2.3 Error Responses
- **400 Bad Request**: Validation errors
- **401 Unauthorized**: Invalid token
- **404 Not Found**: Reminder not found
- **403 Forbidden**: Access denied

## 10. Error Codes

### 10.1 HTTP Status Codes
- **200 OK**: Success
- **201 Created**: Resource created
- **204 No Content**: Success, no content
- **400 Bad Request**: Client error
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict
- **422 Unprocessable Entity**: Business rule violation
- **429 Too Many Requests**: Rate limit exceeded
- **500 Internal Server Error**: Server error

### 10.2 Error Codes
- **VALIDATION_ERROR**: Input validation failed
- **AUTHENTICATION_ERROR**: Authentication failed
- **AUTHORIZATION_ERROR**: Access denied
- **RESOURCE_NOT_FOUND**: Resource not found
- **RESOURCE_CONFLICT**: Resource conflict
- **BUSINESS_RULE_VIOLATION**: Business rule violated
- **RATE_LIMIT_EXCEEDED**: Rate limit exceeded
- **INTERNAL_SERVER_ERROR**: Internal server error

### 10.3 Field Validation Errors
- **REQUIRED_FIELD**: Field is required
- **INVALID_FORMAT**: Field format is invalid
- **INVALID_LENGTH**: Field length is invalid
- **INVALID_RANGE**: Field value is out of range
- **DUPLICATE_VALUE**: Value already exists
- **INVALID_CHOICE**: Invalid enum value

## 11. Request/Response Examples

### 11.1 Complete Registration Flow
```bash
# 1. Register user
POST /api/v1/auth/register
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "preferredLanguage": "VI",
  "timezone": "Asia/Ho_Chi_Minh",
  "defaultReminderTime": "09:00"
}

# 2. Login
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}

# 3. Create learning set
POST /api/v1/learning-sets
Authorization: Bearer <token>
{
  "name": "Set học từ vựng",
  "description": "Set học từ vựng tiếng Anh"
}

# 4. Get learning sets
GET /api/v1/learning-sets
Authorization: Bearer <token>
```

### 11.2 Learning Cycle Flow
```bash
# 1. Get learning set details
GET /api/v1/learning-sets/{setId}
Authorization: Bearer <token>

# 2. Get cycles
GET /api/v1/learning-sets/{setId}/cycles
Authorization: Bearer <token>

# 3. Complete cycle
PUT /api/v1/learning-sets/{setId}/cycles/{cycleId}
Authorization: Bearer <token>
{
  "status": "completed",
  "score": 85
}

# 4. Get statistics
GET /api/v1/statistics/learning-sets/{setId}
Authorization: Bearer <token>
```

## 12. Testing Guidelines

### 12.1 Test Data
- **Valid Users**: Use test email addresses
- **Valid Sets**: Create test learning sets
- **Valid Cycles**: Create test learning cycles
- **Edge Cases**: Test boundary values

### 12.2 Test Scenarios
- **Happy Path**: Normal successful flows
- **Error Cases**: Invalid inputs, missing data
- **Edge Cases**: Boundary values, limits
- **Security**: Authentication, authorization
- **Performance**: Load testing, rate limiting

### 12.3 Test Tools
- **Postman**: API testing
- **curl**: Command line testing
- **Jest**: Unit testing
- **LoadRunner**: Performance testing

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: API Architect  
**Stakeholders**: Development Team, QA Team, Frontend Team
