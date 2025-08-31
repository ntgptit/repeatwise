# Idempotency & Retry Strategy

## 1. Tổng quan

### 1.1 Mục đích
Tài liệu này định nghĩa chiến lược xử lý idempotency và retry cho API RepeatWise, đảm bảo tính nhất quán dữ liệu và khả năng chịu lỗi cao.

### 1.2 Vấn đề cần giải quyết
- **Network Failures**: Mất kết nối mạng tạm thời
- **Timeout Issues**: Request timeout do server overload
- **Duplicate Requests**: Client gửi nhiều lần cùng một request
- **Data Consistency**: Đảm bảo dữ liệu không bị duplicate hoặc inconsistent

## 2. Idempotency

### 2.1 Định nghĩa
Idempotency là tính chất của một operation mà khi thực hiện nhiều lần với cùng input sẽ cho kết quả giống nhau như khi thực hiện một lần.

### 2.2 Idempotency Key
```http
POST /api/v1/learning-sets
Content-Type: application/json
X-Idempotency-Key: abc123-def456-ghi789

{
  "name": "Set học từ vựng",
  "description": "Set học từ vựng tiếng Anh"
}
```

### 2.3 Idempotency Rules
1. **Same Key, Same Response**: Cùng key phải trả về cùng response
2. **Key Expiration**: Key có thời hạn 24 giờ
3. **Key Uniqueness**: Key phải unique trong 24 giờ
4. **Key Format**: UUID v4 hoặc custom format

### 2.4 Idempotent Operations

#### 2.4.1 Create Operations
- **Endpoint**: `POST /api/v1/learning-sets`
- **Behavior**: Nếu key đã tồn tại, trả về resource đã tạo
- **Storage**: Lưu key + response trong cache

#### 2.4.2 Update Operations
- **Endpoint**: `PUT /api/v1/learning-sets/{id}`
- **Behavior**: Cập nhật resource, trả về kết quả mới
- **Storage**: Lưu key + response trong cache

#### 2.4.3 Delete Operations
- **Endpoint**: `DELETE /api/v1/learning-sets/{id}`
- **Behavior**: Xóa resource, trả về 204
- **Storage**: Lưu key + response trong cache

### 2.5 Implementation

#### 2.5.1 Idempotency Key Generation
```java
@Component
public class IdempotencyKeyGenerator {
    
    public String generateKey() {
        return UUID.randomUUID().toString();
    }
    
    public String generateKeyFromRequest(String userId, String operation, String resourceId) {
        return DigestUtils.sha256Hex(userId + operation + resourceId + System.currentTimeMillis());
    }
}
```

#### 2.5.2 Idempotency Service
```java
@Service
public class IdempotencyService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public Optional<ApiResponse> getCachedResponse(String key) {
        String cached = redisTemplate.opsForValue().get("idempotency:" + key);
        return Optional.ofNullable(cached)
                .map(response -> objectMapper.readValue(cached, ApiResponse.class));
    }
    
    public void cacheResponse(String key, ApiResponse response, Duration ttl) {
        String cacheKey = "idempotency:" + key;
        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(response), ttl);
    }
}
```

#### 2.5.3 Idempotency Interceptor
```java
@Component
public class IdempotencyInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String idempotencyKey = request.getHeader("X-Idempotency-Key");
        
        if (idempotencyKey != null && isIdempotentOperation(request)) {
            Optional<ApiResponse> cachedResponse = idempotencyService.getCachedResponse(idempotencyKey);
            
            if (cachedResponse.isPresent()) {
                writeResponse(response, cachedResponse.get());
                return false; // Stop processing
            }
        }
        
        return true;
    }
}
```

## 3. Retry Strategy

### 3.1 Retryable Operations
- **GET Requests**: Retry cho network failures
- **POST Requests**: Retry với idempotency key
- **PUT/PATCH Requests**: Retry với idempotency key
- **DELETE Requests**: Retry với idempotency key

### 3.2 Non-Retryable Operations
- **Authentication Failures**: Không retry
- **Validation Errors**: Không retry
- **Business Logic Errors**: Không retry
- **4xx Client Errors**: Không retry

### 3.3 Retry Configuration

#### 3.3.1 Exponential Backoff
```yaml
retry:
  max-attempts: 3
  initial-interval: 1000ms
  multiplier: 2.0
  max-interval: 10000ms
```

#### 3.3.2 Retry Conditions
```java
@Component
public class RetryPolicy {
    
    public boolean shouldRetry(Exception exception, int attempt) {
        return attempt < maxAttempts && isRetryableException(exception);
    }
    
    private boolean isRetryableException(Exception exception) {
        return exception instanceof ConnectException ||
               exception instanceof SocketTimeoutException ||
               exception instanceof HttpServerErrorException ||
               exception instanceof ResourceAccessException;
    }
}
```

### 3.4 Client-Side Retry

#### 3.4.1 Retry Template
```java
@Configuration
public class RetryConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        
        retryTemplate.setRetryPolicy(retryPolicy);
        
        return retryTemplate;
    }
}
```

#### 3.4.2 Retryable Service
```java
@Service
public class LearningSetService {
    
    @Autowired
    private RetryTemplate retryTemplate;
    
    public LearningSet createSet(CreateSetRequest request) {
        return retryTemplate.execute(context -> {
            String idempotencyKey = generateIdempotencyKey(request);
            return createSetWithIdempotency(request, idempotencyKey);
        });
    }
    
    private LearningSet createSetWithIdempotency(CreateSetRequest request, String idempotencyKey) {
        // Implementation with idempotency
    }
}
```

### 3.5 Server-Side Retry

#### 3.5.1 Circuit Breaker
```java
@Component
public class CircuitBreakerService {
    
    private CircuitBreaker circuitBreaker;
    
    public CircuitBreakerService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(60))
                .ringBufferSizeInHalfOpenState(2)
                .ringBufferSizeInClosedState(10)
                .build();
        
        this.circuitBreaker = CircuitBreaker.of("learning-set-service", config);
    }
    
    public <T> T execute(Supplier<T> supplier) {
        return circuitBreaker.executeSupplier(supplier);
    }
}
```

#### 3.5.2 Bulkhead Pattern
```java
@Component
public class BulkheadService {
    
    private Bulkhead bulkhead;
    
    public BulkheadService() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(10)
                .maxWaitDuration(Duration.ofSeconds(5))
                .build();
        
        this.bulkhead = Bulkhead.of("learning-set-service", config);
    }
    
    public <T> T execute(Supplier<T> supplier) {
        return bulkhead.executeSupplier(supplier);
    }
}
```

## 4. Error Handling

### 4.1 Retryable Errors
```java
public enum RetryableError {
    NETWORK_TIMEOUT("NETWORK_TIMEOUT", "Lỗi timeout mạng"),
    SERVER_ERROR("SERVER_ERROR", "Lỗi server"),
    DATABASE_CONNECTION("DB_CONNECTION", "Lỗi kết nối database"),
    EXTERNAL_SERVICE_UNAVAILABLE("EXT_SERVICE_UNAVAILABLE", "Dịch vụ bên ngoài không khả dụng");
    
    private final String code;
    private final String message;
}
```

### 4.2 Non-Retryable Errors
```java
public enum NonRetryableError {
    VALIDATION_ERROR("VALIDATION_ERROR", "Lỗi validation"),
    AUTHENTICATION_ERROR("AUTH_ERROR", "Lỗi xác thực"),
    AUTHORIZATION_ERROR("FORBIDDEN", "Lỗi quyền truy cập"),
    BUSINESS_LOGIC_ERROR("BUSINESS_ERROR", "Lỗi logic nghiệp vụ");
    
    private final String code;
    private final String message;
}
```

## 5. Monitoring & Observability

### 5.1 Metrics
- **Retry Count**: Số lần retry per operation
- **Retry Success Rate**: Tỷ lệ thành công sau retry
- **Idempotency Hit Rate**: Tỷ lệ cache hit cho idempotency
- **Circuit Breaker State**: Trạng thái circuit breaker

### 5.2 Logging
```json
{
  "timestamp": "2024-01-01T00:00:00Z",
  "level": "INFO",
  "operation": "create_learning_set",
  "idempotency_key": "abc123-def456",
  "retry_attempt": 2,
  "max_retries": 3,
  "error": "Connection timeout",
  "user_id": "user-uuid"
}
```

### 5.3 Alerting
- **High Retry Rate**: Alert khi retry rate > 10%
- **Circuit Breaker Open**: Alert khi circuit breaker mở
- **Idempotency Key Collision**: Alert khi có key collision

## 6. Best Practices

### 6.1 Idempotency Best Practices
1. **Use UUIDs**: Sử dụng UUID cho idempotency keys
2. **Set TTL**: Đặt thời gian hết hạn cho cached responses
3. **Validate Keys**: Validate format của idempotency keys
4. **Monitor Usage**: Theo dõi usage của idempotency

### 6.2 Retry Best Practices
1. **Exponential Backoff**: Sử dụng exponential backoff
2. **Jitter**: Thêm jitter để tránh thundering herd
3. **Max Attempts**: Giới hạn số lần retry
4. **Timeout**: Đặt timeout cho mỗi retry attempt

### 6.3 Implementation Best Practices
1. **Separation of Concerns**: Tách biệt retry logic và business logic
2. **Configuration**: Externalize retry configuration
3. **Testing**: Test retry scenarios
4. **Documentation**: Document retry behavior 
