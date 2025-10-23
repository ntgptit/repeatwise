# Logging System - Spring AOP Implementation

## Overview

Hệ thống logging nâng cấp sử dụng Spring AOP để tự động ghi log cho các phương thức, theo dõi hiệu năng, và quản lý context logging.

## Components

### 1. Annotations

#### `@Loggable`
Tự động ghi log cho method entry/exit, parameters, return values và exceptions.

```java
@Loggable(
    event = LogEvent.USER_GET_PROFILE,
    level = LogLevel.INFO,
    logArgs = true,
    logResult = true,
    logExecutionTime = true
)
public UserDTO getProfile(Long userId) {
    // implementation
}
```

**Parameters:**
- `event`: LogEvent để tag log (default: START)
- `level`: Mức độ log (TRACE, DEBUG, INFO, WARN, ERROR)
- `logArgs`: Có log parameters không (default: true)
- `logResult`: Có log return value không (default: true)
- `logExecutionTime`: Có log thời gian thực thi không (default: false)
- `message`: Custom message (optional)

#### `@PerformanceLog`
Theo dõi performance và cảnh báo khi vượt ngưỡng.

```java
@PerformanceLog(
    operation = "ProcessLargeDataset",
    warnThresholdMs = 1000,
    errorThresholdMs = 5000,
    alwaysLog = false
)
public void processData(List<Data> data) {
    // implementation
}
```

**Parameters:**
- `operation`: Tên operation (default: method name)
- `warnThresholdMs`: Ngưỡng cảnh báo (ms) (default: 1000)
- `errorThresholdMs`: Ngưỡng lỗi (ms) (default: 5000)
- `logArgs`: Log arguments không (default: false)
- `alwaysLog`: Luôn log dù không vượt ngưỡng (default: false)

### 2. LogEvent Enum

Enhanced với metadata methods:

```java
LogEvent event = LogEvent.USER_GET_PROFILE;
String category = event.getCategory();        // "USER"
LogLevel level = event.getSuggestedLevel();   // LogLevel.INFO
boolean isHighPriority = event.isHighPriority(); // false
```

### 3. LogContext - MDC Support

Quản lý Mapped Diagnostic Context cho distributed tracing:

```java
// Setup context
LogContext.setRequestId(UUID.randomUUID().toString());
LogContext.setUserId(userId);
LogContext.setOperation("ProcessPayment");

// MDC values tự động xuất hiện trong mọi log
log.info("Processing payment"); // Includes requestId, userId, operation

// Clear context khi xong
LogContext.clear();
```

**Available Methods:**
- `setRequestId(String)` / `generateRequestId()`
- `setUserId(Long)`
- `setSessionId(String)`
- `setCorrelationId(String)`
- `setOperation(String)`
- `setClientIp(String)`
- `setUserAgent(String)`
- `set(String key, String value)` - custom values
- `clear()` - **QUAN TRỌNG**: Gọi khi kết thúc request

### 4. Aspects

#### LoggingAspect
- Tự động log cho methods có `@Loggable`
- Fallback exception logging cho Service và Controller layers
- Format: `[EVENT] ClassName.methodName - STATUS | details`

#### PerformanceAspect
- Monitor methods có `@PerformanceLog`
- Tự động track slow queries (Repository > 100ms)
- Tự động track slow requests (Controller > 1000ms)
- Format: `[PERFORMANCE] operation=X | executionTime=Xms | status=OK/SLOW/CRITICAL`

#### RequestLoggingFilter
- Tự động log tất cả HTTP requests/responses
- Setup MDC context cho mỗi request
- Log request body (POST/PUT/PATCH)
- Log response body khi có error
- Tự động clear MDC sau request

## Usage Examples

### Example 1: Service Method Logging

```java
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Loggable(
        event = LogEvent.USER_GET_PROFILE,
        level = LogLevel.INFO,
        logArgs = true,
        logResult = false  // Don't log user data
    )
    @Override
    public UserDTO getProfile(Long userId) {
        log.info("Fetching user profile");
        // implementation
        return userDTO;
    }

    @Loggable(event = LogEvent.USER_UPDATE_PROFILE)
    @PerformanceLog(warnThresholdMs = 500)
    @Override
    public UserDTO updateProfile(Long userId, UpdateUserDTO dto) {
        // Both logging and performance monitoring
        return updatedUser;
    }
}
```

**Output:**
```
[USER_GET_PROFILE] UserServiceImpl.getProfile - ENTRY | args=[123]
Fetching user profile
[USER_GET_PROFILE] UserServiceImpl.getProfile - EXIT
[PERFORMANCE] operation=UserServiceImpl.updateProfile | executionTime=350ms | status=OK
```

### Example 2: Controller with Performance Monitoring

```java
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @PostMapping
    @Loggable(
        event = LogEvent.FOLDER_CREATE_START,
        logArgs = true,
        logExecutionTime = true
    )
    @PerformanceLog(
        operation = "CreateFolder",
        warnThresholdMs = 500,
        alwaysLog = true
    )
    public ResponseEntity<FolderDTO> createFolder(@RequestBody CreateFolderDTO dto) {
        // implementation
    }
}
```

**Output:**
```
[HTTP_REQUEST] POST /api/folders | client=192.168.1.1 | body={...}
[FOLDER_CREATE_START] FolderController.createFolder - ENTRY | args=[CreateFolderDTO(...)]
[FOLDER_CREATE_START] FolderController.createFolder - EXIT | executionTime=250ms
[PERFORMANCE] operation=CreateFolder | executionTime=250ms | status=OK
[HTTP_RESPONSE] POST /api/folders | status=201 | duration=255ms
```

### Example 3: Custom MDC Context

```java
@Service
public class NotificationService {

    public void sendNotification(Long userId, String message) {
        try {
            LogContext.setUserId(userId);
            LogContext.setOperation("SendNotification");

            log.info("Starting notification send");
            // All logs will include userId and operation

            emailService.send(message);
            log.info("Notification sent successfully");

        } finally {
            LogContext.clear(); // Always clear!
        }
    }

    // Or use withContext for automatic cleanup
    public void sendBulkNotifications(List<Long> userIds) {
        userIds.forEach(userId -> {
            LogContext.withContext(
                Map.of("userId", userId.toString()),
                () -> {
                    log.info("Processing notification");
                    sendNotification(userId, "message");
                }
            );
        });
    }
}
```

### Example 4: Exception Logging

```java
@Service
public class DeckService {

    @Loggable(
        event = LogEvent.DECK_CREATE_START,
        level = LogLevel.INFO
    )
    public DeckDTO createDeck(CreateDeckDTO dto) {
        // Nếu throw exception, aspect tự động log:
        // [DECK_CREATE_START] DeckService.createDeck - EXCEPTION |
        // type=ResourceNotFoundException | message=Folder not found | executionTime=50ms

        throw new ResourceNotFoundException("Folder not found");
    }
}
```

### Example 5: Class-Level Annotation

```java
@Service
@Loggable(level = LogLevel.DEBUG)  // Apply to all methods
public class StatisticsService {

    // All methods automatically logged at DEBUG level
    public void calculateStats() {
        // implementation
    }

    // Override class-level settings
    @Loggable(
        event = LogEvent.FOLDER_STATS_CALCULATED,
        level = LogLevel.INFO
    )
    public Stats getDetailedStats() {
        // implementation
    }
}
```

## Automatic Logging Features

### 1. Repository Performance Monitoring
Tự động cảnh báo slow queries (> 100ms):
```
[PERFORMANCE] [SLOW_QUERY] repository=UserRepository | method=findByEmail | executionTime=150ms
```

### 2. Controller Request Tracking
Tự động log slow requests:
```
[PERFORMANCE] [SLOW_REQUEST] controller=DeckController | endpoint=createDeck | executionTime=1200ms | success=true
```

### 3. Exception Fallback
Service và Controller exceptions tự động được log dù không có `@Loggable`:
```
[EX_INTERNAL_SERVER] UserService.getProfile - Unhandled exception: User not found
```

## Best Practices

### 1. Sensitive Data
```java
// DON'T log sensitive data
@Loggable(
    event = LogEvent.AUTH_LOGIN_START,
    logArgs = false,      // Password in args!
    logResult = false     // Token in result!
)
public AuthResponse login(LoginDTO dto) {
    // implementation
}
```

### 2. Performance Critical Code
```java
// Use @PerformanceLog for operations that might be slow
@PerformanceLog(
    warnThresholdMs = 100,   // DB operations should be fast
    alwaysLog = false        // Only log if slow
)
public List<User> findUsers() {
    // implementation
}
```

### 3. MDC Cleanup
```java
// ALWAYS clear MDC to prevent memory leaks
try {
    LogContext.setUserId(userId);
    // do work
} finally {
    LogContext.clear();  // CRITICAL!
}

// Or use withContext for automatic cleanup
LogContext.withContext(context, () -> {
    // work here
}); // Automatic cleanup
```

### 4. Structured Logging
```java
// Use LogEvent for structured, greppable logs
log.info("[{}] Processing user registration: userId={}, email={}",
    LogEvent.USER_CREATE_START, userId, email);

// Easy to grep:
// grep "USER_CREATE_START" logs/app.log
```

### 5. Log Levels
```java
// Use appropriate log levels
log.trace("Detailed debugging info");        // Development only
log.debug("Step-by-step execution");         // Development/troubleshooting
log.info("Normal business events");          // Production
log.warn("Recoverable issues");              // Production - needs attention
log.error("Errors requiring intervention");  // Production - immediate attention
```

## Configuration

### application.yml
```yaml
logging:
  level:
    com.repeatwise: INFO
    com.repeatwise.log: DEBUG  # Enable AOP logging details

  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{userId}] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{userId}] %logger{36} - %msg%n"

  # MDC pattern includes requestId and userId
```

### logback-spring.xml (Advanced)
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{userId}] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/repeatwise.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{requestId}] [%X{userId}] [%X{operation}] %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/repeatwise.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
    </appender>

    <logger name="com.repeatwise.log" level="DEBUG"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Migration Guide

### Before (Manual Logging)
```java
@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserDTO getProfile(Long userId) {
        log.info("Getting user profile for userId: {}", userId);
        try {
            UserDTO user = findUser(userId);
            log.info("Successfully retrieved user profile");
            return user;
        } catch (Exception e) {
            log.error("Error getting user profile: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

### After (AOP Logging)
```java
@Service
public class UserService {
    @Loggable(
        event = LogEvent.USER_GET_PROFILE,
        logArgs = true,
        logExecutionTime = true
    )
    public UserDTO getProfile(Long userId) {
        return findUser(userId);
        // Logging handled by AOP!
    }
}
```

## Troubleshooting

### AOP Not Working?
1. Verify `spring-boot-starter-aop` dependency
2. Check `@EnableAspectJAutoProxy` in configuration
3. Ensure methods are public
4. Ensure calling from outside the class (Spring proxy limitation)

### MDC Values Not Appearing?
1. Check logback pattern includes `%X{key}`
2. Verify `LogContext.set*()` called before logging
3. Ensure not cleared too early

### Performance Impact?
- AOP adds ~1-5ms overhead per method
- Disable verbose logging in production
- Use `alwaysLog=false` for `@PerformanceLog`
- Adjust log levels appropriately

## Summary

✅ **Created:**
- `@Loggable` - Automatic method logging
- `@PerformanceLog` - Performance monitoring
- `LogEvent` - Enhanced with metadata
- `LogContext` - MDC management
- `LoggingAspect` - AOP logging implementation
- `PerformanceAspect` - Performance tracking
- `RequestLoggingFilter` - HTTP request/response logging

✅ **Features:**
- Automatic entry/exit logging
- Performance monitoring with thresholds
- MDC context for request tracking
- Structured logging with LogEvent
- Sensitive data protection
- Exception tracking
- HTTP request/response logging
