# API Versioning Strategy

## 1. Tổng quan

### 1.1 Mục đích
Tài liệu này định nghĩa chiến lược versioning cho API RepeatWise, đảm bảo backward compatibility và smooth migration khi có breaking changes.

### 1.2 Nguyên tắc
- **Backward Compatibility**: Duy trì compatibility với phiên bản cũ
- **Gradual Migration**: Cho phép migration từ từ
- **Clear Communication**: Thông báo rõ ràng về changes
- **Deprecation Policy**: Chính sách deprecation rõ ràng

## 2. Versioning Approaches

### 2.1 URL Versioning (Recommended)

#### 2.1.1 URL Structure
```
/api/v1/learning-sets
/api/v2/learning-sets
/api/v3/learning-sets
```

#### 2.1.2 Advantages
- **Clear Separation**: Tách biệt rõ ràng các version
- **Easy Routing**: Dễ dàng route requests
- **Independent Deployment**: Deploy độc lập
- **Clear Documentation**: Documentation rõ ràng

#### 2.1.3 Disadvantages
- **URL Pollution**: URL dài hơn
- **Version Proliferation**: Có thể có nhiều version
- **Maintenance Overhead**: Phải maintain nhiều version

### 2.2 Header Versioning

#### 2.2.1 Header Structure
```http
GET /api/learning-sets
Accept: application/vnd.repeatwise.v1+json
```

#### 2.2.2 Advantages
- **Clean URLs**: URL sạch sẽ
- **Content Negotiation**: Sử dụng HTTP content negotiation
- **Flexible**: Linh hoạt trong versioning

#### 2.2.3 Disadvantages
- **Less Visible**: Version ít visible
- **Complex Routing**: Routing phức tạp hơn
- **Tool Support**: Ít tool hỗ trợ

### 2.3 Query Parameter Versioning

#### 2.3.1 Query Structure
```
GET /api/learning-sets?version=1
GET /api/learning-sets?version=2
```

#### 2.3.2 Advantages
- **Simple**: Đơn giản
- **Easy Testing**: Dễ test
- **Flexible**: Linh hoạt

#### 2.3.3 Disadvantages
- **URL Pollution**: URL bị pollute
- **Caching Issues**: Vấn đề với caching
- **Less RESTful**: Ít RESTful hơn

## 3. Versioning Strategy for RepeatWise

### 3.1 Primary Approach: URL Versioning
RepeatWise sẽ sử dụng URL versioning làm primary approach:

```
/api/v1/learning-sets
/api/v1/users
/api/v1/cycles
/api/v1/statistics
```

### 3.2 Version Naming Convention
- **Major Version**: `v1`, `v2`, `v3` (breaking changes)
- **Minor Version**: `v1.1`, `v1.2` (backward compatible changes)
- **Patch Version**: `v1.1.1` (bug fixes)

### 3.3 Version Lifecycle

#### 3.3.1 Version States
1. **Current**: Version hiện tại, được recommend sử dụng
2. **Supported**: Version được support nhưng không recommend
3. **Deprecated**: Version sẽ bị remove trong tương lai
4. **Retired**: Version đã bị remove

#### 3.3.2 Timeline
```
v1: Current (2024-2025)
v2: Development (2025)
v3: Planning (2026)
```

## 4. Breaking Changes Policy

### 4.1 Definition of Breaking Changes
- **Removed Endpoints**: Xóa endpoints
- **Changed Response Structure**: Thay đổi cấu trúc response
- **Changed Request Structure**: Thay đổi cấu trúc request
- **Changed HTTP Methods**: Thay đổi HTTP methods
- **Changed Status Codes**: Thay đổi status codes

### 4.2 Non-Breaking Changes
- **Added Fields**: Thêm fields mới (optional)
- **Added Endpoints**: Thêm endpoints mới
- **Enhanced Validation**: Tăng cường validation
- **Performance Improvements**: Cải thiện performance

### 4.3 Migration Strategy

#### 4.3.1 Parallel Support
```
/api/v1/learning-sets (Deprecated)
/api/v2/learning-sets (Current)
```

#### 4.3.2 Migration Timeline
1. **Announcement**: Thông báo deprecation (6 tháng trước)
2. **Parallel Support**: Support cả 2 version (12 tháng)
3. **Deprecation**: Mark version cũ là deprecated
4. **Removal**: Remove version cũ

## 5. Implementation

### 5.1 Controller Structure

#### 5.1.1 Version 1 Controller
```java
@RestController
@RequestMapping("/api/v1/learning-sets")
@Deprecated(since = "2.0.0", forRemoval = true)
public class LearningSetControllerV1 {
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<LearningSetDTO>>> getLearningSets() {
        // V1 implementation
    }
}
```

#### 5.1.2 Version 2 Controller
```java
@RestController
@RequestMapping("/api/v2/learning-sets")
public class LearningSetControllerV2 {
    
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<LearningSetDTO>>> getLearningSets(
            @Valid PaginationRequest request) {
        // V2 implementation with pagination
    }
}
```

### 5.2 Service Layer Versioning

#### 5.2.1 Service Interface
```java
public interface LearningSetService {
    List<LearningSetDTO> getLearningSets();
}

public interface LearningSetServiceV2 extends LearningSetService {
    PaginationResponse<LearningSetDTO> getLearningSets(PaginationRequest request);
}
```

#### 5.2.2 Service Implementation
```java
@Service
public class LearningSetServiceImpl implements LearningSetServiceV2 {
    
    @Override
    public List<LearningSetDTO> getLearningSets() {
        // V1 implementation
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaginationResponse<LearningSetDTO> getLearningSets(PaginationRequest request) {
        // V2 implementation with pagination
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<LearningSet> page = repository.findAll(pageable);
        
        return buildPaginationResponse(page);
    }
}
```

### 5.3 DTO Versioning

#### 5.3.1 Version 1 DTO
```java
@Data
@Builder
public class LearningSetDTO {
    private String id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}
```

#### 5.3.2 Version 2 DTO
```java
@Data
@Builder
public class LearningSetDTOV2 {
    private String id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer cycleCount;
    private Double averageScore;
}
```

### 5.4 Configuration Management

#### 5.4.1 Version Configuration
```yaml
api:
  versioning:
    current-version: "v2"
    supported-versions: ["v1", "v2"]
    deprecated-versions: ["v1"]
    retirement-schedule:
      v1: "2025-12-31"
```

#### 5.4.2 Version Configuration Service
```java
@Service
public class VersionConfigurationService {
    
    @Value("${api.versioning.current-version}")
    private String currentVersion;
    
    @Value("${api.versioning.supported-versions}")
    private List<String> supportedVersions;
    
    @Value("${api.versioning.deprecated-versions}")
    private List<String> deprecatedVersions;
    
    public boolean isVersionSupported(String version) {
        return supportedVersions.contains(version);
    }
    
    public boolean isVersionDeprecated(String version) {
        return deprecatedVersions.contains(version);
    }
    
    public String getCurrentVersion() {
        return currentVersion;
    }
}
```

## 6. Documentation Strategy

### 6.1 API Documentation

#### 6.1.1 OpenAPI Specification
```yaml
openapi: 3.0.0
info:
  title: RepeatWise API
  version: 2.0.0
  description: |
    RepeatWise API v2.0.0
    
    ## Version Information
    - Current Version: v2
    - Supported Versions: v1, v2
    - Deprecated Versions: v1 (will be removed on 2025-12-31)
    
    ## Migration Guide
    See /docs/migration/v1-to-v2.md for migration guide from v1 to v2.
```

#### 6.1.2 Version-specific Documentation
```
/docs/api/v1/
/docs/api/v2/
/docs/migration/v1-to-v2.md
```

### 6.2 Migration Guides

#### 6.2.1 Migration Guide Template
```markdown
# Migration Guide: v1 to v2

## Overview
This guide helps you migrate from RepeatWise API v1 to v2.

## Breaking Changes
1. **Pagination**: All list endpoints now require pagination
2. **Response Format**: Changed response structure
3. **Authentication**: Enhanced JWT token format

## Migration Steps
1. Update API base URL to `/api/v2/`
2. Add pagination parameters to list requests
3. Update response handling code
4. Update authentication headers

## Code Examples
### Before (v1)
```javascript
GET /api/v1/learning-sets
```

### After (v2)
```javascript
GET /api/v2/learning-sets?page=0&size=20
```
```

## 7. Testing Strategy

### 7.1 Version Testing

#### 7.1.1 Multi-version Testing
```java
@SpringBootTest
class LearningSetControllerTest {
    
    @Test
    void testV1Endpoint() {
        // Test v1 endpoint
        mockMvc.perform(get("/api/v1/learning-sets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
    
    @Test
    void testV2Endpoint() {
        // Test v2 endpoint
        mockMvc.perform(get("/api/v2/learning-sets")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").exists());
    }
}
```

#### 7.1.2 Backward Compatibility Testing
```java
@Test
void testBackwardCompatibility() {
    // Test that v1 response can be parsed by v1 client
    String v1Response = mockMvc.perform(get("/api/v1/learning-sets"))
            .andReturn()
            .getResponse()
            .getContentAsString();
    
    ObjectMapper mapper = new ObjectMapper();
    ApiResponse<List<LearningSetDTO>> response = mapper.readValue(v1Response, 
            new TypeReference<ApiResponse<List<LearningSetDTO>>>() {});
    
    assertNotNull(response.getData());
}
```

### 7.2 Integration Testing

#### 7.2.1 Version Migration Testing
```java
@Test
void testVersionMigration() {
    // Test that data created in v1 can be accessed in v2
    String setId = createLearningSetV1();
    
    mockMvc.perform(get("/api/v2/learning-sets/" + setId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(setId));
}
```

## 8. Monitoring & Observability

### 8.1 Version Usage Metrics
```java
@Component
public class VersionMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public void recordApiCall(String version, String endpoint, int statusCode) {
        Counter.builder("api.calls")
                .tag("version", version)
                .tag("endpoint", endpoint)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry)
                .increment();
    }
}
```

### 8.2 Version Deprecation Alerts
```java
@Component
public class VersionDeprecationAlert {
    
    public void checkDeprecatedVersionUsage(String version, String userAgent) {
        if (isVersionDeprecated(version)) {
            log.warn("Deprecated version {} used by {}", version, userAgent);
            // Send alert to monitoring system
        }
    }
}
```

## 9. Best Practices

### 9.1 Versioning Best Practices
1. **Plan Ahead**: Plan versioning strategy từ đầu
2. **Document Changes**: Document tất cả breaking changes
3. **Provide Migration Path**: Cung cấp migration path rõ ràng
4. **Monitor Usage**: Monitor usage của các version

### 9.2 Implementation Best Practices
1. **Separate Controllers**: Tách biệt controllers cho từng version
2. **Reuse Services**: Reuse service logic khi có thể
3. **Version Configuration**: Externalize version configuration
4. **Comprehensive Testing**: Test tất cả versions

### 9.3 Communication Best Practices
1. **Early Announcement**: Thông báo sớm về deprecation
2. **Clear Timeline**: Cung cấp timeline rõ ràng
3. **Migration Support**: Hỗ trợ migration
4. **Documentation**: Maintain documentation đầy đủ 
