# Error Handling Specifications

## 1. HTTP Status Codes Mapping

| Status Code | Category | When to Use |
|-------------|----------|-------------|
| 200 OK | Success | Read operations success |
| 201 Created | Success | Resource created |
| 204 No Content | Success | Delete success, no response body |
| 400 Bad Request | Client Error | Validation failed, malformed request |
| 401 Unauthorized | Client Error | Authentication required/failed |
| 403 Forbidden | Client Error | Authenticated but not authorized |
| 404 Not Found | Client Error | Resource not found |
| 409 Conflict | Client Error | Duplicate resource, concurrent update |
| 422 Unprocessable Entity | Client Error | Business rule violation |
| 429 Too Many Requests | Client Error | Rate limit exceeded |
| 500 Internal Server Error | Server Error | Unexpected error |
| 503 Service Unavailable | Server Error | Service down/maintenance |

## 2. Error Codes Catalog

### Authentication Errors (401)
- `INVALID_CREDENTIALS`: Email/password incorrect
- `TOKEN_EXPIRED`: Access token expired (use refresh)
- `TOKEN_INVALID`: Token malformed or tampered
- `REFRESH_TOKEN_EXPIRED`: Refresh token expired (re-login)
- `REFRESH_TOKEN_REVOKED`: Token revoked (logout/password change)
- `USER_INACTIVE`: Account suspended/deleted

### Authorization Errors (403)
- `ACCESS_DENIED`: Cannot access resource (not owner)
- `INSUFFICIENT_PERMISSIONS`: Need higher role

### Validation Errors (400)
- `VALIDATION_ERROR`: Field validation failed (multiple fields)
- `FIELD_REQUIRED`: Required field missing
- `FIELD_TOO_LONG`: Field exceeds max length
- `FIELD_TOO_SHORT`: Field below min length
- `INVALID_FORMAT`: Invalid format (email, UUID, date)
- `INVALID_ENUM`: Invalid enum value

### Business Rule Errors (422)
- `MAX_DEPTH_EXCEEDED`: Folder depth > 10
- `CIRCULAR_REFERENCE`: Move folder into itself
- `FOLDER_TOO_LARGE`: Copy > 500 items
- `DECK_TOO_LARGE`: Copy > 10,000 cards
- `DAILY_LIMIT_EXCEEDED`: Review/new card limit reached
- `DUPLICATE_RESOURCE`: Name conflict
- `IMPORT_FILE_TOO_LARGE`: File > 50MB or > 10,000 rows
- `IMPORT_VALIDATION_FAILED`: CSV/Excel data invalid

### Not Found Errors (404)
- `RESOURCE_NOT_FOUND`: Generic resource not found
- `USER_NOT_FOUND`: User not found
- `FOLDER_NOT_FOUND`: Folder not found
- `DECK_NOT_FOUND`: Deck not found
- `CARD_NOT_FOUND`: Card not found

### Conflict Errors (409)
- `EMAIL_ALREADY_EXISTS`: Email taken
- `NAME_CONFLICT`: Folder/deck name conflict in parent
- `CONCURRENT_UPDATE`: Resource modified by another user

### Rate Limit Errors (429)
- `RATE_LIMIT_EXCEEDED`: Too many requests

### Server Errors (500)
- `INTERNAL_SERVER_ERROR`: Unexpected error
- `DATABASE_ERROR`: Database operation failed
- `FILE_PROCESSING_ERROR`: Import/export failed

## 3. Error Response Format

### Standard Format
```json
{
  "error": "ERROR_CODE",
  "message": "Human-readable message",
  "details": [...],
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

### Validation Error Format
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    {
      "field": "name",
      "code": "FIELD_REQUIRED",
      "message": "Folder name is required"
    },
    {
      "field": "depth",
      "code": "MAX_DEPTH_EXCEEDED",
      "message": "Maximum folder depth (10 levels) exceeded"
    }
  ],
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

## 4. Exception Hierarchy (Pseudo-code)

```
Exception (base)
├── ApplicationException (abstract)
│   ├── ClientException (4xx)
│   │   ├── ValidationException (400)
│   │   │   ├── FieldValidationException
│   │   │   ├── MaxDepthExceededException (422)
│   │   │   ├── FolderTooLargeException (422)
│   │   │   └── ImportValidationException (422)
│   │   ├── AuthenticationException (401)
│   │   │   ├── InvalidCredentialsException
│   │   │   ├── TokenExpiredException
│   │   │   └── TokenInvalidException
│   │   ├── AuthorizationException (403)
│   │   │   └── AccessDeniedException
│   │   ├── ResourceNotFoundException (404)
│   │   │   ├── FolderNotFoundException
│   │   │   ├── DeckNotFoundException
│   │   │   └── CardNotFoundException
│   │   ├── ConflictException (409)
│   │   │   ├── DuplicateResourceException
│   │   │   └── CircularReferenceException
│   │   └── RateLimitException (429)
│   └── ServerException (5xx)
│       ├── InternalServerException (500)
│       ├── DatabaseException (500)
│       └── FileProcessingException (500)
```

## 5. Error Handling Logic (Pseudo-code)

```
FUNCTION GlobalExceptionHandler(exception):
  // Log error
  LOG_ERROR(exception.message, exception.stackTrace)

  // Determine error type
  IF exception INSTANCEOF ClientException THEN
    statusCode = exception.httpStatus()
    errorCode = exception.errorCode()
    message = exception.message()
  ELSE IF exception INSTANCEOF ServerException THEN
    statusCode = 500
    errorCode = "INTERNAL_SERVER_ERROR"
    message = "An unexpected error occurred"  // Don't expose internals
  ELSE
    statusCode = 500
    errorCode = "INTERNAL_SERVER_ERROR"
    message = "An unexpected error occurred"
  END IF

  // Build error response
  errorResponse = {
    error: errorCode,
    message: message,
    details: exception.details(),  // If ValidationException
    timestamp: NOW(),
    path: request.path()
  }

  // Return response
  RETURN HTTP_RESPONSE(statusCode, errorResponse)
END FUNCTION
```

## 6. Recovery Strategies

| Error Type | Recovery Strategy |
|------------|-------------------|
| TOKEN_EXPIRED | Frontend: Auto-refresh token, retry request |
| REFRESH_TOKEN_EXPIRED | Frontend: Redirect to login |
| VALIDATION_ERROR | Frontend: Display field errors, allow retry |
| MAX_DEPTH_EXCEEDED | Frontend: Show error, suggest alternative (flatten structure) |
| FOLDER_TOO_LARGE | Frontend: Suggest copy smaller portions |
| RATE_LIMIT_EXCEEDED | Frontend: Show retry countdown, exponential backoff |
| INTERNAL_SERVER_ERROR | Frontend: Show generic error, allow retry, contact support |

## 7. Logging Strategy

```
FUNCTION logException(exception, request, user):
  logEntry = {
    level: determineLogLevel(exception),
    timestamp: NOW(),
    errorCode: exception.errorCode(),
    message: exception.message(),
    stackTrace: exception.stackTrace(),
    userId: user?.id,
    requestPath: request.path(),
    requestMethod: request.method(),
    requestBody: sanitize(request.body()),  // Remove sensitive data
    ipAddress: request.ipAddress(),
    userAgent: request.userAgent()
  }

  IF exception INSTANCEOF ServerException THEN
    LOG_AS_ERROR(logEntry)  // Alert on-call engineer
  ELSE IF exception INSTANCEOF ClientException AND exception.statusCode >= 400 THEN
    LOG_AS_WARN(logEntry)  // Track for metrics
  ELSE
    LOG_AS_INFO(logEntry)
  END IF
END FUNCTION
```

## 8. Implementation Guidelines

### Error Response Construction

```
FUNCTION buildErrorResponse(exception, request):
  response = {
    error: exception.errorCode(),
    message: exception.message(),
    timestamp: NOW(),
    path: request.path()
  }

  // Add details for validation errors
  IF exception INSTANCEOF ValidationException THEN
    response.details = exception.validationErrors()
  END IF

  RETURN response
END FUNCTION
```

### Client-Side Error Handling

```
FUNCTION handleApiError(error):
  SWITCH error.code:
    CASE "TOKEN_EXPIRED":
      TRY:
        newToken = refreshAccessToken()
        retryOriginalRequest(newToken)
      CATCH:
        redirectToLogin()
      END TRY

    CASE "REFRESH_TOKEN_EXPIRED":
      clearAuthState()
      redirectToLogin()

    CASE "VALIDATION_ERROR":
      displayFieldErrors(error.details)

    CASE "RATE_LIMIT_EXCEEDED":
      showRetryCountdown()
      scheduleRetryWithBackoff()

    CASE "INTERNAL_SERVER_ERROR":
      showGenericError()
      logErrorToMonitoring()

    DEFAULT:
      showErrorMessage(error.message)
  END SWITCH
END FUNCTION
```

### Retry Logic with Exponential Backoff

```
FUNCTION retryWithBackoff(operation, maxRetries = 3):
  FOR attempt = 1 TO maxRetries:
    TRY:
      RETURN operation()
    CATCH error:
      IF attempt = maxRetries THEN
        THROW error
      END IF

      IF error.code = "RATE_LIMIT_EXCEEDED" THEN
        waitTime = 2^attempt * 1000  // Exponential backoff
        WAIT(waitTime)
      ELSE IF isServerError(error) THEN
        waitTime = attempt * 1000  // Linear backoff
        WAIT(waitTime)
      ELSE
        THROW error  // Don't retry client errors
      END IF
    END TRY
  END FOR
END FUNCTION
```

## 9. Error Code Usage Examples

### Authentication Flow

```
// Login with invalid credentials
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "wrong_password"
}

Response: 401 Unauthorized
{
  "error": "INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/auth/login"
}

// Access API with expired token
GET /api/folders
Authorization: Bearer expired_token

Response: 401 Unauthorized
{
  "error": "TOKEN_EXPIRED",
  "message": "Access token has expired",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

### Validation Errors

```
// Create folder with validation errors
POST /api/folders
{
  "name": "",
  "parent_id": "invalid-uuid"
}

Response: 400 Bad Request
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    {
      "field": "name",
      "code": "FIELD_REQUIRED",
      "message": "Folder name is required"
    },
    {
      "field": "parent_id",
      "code": "INVALID_FORMAT",
      "message": "Parent ID must be a valid UUID"
    }
  ],
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

### Business Rule Violations

```
// Move folder exceeding max depth
PUT /api/folders/{folder_id}
{
  "parent_id": "deep-folder-id"
}

Response: 422 Unprocessable Entity
{
  "error": "MAX_DEPTH_EXCEEDED",
  "message": "Maximum folder depth (10 levels) exceeded",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders/abc-123"
}

// Copy folder with too many items
POST /api/folders/{folder_id}/copy
{
  "target_parent_id": "target-id"
}

Response: 422 Unprocessable Entity
{
  "error": "FOLDER_TOO_LARGE",
  "message": "Cannot copy folder with more than 500 items",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders/abc-123/copy"
}
```

### Conflict Errors

```
// Create folder with duplicate name
POST /api/folders
{
  "name": "Existing Folder",
  "parent_id": "parent-123"
}

Response: 409 Conflict
{
  "error": "NAME_CONFLICT",
  "message": "A folder with this name already exists in the parent",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

## 10. Security Considerations

### Error Message Sanitization

```
FUNCTION sanitizeErrorMessage(exception, isProduction):
  // Never expose sensitive information in production
  IF isProduction AND exception INSTANCEOF ServerException THEN
    RETURN "An unexpected error occurred"
  END IF

  // Remove SQL queries, stack traces, file paths
  message = exception.message()
  message = removeSqlQueries(message)
  message = removeFilePaths(message)
  message = removeStackTraces(message)

  RETURN message
END FUNCTION
```

### Rate Limiting Error Headers

```
// Include rate limit headers in response
Response: 429 Too Many Requests
Headers:
  X-RateLimit-Limit: 100
  X-RateLimit-Remaining: 0
  X-RateLimit-Reset: 1641816600
  Retry-After: 60

Body:
{
  "error": "RATE_LIMIT_EXCEEDED",
  "message": "Too many requests. Please try again in 60 seconds",
  "timestamp": "2025-01-10T10:30:00Z",
  "path": "/api/folders"
}
```

### Logging Sensitive Data

```
FUNCTION sanitizeRequestBody(body):
  sensitiveFields = ["password", "token", "apiKey", "secret"]

  FOR field IN sensitiveFields:
    IF body.contains(field) THEN
      body[field] = "[REDACTED]"
    END IF
  END FOR

  RETURN body
END FUNCTION
```
