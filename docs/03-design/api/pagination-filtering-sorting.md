# Pagination, Filtering & Sorting

## 1. Tổng quan

### 1.1 Mục đích
Tài liệu này định nghĩa các quy ước và implementation cho pagination, filtering và sorting trong API RepeatWise, đảm bảo hiệu suất và trải nghiệm người dùng tốt.

### 1.2 Lợi ích
- **Performance**: Giảm tải database và network
- **User Experience**: Tải dữ liệu nhanh hơn
- **Scalability**: Hỗ trợ large datasets
- **Consistency**: Chuẩn hóa cách xử lý dữ liệu

## 2. Pagination

### 2.1 Pagination Strategy

#### 2.1.1 Offset-based Pagination
```
GET /api/v1/learning-sets?page=0&size=20
```

**Parameters:**
- `page`: Số trang (bắt đầu từ 0)
- `size`: Số lượng items per page (mặc định: 20, max: 100)

#### 2.1.2 Cursor-based Pagination
```
GET /api/v1/learning-sets?cursor=abc123&size=20
```

**Parameters:**
- `cursor`: Token để xác định vị trí tiếp theo
- `size`: Số lượng items per page

### 2.2 Response Format

#### 2.2.1 Offset-based Response
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "set-uuid-1",
        "name": "Set học từ vựng",
        "description": "Set học từ vựng tiếng Anh",
        "created_at": "2024-01-01T00:00:00Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
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
      "unsorted": false,
      "empty": false
    },
    "empty": false
  },
  "message": "Thành công",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### 2.2.2 Cursor-based Response
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "set-uuid-1",
        "name": "Set học từ vựng",
        "description": "Set học từ vựng tiếng Anh",
        "created_at": "2024-01-01T00:00:00Z"
      }
    ],
    "pagination": {
      "nextCursor": "def456",
      "hasNext": true,
      "size": 20
    }
  },
  "message": "Thành công",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 2.3 Implementation

#### 2.3.1 Pagination Request DTO
```java
@Data
@Builder
public class PaginationRequest {
    @Min(0)
    private Integer page = 0;
    
    @Min(1)
    @Max(100)
    private Integer size = 20;
    
    private String cursor;
    
    @Valid
    private List<SortRequest> sort;
    
    @Valid
    private List<FilterRequest> filters;
}
```

#### 2.3.2 Pagination Response DTO
```java
@Data
@Builder
public class PaginationResponse<T> {
    private List<T> content;
    private PageableInfo pageable;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private int numberOfElements;
    private int size;
    private int number;
    private SortInfo sort;
    private boolean empty;
}

@Data
@Builder
public class PageableInfo {
    private int pageNumber;
    private int pageSize;
    private SortInfo sort;
}
```

#### 2.3.3 Pagination Service
```java
@Service
public class PaginationService {
    
    public Pageable createPageable(PaginationRequest request) {
        List<Sort.Order> orders = request.getSort().stream()
                .map(this::createSortOrder)
                .collect(Collectors.toList());
        
        Sort sort = Sort.by(orders);
        
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
    
    private Sort.Order createSortOrder(SortRequest sortRequest) {
        Sort.Direction direction = Sort.Direction.fromString(sortRequest.getDirection());
        return new Sort.Order(direction, sortRequest.getField());
    }
}
```

## 3. Filtering

### 3.1 Filter Strategy

#### 3.1.1 Query Parameter Filtering
```
GET /api/v1/learning-sets?status=active&createdAfter=2024-01-01&name=vocabulary
```

#### 3.1.2 JSON Filtering
```
GET /api/v1/learning-sets?filters=[{"field":"status","operator":"eq","value":"active"}]
```

### 3.2 Filter Operators

#### 3.2.1 Comparison Operators
- `eq`: Equal
- `ne`: Not equal
- `gt`: Greater than
- `gte`: Greater than or equal
- `lt`: Less than
- `lte`: Less than or equal
- `in`: In array
- `nin`: Not in array

#### 3.2.2 String Operators
- `contains`: Contains substring
- `startsWith`: Starts with
- `endsWith`: Ends with
- `regex`: Regular expression

#### 3.2.3 Date Operators
- `before`: Before date
- `after`: After date
- `between`: Between two dates

### 3.3 Filter Implementation

#### 3.3.1 Filter Request DTO
```java
@Data
@Builder
public class FilterRequest {
    private String field;
    private String operator;
    private Object value;
    private Object value2; // For between operator
}
```

#### 3.3.2 Filter Service
```java
@Service
public class FilterService {
    
    public Specification<LearningSet> createSpecification(List<FilterRequest> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            for (FilterRequest filter : filters) {
                Predicate predicate = createPredicate(root, criteriaBuilder, filter);
                predicates.add(predicate);
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    private Predicate createPredicate(Root<LearningSet> root, CriteriaBuilder cb, FilterRequest filter) {
        Path<Object> field = root.get(filter.getField());
        
        switch (filter.getOperator()) {
            case "eq":
                return cb.equal(field, filter.getValue());
            case "ne":
                return cb.notEqual(field, filter.getValue());
            case "gt":
                return cb.greaterThan(field, (Comparable) filter.getValue());
            case "gte":
                return cb.greaterThanOrEqualTo(field, (Comparable) filter.getValue());
            case "lt":
                return cb.lessThan(field, (Comparable) filter.getValue());
            case "lte":
                return cb.lessThanOrEqualTo(field, (Comparable) filter.getValue());
            case "in":
                return field.in((Collection<?>) filter.getValue());
            case "contains":
                return cb.like(field.as(String.class), "%" + filter.getValue() + "%");
            case "startsWith":
                return cb.like(field.as(String.class), filter.getValue() + "%");
            case "endsWith":
                return cb.like(field.as(String.class), "%" + filter.getValue());
            default:
                throw new IllegalArgumentException("Unsupported operator: " + filter.getOperator());
        }
    }
}
```

### 3.4 Common Filters

#### 3.4.1 Learning Set Filters
```java
public enum LearningSetFilter {
    STATUS("status", "Trạng thái set"),
    CREATED_AFTER("createdAfter", "Tạo sau ngày"),
    CREATED_BEFORE("createdBefore", "Tạo trước ngày"),
    NAME_CONTAINS("nameContains", "Tên chứa"),
    USER_ID("userId", "ID người dùng");
    
    private final String field;
    private final String description;
}
```

#### 3.4.2 Cycle Filters
```java
public enum CycleFilter {
    SET_ID("setId", "ID set học tập"),
    STATUS("status", "Trạng thái chu kỳ"),
    COMPLETED_AFTER("completedAfter", "Hoàn thành sau ngày"),
    COMPLETED_BEFORE("completedBefore", "Hoàn thành trước ngày");
    
    private final String field;
    private final String description;
}
```

## 4. Sorting

### 4.1 Sort Strategy

#### 4.1.1 Single Field Sorting
```
GET /api/v1/learning-sets?sort=createdAt,desc
```

#### 4.1.2 Multiple Field Sorting
```
GET /api/v1/learning-sets?sort=status,asc&sort=createdAt,desc
```

### 4.2 Sort Implementation

#### 4.2.1 Sort Request DTO
```java
@Data
@Builder
public class SortRequest {
    private String field;
    private String direction = "asc"; // asc, desc
}
```

#### 4.2.2 Sort Service
```java
@Service
public class SortService {
    
    public Sort createSort(List<SortRequest> sortRequests) {
        List<Sort.Order> orders = sortRequests.stream()
                .map(this::createSortOrder)
                .collect(Collectors.toList());
        
        return Sort.by(orders);
    }
    
    private Sort.Order createSortOrder(SortRequest sortRequest) {
        Sort.Direction direction = Sort.Direction.fromString(sortRequest.getDirection());
        return new Sort.Order(direction, sortRequest.getField());
    }
}
```

### 4.3 Sortable Fields

#### 4.3.1 Learning Set Sortable Fields
```java
public enum LearningSetSortableField {
    NAME("name", "Tên set"),
    CREATED_AT("createdAt", "Ngày tạo"),
    UPDATED_AT("updatedAt", "Ngày cập nhật"),
    STATUS("status", "Trạng thái");
    
    private final String field;
    private final String description;
}
```

#### 4.3.2 Cycle Sortable Fields
```java
public enum CycleSortableField {
    ORDER_NUMBER("orderNumber", "Thứ tự"),
    COMPLETED_AT("completedAt", "Ngày hoàn thành"),
    SCORE("score", "Điểm số"),
    STATUS("status", "Trạng thái");
    
    private final String field;
    private final String description;
}
```

## 5. Combined Implementation

### 5.1 Repository Layer
```java
@Repository
public interface LearningSetRepository extends JpaRepository<LearningSet, UUID>, JpaSpecificationExecutor<LearningSet> {
    
    Page<LearningSet> findAll(Specification<LearningSet> spec, Pageable pageable);
    
    @Query("SELECT ls FROM LearningSet ls WHERE ls.user.id = :userId")
    Page<LearningSet> findByUserId(@Param("userId") UUID userId, Pageable pageable);
}
```

### 5.2 Service Layer
```java
@Service
@Transactional
public class LearningSetService {
    
    @Autowired
    private LearningSetRepository repository;
    
    @Autowired
    private FilterService filterService;
    
    @Autowired
    private PaginationService paginationService;
    
    public PaginationResponse<LearningSetDTO> getLearningSets(PaginationRequest request, UUID userId) {
        // Create specification from filters
        Specification<LearningSet> spec = filterService.createSpecification(request.getFilters());
        
        // Add user filter
        spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        
        // Create pageable
        Pageable pageable = paginationService.createPageable(request);
        
        // Execute query
        Page<LearningSet> page = repository.findAll(spec, pageable);
        
        // Convert to DTOs
        List<LearningSetDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // Build response
        return PaginationResponse.<LearningSetDTO>builder()
                .content(content)
                .pageable(buildPageableInfo(page))
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .numberOfElements(page.getNumberOfElements())
                .size(page.getSize())
                .number(page.getNumber())
                .sort(buildSortInfo(page.getSort()))
                .empty(page.isEmpty())
                .build();
    }
}
```

### 5.3 Controller Layer
```java
@RestController
@RequestMapping("/api/v1/learning-sets")
public class LearningSetController {
    
    @Autowired
    private LearningSetService service;
    
    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<LearningSetDTO>>> getLearningSets(
            @Valid PaginationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        UUID userId = UUID.fromString(userDetails.getUsername());
        PaginationResponse<LearningSetDTO> response = service.getLearningSets(request, userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Thành công"));
    }
}
```

## 6. Performance Optimization

### 6.1 Database Indexing
```sql
-- Index for common filters
CREATE INDEX idx_learning_sets_user_status ON learning_sets(user_id, status);
CREATE INDEX idx_learning_sets_created_at ON learning_sets(created_at);
CREATE INDEX idx_learning_sets_name ON learning_sets(name);

-- Index for sorting
CREATE INDEX idx_learning_sets_user_created_at ON learning_sets(user_id, created_at DESC);
```

### 6.2 Query Optimization
```java
@Query("SELECT ls FROM LearningSet ls " +
       "LEFT JOIN FETCH ls.cycles " +
       "WHERE ls.user.id = :userId " +
       "ORDER BY ls.createdAt DESC")
Page<LearningSet> findByUserIdWithCycles(@Param("userId") UUID userId, Pageable pageable);
```

### 6.3 Caching
```java
@Cacheable(value = "learning-sets", key = "#userId + '_' + #request.hashCode()")
public PaginationResponse<LearningSetDTO> getLearningSets(PaginationRequest request, UUID userId) {
    // Implementation
}
```

## 7. Best Practices

### 7.1 Pagination Best Practices
1. **Default Page Size**: Đặt default page size hợp lý (20-50)
2. **Max Page Size**: Giới hạn max page size để tránh overload
3. **Consistent Format**: Sử dụng format response nhất quán
4. **Metadata**: Cung cấp đầy đủ metadata cho pagination

### 7.2 Filtering Best Practices
1. **Field Validation**: Validate tên field trước khi query
2. **Operator Validation**: Validate operator được hỗ trợ
3. **Value Validation**: Validate giá trị filter
4. **Security**: Prevent SQL injection

### 7.3 Sorting Best Practices
1. **Default Sort**: Đặt default sort order
2. **Field Validation**: Validate sortable fields
3. **Performance**: Index cho sortable fields
4. **Consistency**: Sử dụng consistent sort order

### 7.4 Implementation Best Practices
1. **Separation of Concerns**: Tách biệt logic pagination, filtering, sorting
2. **Reusability**: Tạo reusable components
3. **Testing**: Test các scenarios khác nhau
4. **Documentation**: Document API parameters 
