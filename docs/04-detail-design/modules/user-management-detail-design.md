# User Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
User Management Module xử lý tất cả các hoạt động liên quan đến người dùng bao gồm:
- Đăng ký tài khoản mới
- Xác thực và đăng nhập
- Quản lý profile người dùng
- Reset password
- Quản lý preferences và settings

### 1.2 Scope
- **In Scope**: Authentication, authorization, user profile management, password management
- **Out of Scope**: Learning data management, notification preferences (handled by other modules)

### 1.3 Dependencies
- **Database**: Users table, user_preferences table
- **External Services**: Email Service (for confirmation/reset emails)
- **Security**: JWT token service, BCrypt hashing

## 2. API Contracts

### 2.1 Authentication Endpoints

#### POST /api/v1/auth/register
**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123",
  "confirmPassword": "SecurePass123",
  "fullName": "Nguyễn Văn A",
  "preferredLanguage": "vi",
  "timezone": "Asia/Ho_Chi_Minh",
  "defaultReminderTime": "09:00"
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Tài khoản đã được tạo thành công",
  "data": {
    "userId": "uuid-here",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "status": "pending_confirmation",
    "createdAt": "2024-12-19T10:00:00Z"
  }
}
```

**Response (Error - 400):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Dữ liệu đầu vào không hợp lệ",
    "details": [
      {
        "field": "email",
        "message": "Email đã được sử dụng"
      }
    ]
  }
}
```

#### POST /api/v1/auth/login
**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "data": {
    "accessToken": "jwt-token-here",
    "refreshToken": "refresh-token-here",
    "expiresIn": 3600,
    "user": {
      "userId": "uuid-here",
      "email": "user@example.com",
      "fullName": "Nguyễn Văn A",
      "preferredLanguage": "vi",
      "timezone": "Asia/Ho_Chi_Minh",
      "status": "active"
    }
  }
}
```

#### POST /api/v1/auth/refresh
**Request:**
```json
{
  "refreshToken": "refresh-token-here"
}
```

#### POST /api/v1/auth/logout
**Request:**
```json
{
  "refreshToken": "refresh-token-here"
}
```

### 2.2 Password Management

#### POST /api/v1/auth/forgot-password
**Request:**
```json
{
  "email": "user@example.com"
}
```

#### POST /api/v1/auth/reset-password
**Request:**
```json
{
  "token": "reset-token-here",
  "newPassword": "NewSecurePass123",
  "confirmPassword": "NewSecurePass123"
}
```

### 2.3 Profile Management

#### GET /api/v1/users/profile
**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "uuid-here",
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "preferredLanguage": "vi",
    "timezone": "Asia/Ho_Chi_Minh",
    "defaultReminderTime": "09:00",
    "createdAt": "2024-12-19T10:00:00Z",
    "lastLoginAt": "2024-12-19T15:30:00Z",
    "status": "active"
  }
}
```

#### PUT /api/v1/users/profile
**Request:**
```json
{
  "fullName": "Nguyễn Văn B",
  "preferredLanguage": "en",
  "timezone": "UTC",
  "defaultReminderTime": "08:00"
}
```

## 3. Data Models

### 3.1 User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private Language preferredLanguage;
    
    @Column(nullable = false)
    private String timezone;
    
    @Column(nullable = false)
    private String defaultReminderTime;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLoginAt;
    
    private String emailConfirmationToken;
    
    private LocalDateTime emailConfirmationExpiresAt;
    
    private String passwordResetToken;
    
    private LocalDateTime passwordResetExpiresAt;
}
```

### 3.2 User Preferences Entity
```java
@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID preferenceId;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Boolean emailNotifications;
    
    private Boolean pushNotifications;
    
    private String notificationLanguage;
    
    private Integer maxSetsPerDay;
    
    private Integer reviewSessionDuration;
    
    private Boolean darkMode;
    
    private String theme;
}
```

## 4. Business Logic

### 4.1 User Registration Logic
```pseudocode
FUNCTION registerUser(registrationRequest):
    // Validate input
    IF NOT validateRegistrationInput(registrationRequest):
        RETURN validationError
    
    // Check email uniqueness
    IF userRepository.existsByEmail(registrationRequest.email):
        RETURN emailAlreadyExistsError
    
    // Hash password
    hashedPassword = BCrypt.hash(registrationRequest.password)
    
    // Create user entity
    user = new User()
    user.email = registrationRequest.email
    user.passwordHash = hashedPassword
    user.fullName = registrationRequest.fullName
    user.preferredLanguage = registrationRequest.preferredLanguage
    user.timezone = registrationRequest.timezone
    user.defaultReminderTime = registrationRequest.defaultReminderTime
    user.status = PENDING_CONFIRMATION
    user.createdAt = now()
    
    // Generate email confirmation token
    user.emailConfirmationToken = generateSecureToken()
    user.emailConfirmationExpiresAt = now() + 24 hours
    
    // Save user
    savedUser = userRepository.save(user)
    
    // Send confirmation email
    emailService.sendConfirmationEmail(savedUser.email, user.emailConfirmationToken)
    
    // Create default preferences
    createDefaultUserPreferences(savedUser)
    
    RETURN successResponse(savedUser)
```

### 4.2 User Login Logic
```pseudocode
FUNCTION loginUser(loginRequest):
    // Find user by email
    user = userRepository.findByEmail(loginRequest.email)
    IF user IS NULL:
        RETURN invalidCredentialsError
    
    // Check password
    IF NOT BCrypt.verify(loginRequest.password, user.passwordHash):
        RETURN invalidCredentialsError
    
    // Check user status
    IF user.status != ACTIVE:
        RETURN accountNotActiveError
    
    // Update last login
    user.lastLoginAt = now()
    userRepository.save(user)
    
    // Generate tokens
    accessToken = jwtService.generateAccessToken(user)
    refreshToken = jwtService.generateRefreshToken(user)
    
    RETURN successResponse({
        accessToken: accessToken,
        refreshToken: refreshToken,
        expiresIn: 3600,
        user: mapToUserDto(user)
    })
```

### 4.3 Password Reset Logic
```pseudocode
FUNCTION initiatePasswordReset(email):
    user = userRepository.findByEmail(email)
    IF user IS NULL:
        // Don't reveal if email exists
        RETURN successResponse("Nếu email tồn tại, bạn sẽ nhận được hướng dẫn reset password")
    
    // Generate reset token
    user.passwordResetToken = generateSecureToken()
    user.passwordResetExpiresAt = now() + 1 hour
    
    userRepository.save(user)
    
    // Send reset email
    emailService.sendPasswordResetEmail(user.email, user.passwordResetToken)
    
    RETURN successResponse("Nếu email tồn tại, bạn sẽ nhận được hướng dẫn reset password")

FUNCTION resetPassword(resetRequest):
    user = userRepository.findByPasswordResetToken(resetRequest.token)
    IF user IS NULL OR user.passwordResetExpiresAt < now():
        RETURN invalidTokenError
    
    // Validate new password
    IF NOT validatePassword(resetRequest.newPassword):
        RETURN passwordValidationError
    
    // Update password
    user.passwordHash = BCrypt.hash(resetRequest.newPassword)
    user.passwordResetToken = NULL
    user.passwordResetExpiresAt = NULL
    
    userRepository.save(user)
    
    RETURN successResponse("Password đã được reset thành công")
```

## 5. Validation Rules

### 5.1 Registration Validation
- **Email**: Valid email format, unique in system
- **Password**: 8-20 characters, at least 1 uppercase, 1 lowercase, 1 number
- **Full Name**: 2-100 characters, not empty
- **Language**: Must be 'vi' or 'en'
- **Timezone**: Valid IANA timezone identifier
- **Reminder Time**: HH:MM format, 24-hour

### 5.2 Login Validation
- **Email**: Valid email format
- **Password**: Not empty, minimum 1 character

### 5.3 Profile Update Validation
- **Full Name**: 2-100 characters, not empty
- **Language**: Must be 'vi' or 'en'
- **Timezone**: Valid IANA timezone identifier
- **Reminder Time**: HH:MM format, 24-hour

## 6. Error Handling

### 6.1 Error Codes
- `USER_001`: Email already exists
- `USER_002`: Invalid email format
- `USER_003`: Password validation failed
- `USER_004`: User not found
- `USER_005`: Invalid credentials
- `USER_006`: Account not active
- `USER_007`: Invalid reset token
- `USER_008`: Reset token expired
- `USER_009`: Email confirmation required

### 6.2 Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "USER_001",
    "message": "Email đã được sử dụng",
    "details": [
      {
        "field": "email",
        "message": "Email này đã được đăng ký trong hệ thống"
      }
    ],
    "timestamp": "2024-12-19T10:00:00Z",
    "requestId": "req-uuid-here"
  }
}
```

## 7. Security Considerations

### 7.1 Password Security
- Use BCrypt with salt rounds = 12
- Never log or return password in responses
- Implement password strength requirements
- Rate limit password reset attempts

### 7.2 Token Security
- JWT access tokens expire in 1 hour
- Refresh tokens expire in 30 days
- Use secure random token generation
- Implement token blacklisting on logout

### 7.3 Data Protection
- Hash sensitive data (passwords)
- Use HTTPS for all communications
- Implement rate limiting on auth endpoints
- Log security events for monitoring

## 8. Observability

### 8.1 Logging
```java
// Log successful registration
log.info("User registered successfully", 
    "userId", user.getUserId(), 
    "email", user.getEmail());

// Log failed login attempts
log.warn("Failed login attempt", 
    "email", loginRequest.getEmail(), 
    "ipAddress", request.getRemoteAddr());

// Log password reset
log.info("Password reset initiated", 
    "userId", user.getUserId(), 
    "email", user.getEmail());
```

### 8.2 Metrics
- Registration success/failure rate
- Login success/failure rate
- Password reset requests
- Active user sessions
- Authentication response times

### 8.3 Alerts
- High failed login rate (>10% in 5 minutes)
- Multiple password reset attempts from same IP
- Unusual registration patterns
- Authentication service errors

## 9. Testing Strategy

### 9.1 Unit Tests
- User entity validation
- Password hashing/verification
- Token generation/validation
- Business logic functions

### 9.2 Integration Tests
- API endpoint testing
- Database operations
- Email service integration
- JWT token flow

### 9.3 Security Tests
- Password strength validation
- SQL injection prevention
- Rate limiting effectiveness
- Token security

### 9.4 Performance Tests
- Registration throughput
- Login response times
- Concurrent user handling
- Database query performance

## 10. Dependencies

### 10.1 Internal Dependencies
- `UserRepository`: Database operations
- `JwtService`: Token management
- `EmailService`: Email notifications
- `ValidationService`: Input validation

### 10.2 External Dependencies
- Database (PostgreSQL)
- Email Service (SendGrid/AWS SES)
- Redis (for token blacklisting)

### 10.3 Configuration
```yaml
user-management:
  password:
    min-length: 8
    max-length: 20
    require-uppercase: true
    require-lowercase: true
    require-number: true
  token:
    access-token-expiry: 3600
    refresh-token-expiry: 2592000
    reset-token-expiry: 3600
  email:
    confirmation-expiry: 86400
  rate-limiting:
    login-attempts: 5
    reset-attempts: 3
    window-minutes: 15
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, Security Team
