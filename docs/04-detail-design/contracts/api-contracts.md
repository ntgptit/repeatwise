# API Contracts

## 1. Overview

API Contracts định nghĩa các interface và schema cho tất cả REST API endpoints của RepeatWise. Tài liệu này cung cấp chi tiết đầy đủ để developers có thể implement chính xác.

## 2. Base API Structure

### 2.1 Base URL
```
Development: http://localhost:8080/api/v1
Staging: https://staging-api.repeatwise.com/api/v1
Production: https://api.repeatwise.com/api/v1
```

### 2.2 Common Headers
```http
Content-Type: application/json
Authorization: Bearer {jwt_token}
Accept: application/json
X-Request-ID: {uuid}
```

### 2.3 Standard Response Format
```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully",
  "timestamp": "2024-01-15T10:30:00Z",
  "requestId": "uuid-here"
}
```

### 2.4 Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "email",
        "message": "Email is required"
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00Z",
  "requestId": "uuid-here"
}
```

## 3. Authentication APIs

### 3.1 User Registration
**Endpoint**: `POST /auth/register`

**Request**:
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "fullName": "Nguyễn Văn A",
  "preferredLanguage": "VI",
  "timezone": "Asia/Ho_Chi_Minh",
  "defaultReminderTime": "20:00"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "preferredLanguage": "VI",
    "timezone": "Asia/Ho_Chi_Minh",
    "defaultReminderTime": "20:00",
    "status": "ACTIVE",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "message": "User registered successfully"
}
```

**Validation Rules**:
- Email: Required, valid email format, unique
- Password: 8-20 characters, at least 1 uppercase, 1 lowercase, 1 number
- ConfirmPassword: Must match password
- FullName: Required, max 100 characters
- PreferredLanguage: VI or EN
- Timezone: Valid timezone identifier
- DefaultReminderTime: HH:mm format

### 3.2 User Login
**Endpoint**: `POST /auth/login`

**Request**:
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 1800000,
    "user": {
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "email": "user@example.com",
      "fullName": "Nguyễn Văn A",
      "preferredLanguage": "VI",
      "timezone": "Asia/Ho_Chi_Minh",
      "defaultReminderTime": "20:00"
    }
  },
  "message": "Login successful"
}
```

### 3.3 Refresh Token
**Endpoint**: `POST /auth/refresh`

**Request**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 1800000
  },
  "message": "Token refreshed successfully"
}
```

### 3.4 Logout
**Endpoint**: `POST /auth/logout`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

## 4. User Management APIs

### 4.1 Get User Profile
**Endpoint**: `GET /users/profile`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "preferredLanguage": "VI",
    "timezone": "Asia/Ho_Chi_Minh",
    "defaultReminderTime": "20:00",
    "avatarUrl": "https://example.com/avatar.jpg",
    "bio": "Learning enthusiast",
    "goals": [
      {
        "id": "goal-1",
        "description": "Learn 1000 new words",
        "targetDate": "2024-12-31",
        "progress": 65
      }
    ],
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-20T15:45:00Z"
  }
}
```

### 4.2 Update User Profile
**Endpoint**: `PUT /users/profile`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "fullName": "Nguyễn Văn B",
  "preferredLanguage": "EN",
  "timezone": "UTC",
  "defaultReminderTime": "19:00",
  "avatarUrl": "https://example.com/new-avatar.jpg",
  "bio": "Updated bio"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "fullName": "Nguyễn Văn B",
    "preferredLanguage": "EN",
    "timezone": "UTC",
    "defaultReminderTime": "19:00",
    "avatarUrl": "https://example.com/new-avatar.jpg",
    "bio": "Updated bio",
    "updatedAt": "2024-01-20T16:00:00Z"
  },
  "message": "Profile updated successfully"
}
```

### 4.3 Change Password
**Endpoint**: `PUT /users/password`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "currentPassword": "OldPass123!",
  "newPassword": "NewSecurePass456!",
  "confirmNewPassword": "NewSecurePass456!"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

## 5. Set Management APIs

### 5.1 Create New Set
**Endpoint**: `POST /sets`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "name": "Basic Vocabulary Set 1",
  "description": "Essential vocabulary for beginners",
  "category": "VOCABULARY",
  "wordCount": 50
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "name": "Basic Vocabulary Set 1",
    "description": "Essential vocabulary for beginners",
    "category": "VOCABULARY",
    "wordCount": 50,
    "status": "NOT_STARTED",
    "currentCycle": 1,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "message": "Set created successfully"
}
```

### 5.2 Get Sets List
**Endpoint**: `GET /sets`

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `status`: NOT_STARTED, LEARNING, REVIEWING, MASTERED (optional)
- `category`: VOCABULARY, GRAMMAR, MIXED, OTHER (optional)
- `page`: number (default: 0)
- `size`: number (default: 20, max: 100)

**Response**:
```json
{
  "success": true,
  "data": {
    "sets": [
      {
        "setId": "set-550e8400-e29b-41d4-a716-446655440000",
        "name": "Basic Vocabulary Set 1",
        "description": "Essential vocabulary for beginners",
        "category": "VOCABULARY",
        "wordCount": 50,
        "status": "LEARNING",
        "currentCycle": 2,
        "averageScore": 85.5,
        "lastReviewDate": "2024-01-19T15:30:00Z",
        "nextReviewDate": "2024-01-22T20:00:00Z",
        "createdAt": "2024-01-15T10:30:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 5,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

### 5.3 Get Set Details
**Endpoint**: `GET /sets/{setId}`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "name": "Basic Vocabulary Set 1",
    "description": "Essential vocabulary for beginners",
    "category": "VOCABULARY",
    "wordCount": 50,
    "status": "LEARNING",
    "currentCycle": 2,
    "averageScore": 85.5,
    "totalCycles": 1,
    "totalReviews": 5,
    "lastReviewDate": "2024-01-19T15:30:00Z",
    "nextReviewDate": "2024-01-22T20:00:00Z",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-19T15:30:00Z",
    "recentReviews": [
      {
        "reviewId": "review-1",
        "cycleNumber": 2,
        "reviewNumber": 1,
        "score": 90,
        "note": "Good progress",
        "createdAt": "2024-01-19T15:30:00Z"
      }
    ]
  }
}
```

### 5.4 Update Set
**Endpoint**: `PUT /sets/{setId}`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "name": "Updated Vocabulary Set",
  "description": "Updated description",
  "category": "MIXED"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "name": "Updated Vocabulary Set",
    "description": "Updated description",
    "category": "MIXED",
    "wordCount": 50,
    "status": "LEARNING",
    "currentCycle": 2,
    "updatedAt": "2024-01-20T16:00:00Z"
  },
  "message": "Set updated successfully"
}
```

### 5.5 Delete Set
**Endpoint**: `DELETE /sets/{setId}`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "message": "Set deleted successfully"
}
```

## 6. Learning Cycle APIs

### 6.1 Start Learning Cycle
**Endpoint**: `POST /sets/{setId}/start`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "cycleNumber": 1,
    "status": "LEARNING",
    "startDate": "2024-01-15T10:30:00Z",
    "schedule": [
      {
        "reviewNumber": 1,
        "scheduledDate": "2024-01-16T20:00:00Z",
        "status": "PENDING"
      },
      {
        "reviewNumber": 2,
        "scheduledDate": "2024-01-18T20:00:00Z",
        "status": "PENDING"
      },
      {
        "reviewNumber": 3,
        "scheduledDate": "2024-01-22T20:00:00Z",
        "status": "PENDING"
      },
      {
        "reviewNumber": 4,
        "scheduledDate": "2024-02-05T20:00:00Z",
        "status": "PENDING"
      },
      {
        "reviewNumber": 5,
        "scheduledDate": "2024-03-06T20:00:00Z",
        "status": "PENDING"
      }
    ]
  },
  "message": "Learning cycle started successfully"
}
```

### 6.2 Input Review Score
**Endpoint**: `POST /sets/{setId}/reviews`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "cycleNumber": 1,
  "reviewNumber": 1,
  "score": 85,
  "note": "Remembered most words, need to review some difficult ones"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "reviewId": "review-550e8400-e29b-41d4-a716-446655440000",
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "cycleNumber": 1,
    "reviewNumber": 1,
    "score": 85,
    "note": "Remembered most words, need to review some difficult ones",
    "status": "COMPLETED",
    "createdAt": "2024-01-16T20:30:00Z",
    "nextReviewDate": "2024-01-18T20:00:00Z",
    "cycleProgress": {
      "completedReviews": 1,
      "totalReviews": 5,
      "averageScore": 85.0
    }
  },
  "message": "Review score recorded successfully"
}
```

### 6.3 Skip Review
**Endpoint**: `POST /sets/{setId}/reviews/skip`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "cycleNumber": 1,
  "reviewNumber": 2,
  "skipReason": "BUSY",
  "note": "Too busy today, will review tomorrow"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "reviewId": "review-550e8400-e29b-41d4-a716-446655440000",
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "cycleNumber": 1,
    "reviewNumber": 2,
    "skipReason": "BUSY",
    "note": "Too busy today, will review tomorrow",
    "status": "SKIPPED",
    "createdAt": "2024-01-18T20:00:00Z",
    "rescheduledDate": "2024-01-19T20:00:00Z"
  },
  "message": "Review skipped successfully"
}
```

### 6.4 Get Review History
**Endpoint**: `GET /sets/{setId}/reviews`

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `cycleNumber`: number (optional)
- `page`: number (default: 0)
- `size`: number (default: 20, max: 100)

**Response**:
```json
{
  "success": true,
  "data": {
    "reviews": [
      {
        "reviewId": "review-1",
        "cycleNumber": 1,
        "reviewNumber": 1,
        "score": 85,
        "note": "Good progress",
        "status": "COMPLETED",
        "createdAt": "2024-01-16T20:30:00Z"
      },
      {
        "reviewId": "review-2",
        "cycleNumber": 1,
        "reviewNumber": 2,
        "skipReason": "BUSY",
        "note": "Too busy",
        "status": "SKIPPED",
        "createdAt": "2024-01-18T20:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "totalElements": 2,
      "totalPages": 1,
      "hasNext": false,
      "hasPrevious": false
    }
  }
}
```

## 7. Reminder Management APIs

### 7.1 Get Today's Reminders
**Endpoint**: `GET /reminders/today`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "date": "2024-01-20",
    "reminders": [
      {
        "reminderId": "reminder-1",
        "setId": "set-550e8400-e29b-41d4-a716-446655440000",
        "setName": "Basic Vocabulary Set 1",
        "cycleNumber": 1,
        "reviewNumber": 3,
        "scheduledTime": "2024-01-20T20:00:00Z",
        "status": "PENDING",
        "priority": "HIGH"
      }
    ],
    "summary": {
      "totalReminders": 1,
      "pendingReminders": 1,
      "completedReminders": 0,
      "skippedReminders": 0
    }
  }
}
```

### 7.2 Get Reminders by Date Range
**Endpoint**: `GET /reminders`

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `startDate`: YYYY-MM-DD (required)
- `endDate`: YYYY-MM-DD (required)
- `status`: PENDING, COMPLETED, SKIPPED (optional)

**Response**:
```json
{
  "success": true,
  "data": {
    "reminders": [
      {
        "reminderId": "reminder-1",
        "setId": "set-550e8400-e29b-41d4-a716-446655440000",
        "setName": "Basic Vocabulary Set 1",
        "cycleNumber": 1,
        "reviewNumber": 3,
        "scheduledDate": "2024-01-20",
        "scheduledTime": "2024-01-20T20:00:00Z",
        "status": "PENDING",
        "priority": "HIGH"
      }
    ],
    "summary": {
      "totalReminders": 1,
      "pendingReminders": 1,
      "completedReminders": 0,
      "skippedReminders": 0
    }
  }
}
```

### 7.3 Reschedule Reminder
**Endpoint**: `PUT /reminders/{reminderId}/reschedule`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "newDate": "2024-01-21",
  "newTime": "19:00",
  "reason": "BUSY"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "reminderId": "reminder-1",
    "oldDate": "2024-01-20",
    "oldTime": "20:00",
    "newDate": "2024-01-21",
    "newTime": "19:00",
    "reason": "BUSY",
    "rescheduleCount": 1,
    "updatedAt": "2024-01-20T10:00:00Z"
  },
  "message": "Reminder rescheduled successfully"
}
```

### 7.4 Mark Reminder as Done
**Endpoint**: `PUT /reminders/{reminderId}/complete`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "reminderId": "reminder-1",
    "status": "COMPLETED",
    "completedAt": "2024-01-20T20:30:00Z"
  },
  "message": "Reminder marked as completed"
}
```

## 8. Statistics & Analytics APIs

### 8.1 Get Learning Statistics
**Endpoint**: `GET /statistics/overview`

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `period`: WEEK, MONTH, YEAR, ALL (default: MONTH)

**Response**:
```json
{
  "success": true,
  "data": {
    "period": "MONTH",
    "startDate": "2024-01-01",
    "endDate": "2024-01-31",
    "overview": {
      "totalSets": 5,
      "activeSets": 3,
      "completedSets": 1,
      "masteredSets": 1,
      "totalReviews": 25,
      "averageScore": 82.5,
      "learningStreak": 15,
      "totalLearningTime": 120
    },
    "progress": {
      "setsCreated": 2,
      "setsCompleted": 1,
      "setsMastered": 1,
      "reviewsCompleted": 20,
      "reviewsSkipped": 5
    },
    "performance": {
      "scoreDistribution": {
        "excellent": 8,
        "good": 10,
        "average": 5,
        "poor": 2
      },
      "averageScoreByCategory": {
        "VOCABULARY": 85.0,
        "GRAMMAR": 78.5,
        "MIXED": 80.0
      }
    }
  }
}
```

### 8.2 Get Set Progress
**Endpoint**: `GET /sets/{setId}/progress`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "success": true,
  "data": {
    "setId": "set-550e8400-e29b-41d4-a716-446655440000",
    "setName": "Basic Vocabulary Set 1",
    "progress": {
      "currentCycle": 2,
      "totalCycles": 1,
      "completedReviews": 7,
      "totalReviews": 10,
      "averageScore": 85.5,
      "learningTime": 45,
      "lastReviewDate": "2024-01-19T15:30:00Z",
      "nextReviewDate": "2024-01-22T20:00:00Z"
    },
    "cycleHistory": [
      {
        "cycleNumber": 1,
        "averageScore": 82.0,
        "completedReviews": 5,
        "skippedReviews": 0,
        "startDate": "2024-01-15T10:30:00Z",
        "endDate": "2024-01-29T20:00:00Z"
      }
    ],
    "scoreTrend": [
      {
        "date": "2024-01-16",
        "score": 80
      },
      {
        "date": "2024-01-18",
        "score": 85
      },
      {
        "date": "2024-01-19",
        "score": 90
      }
    ]
  }
}
```

### 8.3 Get Performance Trends
**Endpoint**: `GET /statistics/trends`

**Headers**: `Authorization: Bearer {token}`

**Query Parameters**:
- `period`: WEEK, MONTH, YEAR (default: MONTH)
- `metric`: AVERAGE_SCORE, COMPLETION_RATE, LEARNING_TIME (default: AVERAGE_SCORE)

**Response**:
```json
{
  "success": true,
  "data": {
    "period": "MONTH",
    "metric": "AVERAGE_SCORE",
    "trends": [
      {
        "date": "2024-01-01",
        "value": 75.0,
        "setsCount": 2
      },
      {
        "date": "2024-01-08",
        "value": 80.5,
        "setsCount": 3
      },
      {
        "date": "2024-01-15",
        "value": 85.2,
        "setsCount": 4
      },
      {
        "date": "2024-01-22",
        "value": 88.0,
        "setsCount": 5
      }
    ],
    "summary": {
      "startValue": 75.0,
      "endValue": 88.0,
      "improvement": 13.0,
      "trend": "IMPROVING"
    }
  }
}
```

## 9. Error Codes

### 9.1 Authentication Errors
- `AUTH_001`: Invalid credentials
- `AUTH_002`: Token expired
- `AUTH_003`: Token invalid
- `AUTH_004`: User not found
- `AUTH_005`: Email already exists

### 9.2 Validation Errors
- `VAL_001`: Required field missing
- `VAL_002`: Invalid email format
- `VAL_003`: Password too weak
- `VAL_004`: Invalid date format
- `VAL_005`: Score out of range (0-100)

### 9.3 Business Logic Errors
- `BUS_001`: Set not found
- `BUS_002`: Set already in progress
- `BUS_003`: Cannot skip more than 2 times
- `BUS_004`: Reminder not found
- `BUS_005`: Cannot reschedule more than 2 times

### 9.4 System Errors
- `SYS_001`: Database connection error
- `SYS_002`: External service unavailable
- `SYS_003`: Internal server error
- `SYS_004`: Rate limit exceeded

## 10. Rate Limiting

### 10.1 Rate Limits
- **Authentication endpoints**: 5 requests per minute
- **User management**: 10 requests per minute
- **Set management**: 20 requests per minute
- **Learning cycle**: 30 requests per minute
- **Statistics**: 10 requests per minute

### 10.2 Rate Limit Headers
```http
X-RateLimit-Limit: 20
X-RateLimit-Remaining: 15
X-RateLimit-Reset: 1642680000
```

## 11. API Versioning

### 11.1 Version Strategy
- URL versioning: `/api/v1/`
- Backward compatibility for 1 year
- Deprecation notice 6 months in advance

### 11.2 Version Headers
```http
API-Version: 1.0
Deprecation-Date: 2025-01-15
Sunset-Date: 2025-07-15
```

## 12. Webhook Events

### 12.1 Available Events
- `set.created`: When a new set is created
- `review.completed`: When a review is completed
- `cycle.completed`: When a learning cycle is completed
- `set.mastered`: When a set reaches mastered status
- `reminder.scheduled`: When a reminder is scheduled
- `reminder.sent`: When a reminder is sent

### 12.2 Webhook Payload Format
```json
{
  "event": "review.completed",
  "timestamp": "2024-01-20T20:30:00Z",
  "data": {
    "reviewId": "review-1",
    "setId": "set-1",
    "score": 85,
    "cycleNumber": 1,
    "reviewNumber": 1
  }
}
```
