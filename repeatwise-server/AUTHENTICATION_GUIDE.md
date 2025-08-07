# Authentication Guide for RepeatWise

## Overview

RepeatWise now includes a complete authentication system with JWT tokens for secure user management.

## Features

- **User Registration**: Create new accounts with email and password
- **User Login**: Authenticate with email and password
- **JWT Token Authentication**: Secure API access with JWT tokens
- **Password Encryption**: BCrypt password hashing
- **Token-based Session Management**: Stateless authentication
- **CORS Support**: Cross-origin resource sharing enabled

## API Endpoints

### Authentication Endpoints

#### POST `/api/auth/register`
Register a new user account.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "message": "Registration successful"
}
```

#### POST `/api/auth/login`
Authenticate existing user.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "message": "Login successful"
}
```

#### POST `/api/auth/logout`
Logout current user (token validation).

**Headers:**
```
Authorization: Bearer <token>
```

#### GET `/api/auth/me`
Get current user information.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

## Security Configuration

### JWT Configuration
- **Secret Key**: Base64 encoded 256-bit key
- **Token Expiration**: 24 hours
- **Refresh Token Expiration**: 7 days
- **Algorithm**: HS256

### Password Security
- **Hashing**: BCrypt with default strength
- **Minimum Length**: 6 characters
- **Validation**: Email format validation

### CORS Configuration
- **Allowed Origins**: All origins (development)
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: All headers
- **Credentials**: Enabled

## Database Schema

### Users Table
```sql
ALTER TABLE users ADD COLUMN name VARCHAR(128) NOT NULL;
ALTER TABLE users ADD COLUMN password VARCHAR(255) NOT NULL;
```

## Frontend Integration

### Flutter Authentication Flow

1. **Login/Register**: User submits credentials
2. **Token Storage**: JWT token saved to secure storage
3. **API Requests**: Token included in Authorization header
4. **Token Refresh**: Automatic token refresh (future enhancement)
5. **Logout**: Token removed from storage

### Authentication State Management

The app uses Riverpod for state management with:
- `AuthNotifier`: Manages authentication state
- `AuthGuard`: Protects routes requiring authentication
- `AuthInitializer`: Checks authentication on app startup

## Error Handling

### Common Error Responses

**Authentication Failed:**
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

**Validation Error:**
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Please provide a valid email address",
    "password": "Password must be at least 6 characters"
  }
}
```

**User Not Found:**
```json
{
  "success": false,
  "message": "User not found"
}
```

## Development Setup

1. **Database Migration**: Run V4__Add_Authentication_Fields.sql
2. **Dependencies**: Ensure JWT and Spring Security dependencies are included
3. **Configuration**: Update application.properties for database connection
4. **Frontend**: Update API base URL in Flutter app

## Security Best Practices

1. **Token Storage**: Use secure storage for JWT tokens
2. **HTTPS**: Always use HTTPS in production
3. **Token Expiration**: Implement token refresh mechanism
4. **Input Validation**: Validate all user inputs
5. **Error Messages**: Don't expose sensitive information in error messages

## Future Enhancements

- [ ] Token refresh mechanism
- [ ] Password reset functionality
- [ ] Email verification
- [ ] Two-factor authentication
- [ ] Role-based access control
- [ ] Rate limiting
- [ ] Audit logging
