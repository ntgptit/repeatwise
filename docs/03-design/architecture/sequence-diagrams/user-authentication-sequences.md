# User Authentication Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình xác thực người dùng trong hệ thống RepeatWise, bao gồm đăng ký, đăng nhập, refresh token và reset password.

## 1. User Registration Sequence

### 1.1 Successful Registration

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL
    participant NotificationService as Notification Service
    participant EmailService as Email Service

    MobileApp->>APIGateway: POST /api/auth/register
    Note over MobileApp,APIGateway: {email, password, name, phone}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: registerUser(userData)
    
    AuthService->>UserRepository: findByEmail(email)
    UserRepository->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserRepository: No user found
    UserRepository-->>AuthService: null
    
    AuthService->>AuthService: validatePassword(password)
    AuthService->>AuthService: hashPassword(password)
    AuthService->>AuthService: createUserEntity(userData)
    
    AuthService->>UserRepository: save(user)
    UserRepository->>Database: INSERT INTO users (email, password_hash, name, phone, created_at)
    Database-->>UserRepository: User created with ID
    UserRepository-->>AuthService: User entity
    
    AuthService->>NotificationService: sendWelcomeEmail(user)
    NotificationService->>EmailService: sendEmail(welcome_template, user.email)
    EmailService-->>NotificationService: Email sent successfully
    
    AuthService-->>AuthController: RegistrationResult(user, success)
    AuthController-->>APIGateway: 201 Created + User data
    APIGateway-->>MobileApp: 201 Created + User data
```

### 1.2 Registration with Existing Email

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/auth/register
    Note over MobileApp,APIGateway: {email, password, name, phone}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: registerUser(userData)
    
    AuthService->>UserRepository: findByEmail(email)
    UserRepository->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserRepository: Existing user found
    UserRepository-->>AuthService: Existing user entity
    
    AuthService-->>AuthController: RegistrationError("Email already exists")
    AuthController-->>APIGateway: 409 Conflict + Error message
    APIGateway-->>MobileApp: 409 Conflict + Error message
```

## 2. User Login Sequence

### 2.1 Successful Login

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL
    participant Cache as Redis Cache

    MobileApp->>APIGateway: POST /api/auth/login
    Note over MobileApp,APIGateway: {email, password}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: authenticateUser(email, password)
    
    AuthService->>UserRepository: findByEmail(email)
    UserRepository->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserRepository: User data
    UserRepository-->>AuthService: User entity
    
    AuthService->>AuthService: validatePassword(password, hashedPassword)
    AuthService->>AuthService: generateJWTToken(user)
    AuthService->>AuthService: generateRefreshToken(user)
    
    AuthService->>Cache: storeUserSession(accessToken, user.id)
    Cache-->>AuthService: Session stored
    AuthService->>Cache: storeRefreshToken(refreshToken, user.id)
    Cache-->>AuthService: Refresh token stored
    
    AuthService-->>AuthController: AuthenticationResult(accessToken, refreshToken, user)
    AuthController-->>APIGateway: 200 OK + Tokens + User data
    APIGateway-->>MobileApp: 200 OK + Tokens + User data
```

### 2.2 Login with Invalid Credentials

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/auth/login
    Note over MobileApp,APIGateway: {email, password}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: authenticateUser(email, password)
    
    AuthService->>UserRepository: findByEmail(email)
    UserRepository->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserRepository: User data
    UserRepository-->>AuthService: User entity
    
    AuthService->>AuthService: validatePassword(password, hashedPassword)
    Note over AuthService: Password validation fails
    
    AuthService-->>AuthController: AuthenticationError("Invalid credentials")
    AuthController-->>APIGateway: 401 Unauthorized + Error message
    APIGateway-->>MobileApp: 401 Unauthorized + Error message
```

## 3. Token Refresh Sequence

### 3.1 Successful Token Refresh

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant Cache as Redis Cache

    MobileApp->>APIGateway: POST /api/auth/refresh
    Note over MobileApp,APIGateway: {refreshToken}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: refreshToken(refreshToken)
    
    AuthService->>AuthService: validateRefreshToken(refreshToken)
    AuthService->>Cache: getUserIdByRefreshToken(refreshToken)
    Cache-->>AuthService: User ID
    
    AuthService->>AuthService: generateNewAccessToken(userId)
    AuthService->>AuthService: generateNewRefreshToken(userId)
    
    AuthService->>Cache: storeUserSession(newAccessToken, userId)
    Cache-->>AuthService: Session stored
    AuthService->>Cache: storeRefreshToken(newRefreshToken, userId)
    Cache-->>AuthService: Refresh token stored
    AuthService->>Cache: invalidateOldRefreshToken(refreshToken)
    Cache-->>AuthService: Old token invalidated
    
    AuthService-->>AuthController: TokenRefreshResult(newAccessToken, newRefreshToken)
    AuthController-->>APIGateway: 200 OK + New tokens
    APIGateway-->>MobileApp: 200 OK + New tokens
```

### 3.2 Token Refresh with Invalid Token

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant Cache as Redis Cache

    MobileApp->>APIGateway: POST /api/auth/refresh
    Note over MobileApp,APIGateway: {refreshToken}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: refreshToken(refreshToken)
    
    AuthService->>AuthService: validateRefreshToken(refreshToken)
    Note over AuthService: Token validation fails
    
    AuthService-->>AuthController: TokenRefreshError("Invalid refresh token")
    AuthController-->>APIGateway: 401 Unauthorized + Error message
    APIGateway-->>MobileApp: 401 Unauthorized + Error message
```

## 4. Password Reset Sequence

### 4.1 Request Password Reset

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL
    participant NotificationService as Notification Service
    participant EmailService as Email Service

    MobileApp->>APIGateway: POST /api/auth/forgot-password
    Note over MobileApp,APIGateway: {email}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: requestPasswordReset(email)
    
    AuthService->>UserRepository: findByEmail(email)
    UserRepository->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserRepository: User data
    UserRepository-->>AuthService: User entity
    
    AuthService->>AuthService: generateResetToken()
    AuthService->>AuthService: setResetTokenExpiry()
    
    AuthService->>UserRepository: updateResetToken(user.id, resetToken, expiry)
    UserRepository->>Database: UPDATE users SET reset_token = ?, reset_token_expiry = ?
    Database-->>UserRepository: User updated
    
    AuthService->>NotificationService: sendPasswordResetEmail(user, resetToken)
    NotificationService->>EmailService: sendEmail(reset_template, user.email, resetToken)
    EmailService-->>NotificationService: Email sent successfully
    
    AuthService-->>AuthController: PasswordResetRequestResult(success)
    AuthController-->>APIGateway: 200 OK + Success message
    APIGateway-->>MobileApp: 200 OK + Success message
```

### 4.2 Reset Password with Token

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant UserRepository as User Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/auth/reset-password
    Note over MobileApp,APIGateway: {resetToken, newPassword}
    
    APIGateway->>APIGateway: Validate request format
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: resetPassword(resetToken, newPassword)
    
    AuthService->>UserRepository: findByResetToken(resetToken)
    UserRepository->>Database: SELECT * FROM users WHERE reset_token = ?
    Database-->>UserRepository: User data
    UserRepository-->>AuthService: User entity
    
    AuthService->>AuthService: validateResetTokenExpiry(user.resetTokenExpiry)
    AuthService->>AuthService: validatePassword(newPassword)
    AuthService->>AuthService: hashPassword(newPassword)
    
    AuthService->>UserRepository: updatePassword(user.id, hashedPassword)
    UserRepository->>Database: UPDATE users SET password_hash = ?, reset_token = NULL, reset_token_expiry = NULL
    Database-->>UserRepository: User updated
    
    AuthService-->>AuthController: PasswordResetResult(success)
    AuthController-->>APIGateway: 200 OK + Success message
    APIGateway-->>MobileApp: 200 OK + Success message
```

## 5. Logout Sequence

### 5.1 User Logout

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant AuthController as Auth Controller
    participant AuthService as Auth Service
    participant Cache as Redis Cache

    MobileApp->>APIGateway: POST /api/auth/logout
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Extract access token
    APIGateway->>AuthController: Forward request
    
    AuthController->>AuthService: logout(accessToken)
    
    AuthService->>AuthService: extractUserIdFromToken(accessToken)
    AuthService->>Cache: invalidateUserSession(accessToken)
    Cache-->>AuthService: Session invalidated
    AuthService->>Cache: invalidateRefreshToken(userId)
    Cache-->>AuthService: Refresh token invalidated
    
    AuthService-->>AuthController: LogoutResult(success)
    AuthController-->>APIGateway: 200 OK + Success message
    APIGateway-->>MobileApp: 200 OK + Success message
```

## Ghi chú kỹ thuật

### 1. Security Considerations
- Tất cả passwords được hash bằng bcrypt với salt
- JWT tokens có expiration time
- Refresh tokens được rotate sau mỗi lần sử dụng
- Session được lưu trong Redis với TTL

### 2. Error Handling
- Validation errors trả về 400 Bad Request
- Authentication errors trả về 401 Unauthorized
- Duplicate email trả về 409 Conflict
- Server errors trả về 500 Internal Server Error

### 3. Performance
- User lookup được cache trong Redis
- Email sending được xử lý async
- Database queries được optimize với indexes

### 4. Monitoring
- Login attempts được log để detect brute force
- Failed authentication được track
- Email delivery status được monitor
