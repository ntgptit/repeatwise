# Permissions - RepeatWise

## 1. Overview

Permissions mô tả chi tiết ma trận quyền, access control, data isolation và security rules cho RepeatWise. Tài liệu này cung cấp thông tin đầy đủ để developer implement security và QA test các scenarios bảo mật.

## 2. User Roles and Permissions

### 2.1 Role Definitions

#### 2.1.1 User Roles
- **STUDENT**: Người học sử dụng ứng dụng
- **ADMIN**: Quản trị viên hệ thống
- **SUPPORT**: Nhân viên hỗ trợ khách hàng

#### 2.1.2 Role Hierarchy
```
ADMIN > SUPPORT > STUDENT
```

### 2.2 Permission Matrix

| Resource | Action | STUDENT | SUPPORT | ADMIN |
|----------|--------|---------|---------|-------|
| **User Management** | | | | |
| Own Profile | READ | ✓ | ✓ | ✓ |
| Own Profile | UPDATE | ✓ | ✓ | ✓ |
| Own Profile | DELETE | ✓ | ✗ | ✓ |
| Other Users | READ | ✗ | ✓ | ✓ |
| Other Users | UPDATE | ✗ | ✓ | ✓ |
| Other Users | DELETE | ✗ | ✗ | ✓ |
| **Learning Sets** | | | | |
| Own Sets | READ | ✓ | ✓ | ✓ |
| Own Sets | CREATE | ✓ | ✓ | ✓ |
| Own Sets | UPDATE | ✓ | ✓ | ✓ |
| Own Sets | DELETE | ✓ | ✓ | ✓ |
| Other Users' Sets | READ | ✗ | ✓ | ✓ |
| Other Users' Sets | CREATE | ✗ | ✓ | ✓ |
| Other Users' Sets | UPDATE | ✗ | ✓ | ✓ |
| Other Users' Sets | DELETE | ✗ | ✗ | ✓ |
| **Learning Cycles** | | | | |
| Own Cycles | READ | ✓ | ✓ | ✓ |
| Own Cycles | UPDATE | ✓ | ✓ | ✓ |
| Own Cycles | DELETE | ✓ | ✓ | ✓ |
| Other Users' Cycles | READ | ✗ | ✓ | ✓ |
| Other Users' Cycles | UPDATE | ✗ | ✓ | ✓ |
| Other Users' Cycles | DELETE | ✗ | ✗ | ✓ |
| **Statistics** | | | | |
| Own Statistics | READ | ✓ | ✓ | ✓ |
| Other Users' Statistics | READ | ✗ | ✓ | ✓ |
| System Statistics | READ | ✗ | ✗ | ✓ |
| **Reminders** | | | | |
| Own Reminders | READ | ✓ | ✓ | ✓ |
| Own Reminders | CREATE | ✓ | ✓ | ✓ |
| Own Reminders | UPDATE | ✓ | ✓ | ✓ |
| Own Reminders | DELETE | ✓ | ✓ | ✓ |
| Other Users' Reminders | READ | ✗ | ✓ | ✓ |
| Other Users' Reminders | CREATE | ✗ | ✓ | ✓ |
| Other Users' Reminders | UPDATE | ✗ | ✓ | ✓ |
| Other Users' Reminders | DELETE | ✗ | ✗ | ✓ |
| **Data Management** | | | | |
| Own Data Export | CREATE | ✓ | ✓ | ✓ |
| Own Data Import | CREATE | ✓ | ✓ | ✓ |
| Own Data Backup | CREATE | ✓ | ✓ | ✓ |
| Own Data Backup | RESTORE | ✓ | ✓ | ✓ |
| Other Users' Data | EXPORT | ✗ | ✓ | ✓ |
| Other Users' Data | IMPORT | ✗ | ✓ | ✓ |
| Other Users' Data | BACKUP | ✗ | ✗ | ✓ |
| Other Users' Data | RESTORE | ✗ | ✗ | ✓ |
| **System Management** | | | | |
| System Configuration | READ | ✗ | ✗ | ✓ |
| System Configuration | UPDATE | ✗ | ✗ | ✓ |
| User Management | READ | ✗ | ✓ | ✓ |
| User Management | UPDATE | ✗ | ✓ | ✓ |
| User Management | DELETE | ✗ | ✗ | ✓ |
| Activity Logs | READ | ✗ | ✓ | ✓ |
| System Logs | READ | ✗ | ✗ | ✓ |

## 3. Access Control Implementation

### 3.1 Authentication

#### 3.1.1 JWT Token Structure
```json
{
  "sub": "user_id",
  "iat": 1640995200,
  "exp": 1640998800,
  "role": "STUDENT",
  "permissions": [
    "user:read:own",
    "user:update:own",
    "set:read:own",
    "set:create:own",
    "set:update:own",
    "set:delete:own"
  ]
}
```

#### 3.1.2 Token Validation
```pseudocode
FUNCTION validateToken(token)
    // Verify token signature
    IF !verifyTokenSignature(token) THEN
        RETURN 'invalid_signature'
    END IF
    
    // Check token expiration
    IF isTokenExpired(token) THEN
        RETURN 'token_expired'
    END IF
    
    // Check token blacklist
    IF isTokenBlacklisted(token) THEN
        RETURN 'token_blacklisted'
    END IF
    
    // Extract user information
    userInfo = extractUserInfo(token)
    
    RETURN userInfo
END FUNCTION
```

### 3.2 Authorization

#### 3.2.1 Permission Check
```pseudocode
FUNCTION checkPermission(userId, resource, action, resourceId)
    // Get user role
    userRole = getUserRole(userId)
    
    // Get user permissions
    userPermissions = getUserPermissions(userId)
    
    // Check if user has permission
    permission = resource + ':' + action + ':' + getScope(resourceId, userId)
    
    IF permission IN userPermissions THEN
        RETURN 'allowed'
    END IF
    
    // Check role-based permission
    IF hasRolePermission(userRole, resource, action) THEN
        RETURN 'allowed'
    END IF
    
    RETURN 'denied'
END FUNCTION

FUNCTION getScope(resourceId, userId)
    // Determine scope based on resource ownership
    IF isOwnResource(resourceId, userId) THEN
        RETURN 'own'
    ELSE
        RETURN 'other'
    END IF
END FUNCTION
```

#### 3.2.2 Resource Ownership Check
```pseudocode
FUNCTION isOwnResource(resourceId, userId)
    // Check if resource belongs to user
    resource = getResourceById(resourceId)
    
    IF resource == null THEN
        RETURN false
    END IF
    
    // Check ownership based on resource type
    SWITCH resource.type
        CASE 'user':
            RETURN resource.id == userId
        CASE 'set':
            RETURN resource.userId == userId
        CASE 'cycle':
            RETURN resource.set.userId == userId
        CASE 'reminder':
            RETURN resource.userId == userId
        DEFAULT:
            RETURN false
    END SWITCH
END FUNCTION
```

### 3.3 Data Isolation

#### 3.3.1 User Data Isolation
```pseudocode
FUNCTION filterUserData(data, userId, userRole)
    // Filter data based on user role and ownership
    SWITCH userRole
        CASE 'STUDENT':
            RETURN filterOwnData(data, userId)
        CASE 'SUPPORT':
            RETURN filterSupportData(data, userId)
        CASE 'ADMIN':
            RETURN data  // Admin can see all data
    END SWITCH
END FUNCTION

FUNCTION filterOwnData(data, userId)
    // Only return data owned by user
    filteredData = []
    
    FOR EACH item IN data DO
        IF item.userId == userId THEN
            filteredData.add(item)
        END IF
    END FOR
    
    RETURN filteredData
END FUNCTION
```

#### 3.3.2 Database Query Filtering
```sql
-- Example: Get sets for user
SELECT * FROM sets 
WHERE user_id = ? AND deleted_at IS NULL;

-- Example: Get cycles for user's sets
SELECT c.* FROM learning_cycles c
JOIN sets s ON c.set_id = s.set_id
WHERE s.user_id = ? AND s.deleted_at IS NULL;
```

## 4. Security Rules

### 4.1 Authentication Rules

#### 4.1.1 Password Requirements
- **Minimum Length**: 8 characters
- **Maximum Length**: 20 characters
- **Required Characters**: 
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 number
- **Hashing**: BCrypt with cost factor 12
- **Salt**: Random salt per password

#### 4.1.2 Session Management
- **Token Expiry**: 1 hour for access token
- **Refresh Token**: 30 days
- **Token Rotation**: New refresh token on each refresh
- **Concurrent Sessions**: Maximum 3 active sessions
- **Session Invalidation**: On logout, password change, or security breach

#### 4.1.3 Rate Limiting
- **Authentication Endpoints**: 5 attempts per hour per IP
- **API Endpoints**: 100 requests per minute per user
- **Registration**: 3 attempts per hour per IP
- **Password Reset**: 3 attempts per hour per email

### 4.2 Authorization Rules

#### 4.2.1 Resource Access Rules
- **Own Resources**: Full access (CRUD)
- **Other Users' Resources**: Read-only for SUPPORT, no access for STUDENT
- **System Resources**: ADMIN only
- **Cross-User Operations**: Prohibited for STUDENT

#### 4.2.2 Data Modification Rules
- **Soft Delete**: All deletions are soft deletes
- **Audit Trail**: All modifications are logged
- **Data Validation**: All inputs are validated
- **Business Rules**: All business rules are enforced

### 4.3 Data Protection Rules

#### 4.3.1 Personal Data Protection
- **PII Encryption**: Sensitive data encrypted at rest
- **Data Minimization**: Only necessary data is collected
- **Data Retention**: Automatic cleanup after retention period
- **Data Portability**: Users can export their data

#### 4.3.2 Data Access Logging
- **Access Logs**: All data access is logged
- **Modification Logs**: All data modifications are logged
- **Authentication Logs**: All authentication attempts are logged
- **Authorization Logs**: All authorization decisions are logged

## 5. Security Implementation

### 5.1 Input Validation

#### 5.1.1 Validation Rules
```pseudocode
FUNCTION validateInput(input, inputType)
    errors = []
    
    // Sanitize input
    sanitizedInput = sanitizeInput(input)
    
    // Validate format
    IF !isValidFormat(sanitizedInput, inputType) THEN
        errors.add('Invalid format')
    END IF
    
    // Validate length
    IF !isValidLength(sanitizedInput, inputType) THEN
        errors.add('Invalid length')
    END IF
    
    // Validate content
    IF !isValidContent(sanitizedInput, inputType) THEN
        errors.add('Invalid content')
    END IF
    
    RETURN errors
END FUNCTION

FUNCTION sanitizeInput(input)
    // Remove potentially dangerous characters
    sanitized = input.replace(/[<>\"'&]/g, '')
    
    // Trim whitespace
    sanitized = sanitized.trim()
    
    RETURN sanitized
END FUNCTION
```

#### 5.1.2 SQL Injection Prevention
```sql
-- Use parameterized queries
SELECT * FROM users WHERE email = ? AND password_hash = ?;

-- Use stored procedures
CALL authenticate_user(?, ?);

-- Validate input types
SELECT * FROM sets WHERE user_id = ? AND set_id = ?;
```

### 5.2 XSS Prevention

#### 5.2.1 Output Encoding
```pseudocode
FUNCTION encodeOutput(data)
    // HTML encode
    data = htmlEncode(data)
    
    // JavaScript encode
    data = jsEncode(data)
    
    // URL encode
    data = urlEncode(data)
    
    RETURN data
END FUNCTION
```

#### 5.2.2 Content Security Policy
```http
Content-Security-Policy: default-src 'self'; 
                        script-src 'self' 'unsafe-inline'; 
                        style-src 'self' 'unsafe-inline';
                        img-src 'self' data: https:;
                        connect-src 'self';
```

### 5.3 CSRF Protection

#### 5.3.1 CSRF Token Implementation
```pseudocode
FUNCTION generateCSRFToken(userId)
    // Generate random token
    token = generateRandomToken()
    
    // Store token in session
    storeCSRFToken(userId, token)
    
    // Set token expiry
    setTokenExpiry(userId, token, 1 HOUR)
    
    RETURN token
END FUNCTION

FUNCTION validateCSRFToken(userId, token)
    // Get stored token
    storedToken = getCSRFToken(userId)
    
    // Check if token exists
    IF storedToken == null THEN
        RETURN false
    END IF
    
    // Check if token matches
    IF storedToken != token THEN
        RETURN false
    END IF
    
    // Check if token is expired
    IF isTokenExpired(userId, token) THEN
        RETURN false
    END IF
    
    RETURN true
END FUNCTION
```

## 6. Audit and Monitoring

### 6.1 Audit Logging

#### 6.1.1 Audit Events
- **Authentication Events**: Login, logout, failed login
- **Authorization Events**: Permission denied, role changes
- **Data Access Events**: Read, write, delete operations
- **System Events**: Configuration changes, system errors

#### 6.1.2 Audit Log Structure
```json
{
  "eventId": "uuid",
  "timestamp": "2024-12-19T10:30:00Z",
  "userId": "user_id",
  "userRole": "STUDENT",
  "action": "read",
  "resource": "set",
  "resourceId": "set_id",
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0...",
  "result": "success",
  "details": {
    "additionalInfo": "value"
  }
}
```

#### 6.1.3 Audit Log Implementation
```pseudocode
FUNCTION logAuditEvent(event)
    // Validate event
    IF !isValidAuditEvent(event) THEN
        RETURN 'invalid_event'
    END IF
    
    // Add metadata
    event.eventId = generateUUID()
    event.timestamp = getCurrentTimestamp()
    event.ipAddress = getClientIP()
    event.userAgent = getClientUserAgent()
    
    // Store audit log
    storeAuditLog(event)
    
    // Send to monitoring system
    sendToMonitoring(event)
    
    RETURN 'logged'
END FUNCTION
```

### 6.2 Security Monitoring

#### 6.2.1 Monitoring Rules
- **Failed Login Attempts**: Alert after 5 failed attempts
- **Unusual Access Patterns**: Alert on abnormal access
- **Data Breach Attempts**: Alert on suspicious activities
- **System Errors**: Alert on critical errors

#### 6.2.2 Alert Implementation
```pseudocode
FUNCTION checkSecurityAlerts(userId, event)
    // Check failed login attempts
    IF event.action == 'login_failed' THEN
        failedAttempts = getFailedLoginAttempts(userId, 1 HOUR)
        IF failedAttempts >= 5 THEN
            sendSecurityAlert('multiple_failed_logins', userId)
        END IF
    END IF
    
    // Check unusual access patterns
    IF event.action == 'read' THEN
        accessCount = getAccessCount(userId, 1 HOUR)
        IF accessCount > 1000 THEN
            sendSecurityAlert('unusual_access_pattern', userId)
        END IF
    END IF
    
    // Check data breach attempts
    IF event.action == 'unauthorized_access' THEN
        sendSecurityAlert('data_breach_attempt', userId)
    END IF
END FUNCTION
```

## 7. Data Privacy and Compliance

### 7.1 Data Classification

#### 7.1.1 Data Categories
- **Public**: Non-sensitive information
- **Internal**: Business information
- **Confidential**: Personal information
- **Restricted**: Highly sensitive information

#### 7.1.2 Data Handling Rules
- **Public**: No restrictions
- **Internal**: Employee access only
- **Confidential**: Encrypted, limited access
- **Restricted**: Encrypted, audit required

### 7.2 Privacy Controls

#### 7.2.1 Data Minimization
- **Collection**: Only necessary data
- **Processing**: Only for stated purpose
- **Retention**: Automatic deletion after period
- **Sharing**: Only with consent

#### 7.2.2 User Rights
- **Access**: Users can view their data
- **Rectification**: Users can correct their data
- **Erasure**: Users can delete their data
- **Portability**: Users can export their data

### 7.3 Compliance Requirements

#### 7.3.1 GDPR Compliance
- **Consent**: Explicit consent for data processing
- **Right to be Forgotten**: Data deletion on request
- **Data Portability**: Data export in standard format
- **Privacy by Design**: Privacy built into system

#### 7.3.2 Data Protection Implementation
```pseudocode
FUNCTION handleDataDeletionRequest(userId)
    // Validate request
    IF !isValidDeletionRequest(userId) THEN
        RETURN 'invalid_request'
    END IF
    
    // Anonymize data
    anonymizeUserData(userId)
    
    // Delete personal data
    deletePersonalData(userId)
    
    // Log deletion
    logDataDeletion(userId)
    
    // Notify user
    notifyDataDeletion(userId)
    
    RETURN 'deleted'
END FUNCTION
```

## 8. Security Testing

### 8.1 Security Test Cases

#### 8.1.1 Authentication Tests
- **Valid Login**: Test successful authentication
- **Invalid Credentials**: Test failed authentication
- **Token Expiry**: Test token expiration handling
- **Session Management**: Test session lifecycle

#### 8.1.2 Authorization Tests
- **Access Control**: Test permission enforcement
- **Data Isolation**: Test user data separation
- **Role-based Access**: Test role permissions
- **Cross-user Access**: Test unauthorized access prevention

#### 8.1.3 Input Validation Tests
- **SQL Injection**: Test SQL injection prevention
- **XSS Prevention**: Test cross-site scripting prevention
- **CSRF Protection**: Test CSRF token validation
- **Input Sanitization**: Test input cleaning

### 8.2 Security Test Scenarios

#### 8.2.1 Penetration Testing
- **Authentication Bypass**: Attempt to bypass authentication
- **Privilege Escalation**: Attempt to gain higher privileges
- **Data Access**: Attempt to access other users' data
- **System Compromise**: Attempt to compromise system

#### 8.2.2 Vulnerability Testing
- **OWASP Top 10**: Test for common vulnerabilities
- **Security Headers**: Test security header implementation
- **Encryption**: Test data encryption
- **Session Security**: Test session management

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Security Architect  
**Stakeholders**: Development Team, QA Team, DevOps Team
