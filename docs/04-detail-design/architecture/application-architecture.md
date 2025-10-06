# Application Architecture - Detail Design

## 1. Tổng quan

Tài liệu này mô tả chi tiết kiến trúc ứng dụng RepeatWise, bao gồm cấu trúc code, design patterns, và implementation guidelines để các developer có thể code ngay lập tức.

## 2. Layered Architecture Implementation

### 2.1 Package Structure

```
com.repeatwise
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── DatabaseConfig.java
│   ├── RedisConfig.java
│   └── WebConfig.java
├── controller/             # REST Controllers
│   ├── AuthController.java
│   ├── SetController.java
│   ├── CycleController.java
│   ├── ReminderController.java
│   ├── StatisticsController.java
│   └── DataController.java
├── service/               # Business Logic Layer
│   ├── AuthService.java
│   ├── SetService.java
│   ├── CycleService.java
│   ├── ReminderService.java
│   ├── StatisticsService.java
│   ├── NotificationService.java
│   ├── DataService.java
│   └── SrsAlgorithmService.java
├── repository/            # Data Access Layer
│   ├── UserRepository.java
│   ├── SetRepository.java
│   ├── CycleRepository.java
│   ├── ReviewRepository.java
│   └── ReminderRepository.java
├── entity/               # JPA Entities
│   ├── User.java
│   ├── Set.java
│   ├── Cycle.java
│   ├── Review.java
│   └── Reminder.java
├── dto/                  # Data Transfer Objects
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── CreateSetRequest.java
│   │   └── UpdateCycleRequest.java
│   └── response/
│       ├── LoginResponse.java
│       ├── SetResponse.java
│       └── StatisticsResponse.java
├── exception/            # Custom Exceptions
│   ├── BusinessException.java
│   ├── ValidationException.java
│   └── NotFoundException.java
├── security/             # Security Components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── util/                 # Utility Classes
│   ├── DateUtils.java
│   ├── ValidationUtils.java
│   └── SecurityUtils.java
└── scheduler/            # Background Jobs
    ├── ReminderScheduler.java
    └── StatisticsScheduler.java
```

### 2.2 Controller Layer Implementation

#### 2.2.1 Base Controller Pattern
```java
@RestController
@RequestMapping("/api/v1")
@Validated
@Slf4j
public abstract class BaseController {
    
    protected ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    protected ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(data));
    }
    
    protected ResponseEntity<ApiResponse<Void>> noContent() {
        return ResponseEntity.noContent().build();
    }
    
    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("VALIDATION_ERROR", ex.getMessage()));
    }
    
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}
```

#### 2.2.2 Set Controller Implementation
```java
@RestController
@RequestMapping("/api/v1/sets")
@RequiredArgsConstructor
@Slf4j
public class SetController extends BaseController {
    
    private final SetService setService;
    
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<SetResponse>>> getSets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal String userId) {
        
        log.info("Getting sets for user: {}, page: {}, size: {}", userId, page, size);
        
        SetSearchCriteria criteria = SetSearchCriteria.builder()
            .userId(userId)
            .status(status)
            .page(page)
            .size(size)
            .sort(sort)
            .build();
            
        Page<SetResponse> sets = setService.getSets(criteria);
        return success(sets);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<SetResponse>> createSet(
            @Valid @RequestBody CreateSetRequest request,
            @AuthenticationPrincipal String userId) {
        
        log.info("Creating set for user: {}, name: {}", userId, request.getName());
        
        SetResponse set = setService.createSet(request, userId);
        return created(set);
    }
    
    @GetMapping("/{setId}")
    @PreAuthorize("@resourceAuth.canAccessResource(#userId, #setId, 'read:own_sets')")
    public ResponseEntity<ApiResponse<SetDetailResponse>> getSet(
            @PathVariable UUID setId,
            @AuthenticationPrincipal String userId) {
        
        log.info("Getting set details: {} for user: {}", setId, userId);
        
        SetDetailResponse set = setService.getSetDetail(setId, userId);
        return success(set);
    }
    
    @PutMapping("/{setId}")
    @PreAuthorize("@resourceAuth.canAccessResource(#userId, #setId, 'update:own_sets')")
    public ResponseEntity<ApiResponse<SetResponse>> updateSet(
            @PathVariable UUID setId,
            @Valid @RequestBody UpdateSetRequest request,
            @AuthenticationPrincipal String userId) {
        
        log.info("Updating set: {} for user: {}", setId, userId);
        
        SetResponse set = setService.updateSet(setId, request, userId);
        return success(set);
    }
    
    @DeleteMapping("/{setId}")
    @PreAuthorize("@resourceAuth.canAccessResource(#userId, #setId, 'delete:own_sets')")
    public ResponseEntity<ApiResponse<Void>> deleteSet(
            @PathVariable UUID setId,
            @AuthenticationPrincipal String userId) {
        
        log.info("Deleting set: {} for user: {}", setId, userId);
        
        setService.deleteSet(setId, userId);
        return noContent();
    }
}
```

### 2.3 Service Layer Implementation

#### 2.3.1 Base Service Pattern
```java
@Service
@Transactional
@Slf4j
public abstract class BaseService {
    
    protected void validateBusinessRules(Object entity) {
        // Common business rule validation
    }
    
    protected void logOperation(String operation, String entityId, String userId) {
        log.info("Operation: {} on entity: {} by user: {}", operation, entityId, userId);
    }
    
    @Transactional(readOnly = true)
    protected <T> T findEntityOrThrow(Supplier<T> finder, String entityType, String entityId) {
        return finder.get()
            .orElseThrow(() -> new NotFoundException(
                String.format("%s with id %s not found", entityType, entityId)));
    }
}
```

#### 2.3.2 Set Service Implementation
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SetService extends BaseService {
    
    private final SetRepository setRepository;
    private final UserRepository userRepository;
    private final CycleService cycleService;
    private final SetMapper setMapper;
    private final SetValidator setValidator;
    
    @Transactional(readOnly = true)
    public Page<SetResponse> getSets(SetSearchCriteria criteria) {
        logOperation("GET_SETS", "ALL", criteria.getUserId());
        
        Pageable pageable = PageRequest.of(
            criteria.getPage(), 
            criteria.getSize(), 
            Sort.by(parseSort(criteria.getSort()))
        );
        
        Page<Set> sets = setRepository.findByUserIdAndStatus(
            UUID.fromString(criteria.getUserId()),
            criteria.getStatus(),
            pageable
        );
        
        return sets.map(setMapper::toResponse);
    }
    
    @Transactional
    public SetResponse createSet(CreateSetRequest request, String userId) {
        logOperation("CREATE_SET", "NEW", userId);
        
        // Validate business rules
        setValidator.validateCreateRequest(request, userId);
        
        // Create set entity
        Set set = Set.builder()
            .userId(UUID.fromString(userId))
            .name(request.getName())
            .description(request.getDescription())
            .category(request.getCategory())
            .status(SetStatus.NOT_STARTED)
            .currentCycle(1)
            .totalReviews(0)
            .build();
        
        // Save set
        Set savedSet = setRepository.save(set);
        
        // Create initial cycle
        cycleService.createInitialCycle(savedSet);
        
        log.info("Created set: {} for user: {}", savedSet.getId(), userId);
        return setMapper.toResponse(savedSet);
    }
    
    @Transactional(readOnly = true)
    public SetDetailResponse getSetDetail(UUID setId, String userId) {
        logOperation("GET_SET_DETAIL", setId.toString(), userId);
        
        Set set = findEntityOrThrow(
            () -> setRepository.findByIdAndUserId(setId, UUID.fromString(userId)),
            "Set",
            setId.toString()
        );
        
        return setMapper.toDetailResponse(set);
    }
    
    @Transactional
    public SetResponse updateSet(UUID setId, UpdateSetRequest request, String userId) {
        logOperation("UPDATE_SET", setId.toString(), userId);
        
        Set set = findEntityOrThrow(
            () -> setRepository.findByIdAndUserId(setId, UUID.fromString(userId)),
            "Set",
            setId.toString()
        );
        
        // Validate business rules
        setValidator.validateUpdateRequest(request, set);
        
        // Update set
        set.setName(request.getName());
        set.setDescription(request.getDescription());
        set.setStatus(request.getStatus());
        set.setUpdatedAt(Instant.now());
        
        Set updatedSet = setRepository.save(set);
        
        log.info("Updated set: {} for user: {}", setId, userId);
        return setMapper.toResponse(updatedSet);
    }
    
    @Transactional
    public void deleteSet(UUID setId, String userId) {
        logOperation("DELETE_SET", setId.toString(), userId);
        
        Set set = findEntityOrThrow(
            () -> setRepository.findByIdAndUserId(setId, UUID.fromString(userId)),
            "Set",
            setId.toString()
        );
        
        // Soft delete
        set.setDeletedAt(Instant.now());
        set.setStatus(SetStatus.DELETED);
        setRepository.save(set);
        
        log.info("Deleted set: {} for user: {}", setId, userId);
    }
    
    private Sort parseSort(String sortString) {
        String[] parts = sortString.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equals(parts[1]) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
```

### 2.4 Repository Layer Implementation

#### 2.4.1 Base Repository Pattern
```java
@Repository
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    List<T> findAllActive();
    
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL AND e.id = :id")
    Optional<T> findActiveById(@Param("id") ID id);
    
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = :deletedAt WHERE e.id = :id")
    void softDelete(@Param("id") ID id, @Param("deletedAt") Instant deletedAt);
}
```

#### 2.4.2 Set Repository Implementation
```java
@Repository
public interface SetRepository extends BaseRepository<Set, UUID> {
    
    @Query("SELECT s FROM Set s WHERE s.userId = :userId AND s.deletedAt IS NULL")
    Page<Set> findByUserIdAndStatus(
        @Param("userId") UUID userId,
        @Param("status") String status,
        Pageable pageable
    );
    
    @Query("SELECT s FROM Set s WHERE s.id = :setId AND s.userId = :userId AND s.deletedAt IS NULL")
    Optional<Set> findByIdAndUserId(@Param("setId") UUID setId, @Param("userId") UUID userId);
    
    @Query("SELECT COUNT(s) FROM Set s WHERE s.userId = :userId AND s.deletedAt IS NULL")
    long countByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT s FROM Set s WHERE s.userId = :userId AND s.status = :status AND s.deletedAt IS NULL")
    List<Set> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") SetStatus status);
    
    @Query("SELECT s FROM Set s WHERE s.userId = :userId AND s.lastReviewedAt >= :fromDate AND s.deletedAt IS NULL")
    List<Set> findRecentlyReviewed(@Param("userId") UUID userId, @Param("fromDate") Instant fromDate);
    
    @Query("SELECT s FROM Set s WHERE s.userId = :userId AND s.status = 'LEARNING' AND s.deletedAt IS NULL ORDER BY s.lastReviewedAt ASC")
    List<Set> findSetsForReview(@Param("userId") UUID userId);
}
```

### 2.5 Entity Layer Implementation

#### 2.5.1 Base Entity Pattern
```java
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @Column(name = "deleted_at")
    private Instant deletedAt;
    
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

#### 2.5.2 Set Entity Implementation
```java
@Entity
@Table(name = "sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Set extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private SetCategory category;
    
    @Column(name = "word_count", nullable = false)
    private Integer wordCount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SetStatus status;
    
    @Column(name = "current_cycle", nullable = false)
    private Integer currentCycle;
    
    @Column(name = "total_reviews", nullable = false)
    private Integer totalReviews;
    
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;
    
    @Column(name = "last_reviewed_at")
    private Instant lastReviewedAt;
    
    // Relationships
    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SetItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LearningCycle> cycles = new ArrayList<>();
    
    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewHistory> reviewHistories = new ArrayList<>();
    
    // Business methods
    public void updateStatistics(BigDecimal score) {
        this.totalReviews++;
        this.averageScore = calculateAverageScore(score);
        this.lastReviewedAt = Instant.now();
    }
    
    private BigDecimal calculateAverageScore(BigDecimal newScore) {
        if (averageScore == null) {
            return newScore;
        }
        
        BigDecimal totalScore = averageScore.multiply(BigDecimal.valueOf(totalReviews - 1))
            .add(newScore);
        return totalScore.divide(BigDecimal.valueOf(totalReviews), 2, RoundingMode.HALF_UP);
    }
    
    public void startLearning() {
        this.status = SetStatus.LEARNING;
        this.currentCycle = 1;
    }
    
    public void completeCycle() {
        this.currentCycle++;
        if (this.currentCycle > 5) {
            this.status = SetStatus.MASTERED;
        }
    }
}
```

## 3. Design Patterns Implementation

### 3.1 Dependency Injection Pattern

#### 3.1.1 Constructor Injection
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CycleService {
    
    private final CycleRepository cycleRepository;
    private final SetRepository setRepository;
    private final SrsAlgorithmService srsAlgorithmService;
    private final ReminderService reminderService;
    private final CycleMapper cycleMapper;
    private final CycleValidator cycleValidator;
    
    // Service methods...
}
```

#### 3.1.2 Configuration Classes
```java
@Configuration
@EnableJpaRepositories(basePackages = "com.repeatwise.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        config.setUsername(environment.getProperty("spring.datasource.username"));
        config.setPassword(environment.getProperty("spring.datasource.password"));
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }
    
    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
```

### 3.2 Repository Pattern

#### 3.2.1 Custom Repository Implementation
```java
@Repository
public class SetRepositoryImpl implements SetRepositoryCustom {
    
    private final EntityManager entityManager;
    
    public SetRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public List<Set> findSetsForReview(UUID userId, int limit) {
        String jpql = """
            SELECT s FROM Set s 
            WHERE s.userId = :userId 
            AND s.status = 'LEARNING' 
            AND s.deletedAt IS NULL 
            AND (s.lastReviewedAt IS NULL OR s.lastReviewedAt < :cutoffTime)
            ORDER BY s.lastReviewedAt ASC NULLS FIRST
            """;
        
        return entityManager.createQuery(jpql, Set.class)
            .setParameter("userId", userId)
            .setParameter("cutoffTime", Instant.now().minus(1, ChronoUnit.DAYS))
            .setMaxResults(limit)
            .getResultList();
    }
    
    @Override
    public Page<Set> findSetsWithStatistics(SetSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Set> query = cb.createQuery(Set.class);
        Root<Set> root = query.from(Set.class);
        
        // Build dynamic query based on criteria
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getUserId() != null) {
            predicates.add(cb.equal(root.get("userId"), UUID.fromString(criteria.getUserId())));
        }
        
        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), SetStatus.valueOf(criteria.getStatus())));
        }
        
        if (criteria.getCategory() != null) {
            predicates.add(cb.equal(root.get("category"), SetCategory.valueOf(criteria.getCategory())));
        }
        
        predicates.add(cb.isNull(root.get("deletedAt")));
        
        query.where(predicates.toArray(new Predicate[0]));
        
        // Execute query with pagination
        TypedQuery<Set> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        
        List<Set> results = typedQuery.getResultList();
        
        // Get total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(Set.class)));
        countQuery.where(predicates.toArray(new Predicate[0]));
        
        Long total = entityManager.createQuery(countQuery).getSingleResult();
        
        return new PageImpl<>(results, pageable, total);
    }
}
```

### 3.3 Service Pattern

#### 3.3.1 Service Interface
```java
public interface SetService {
    
    Page<SetResponse> getSets(SetSearchCriteria criteria);
    
    SetResponse createSet(CreateSetRequest request, String userId);
    
    SetDetailResponse getSetDetail(UUID setId, String userId);
    
    SetResponse updateSet(UUID setId, UpdateSetRequest request, String userId);
    
    void deleteSet(UUID setId, String userId);
    
    void startLearning(UUID setId, String userId);
    
    void pauseLearning(UUID setId, String userId);
    
    void resumeLearning(UUID setId, String userId);
}
```

#### 3.3.2 Service Implementation with Business Logic
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class SetServiceImpl implements SetService {
    
    private final SetRepository setRepository;
    private final SetValidator setValidator;
    private final SetMapper setMapper;
    private final CycleService cycleService;
    private final NotificationService notificationService;
    
    @Override
    @Transactional(readOnly = true)
    public Page<SetResponse> getSets(SetSearchCriteria criteria) {
        // Implementation as shown above
    }
    
    @Override
    @Transactional
    public SetResponse createSet(CreateSetRequest request, String userId) {
        // Validate business rules
        setValidator.validateCreateRequest(request, userId);
        
        // Check daily limit
        long todaySets = setRepository.countByUserIdAndCreatedAtAfter(
            UUID.fromString(userId), 
            Instant.now().truncatedTo(ChronoUnit.DAYS)
        );
        
        if (todaySets >= 5) { // Business rule: max 5 sets per day
            throw new BusinessException("DAILY_SET_LIMIT_EXCEEDED", 
                "Maximum 5 sets can be created per day");
        }
        
        // Create set
        Set set = Set.builder()
            .userId(UUID.fromString(userId))
            .name(request.getName())
            .description(request.getDescription())
            .category(request.getCategory())
            .wordCount(0) // Will be updated when items are added
            .status(SetStatus.NOT_STARTED)
            .currentCycle(1)
            .totalReviews(0)
            .build();
        
        Set savedSet = setRepository.save(set);
        
        // Create initial cycle
        cycleService.createInitialCycle(savedSet);
        
        // Send notification
        notificationService.sendSetCreatedNotification(savedSet);
        
        return setMapper.toResponse(savedSet);
    }
    
    @Override
    @Transactional
    public void startLearning(UUID setId, String userId) {
        Set set = findSetOrThrow(setId, userId);
        
        if (set.getStatus() != SetStatus.NOT_STARTED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION", 
                "Can only start learning from NOT_STARTED status");
        }
        
        if (set.getItems().isEmpty()) {
            throw new BusinessException("EMPTY_SET", 
                "Cannot start learning with empty set");
        }
        
        set.startLearning();
        setRepository.save(set);
        
        // Create first learning cycle
        cycleService.createLearningCycle(set, 1);
        
        // Schedule first reminder
        reminderService.scheduleFirstReminder(set);
        
        log.info("Started learning for set: {} by user: {}", setId, userId);
    }
}
```

## 4. Error Handling Implementation

### 4.1 Global Exception Handler
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {} - {}", ex.getCode(), ex.getMessage());
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<ValidationErrorResponse>> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message(ex.getMessage())
            .fieldErrors(ex.getFieldErrors())
            .build();
        return ResponseEntity.badRequest().body(ApiResponse.error(errorResponse));
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error("ACCESS_DENIED", "Insufficient permissions"));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorResponse>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        log.warn("Method argument validation failed: {}", ex.getMessage());
        
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.builder()
            .code("VALIDATION_ERROR")
            .message("Validation failed")
            .fieldErrors(fieldErrors.stream()
                .map(error -> FieldError.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .rejectedValue(error.getRejectedValue())
                    .build())
                .collect(Collectors.toList()))
            .build();
        
        return ResponseEntity.badRequest().body(ApiResponse.error(errorResponse));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

### 4.2 Custom Exceptions
```java
public class BusinessException extends RuntimeException {
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}

public class ValidationException extends RuntimeException {
    private final List<FieldError> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = Collections.emptyList();
    }
    
    public ValidationException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
    
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
```

## 5. Configuration Management

### 5.1 Application Properties
```yaml
# application.yml
spring:
  application:
    name: repeatwise-api
  
  datasource:
    url: jdbc:postgresql://localhost:5432/repeatwise
    username: ${DB_USERNAME:repeatwise}
    password: ${DB_PASSWORD:password}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

# Business Configuration
repeatwise:
  business:
    max-sets-per-day: 5
    max-items-per-set: 1000
    default-reminder-time: "09:00"
    cycle-delay-days:
      min: 7
      max: 90
      base: 30
  
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key}
      expiration: 3600000 # 1 hour
      refresh-expiration: 604800000 # 7 days
  
  notification:
    email:
      enabled: true
      from: noreply@repeatwise.com
    push:
      enabled: true
      fcm-server-key: ${FCM_SERVER_KEY:}

# Logging
logging:
  level:
    com.repeatwise: INFO
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/repeatwise-api.log
```

### 5.2 Configuration Classes
```java
@Configuration
@EnableConfigurationProperties(RepeatWiseProperties.class)
public class RepeatWiseConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "repeatwise.business")
    public BusinessProperties businessProperties() {
        return new BusinessProperties();
    }
    
    @Bean
    @ConfigurationProperties(prefix = "repeatwise.security")
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }
}

@ConfigurationProperties(prefix = "repeatwise.business")
@Data
public class BusinessProperties {
    private int maxSetsPerDay = 5;
    private int maxItemsPerSet = 1000;
    private String defaultReminderTime = "09:00";
    private CycleDelay cycleDelay = new CycleDelay();
    
    @Data
    public static class CycleDelay {
        private int min = 7;
        private int max = 90;
        private int base = 30;
    }
}
```

## 6. Testing Strategy

### 6.1 Unit Testing
```java
@ExtendWith(MockitoExtension.class)
class SetServiceTest {
    
    @Mock
    private SetRepository setRepository;
    
    @Mock
    private SetValidator setValidator;
    
    @Mock
    private SetMapper setMapper;
    
    @InjectMocks
    private SetServiceImpl setService;
    
    @Test
    void shouldCreateSetSuccessfully() {
        // Given
        CreateSetRequest request = CreateSetRequest.builder()
            .name("Test Set")
            .description("Test Description")
            .category(SetCategory.VOCABULARY)
            .build();
        
        String userId = "user-123";
        Set savedSet = Set.builder()
            .id(UUID.randomUUID())
            .name("Test Set")
            .build();
        
        when(setRepository.countByUserIdAndCreatedAtAfter(any(), any())).thenReturn(0L);
        when(setRepository.save(any(Set.class))).thenReturn(savedSet);
        when(setMapper.toResponse(any(Set.class))).thenReturn(SetResponse.builder().build());
        
        // When
        SetResponse result = setService.createSet(request, userId);
        
        // Then
        assertThat(result).isNotNull();
        verify(setValidator).validateCreateRequest(request, userId);
        verify(setRepository).save(any(Set.class));
    }
    
    @Test
    void shouldThrowExceptionWhenDailyLimitExceeded() {
        // Given
        CreateSetRequest request = CreateSetRequest.builder()
            .name("Test Set")
            .build();
        
        String userId = "user-123";
        when(setRepository.countByUserIdAndCreatedAtAfter(any(), any())).thenReturn(5L);
        
        // When & Then
        assertThatThrownBy(() -> setService.createSet(request, userId))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Maximum 5 sets can be created per day");
    }
}
```

### 6.2 Integration Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class SetControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private SetRepository setRepository;
    
    @Test
    void shouldCreateSetSuccessfully() {
        // Given
        CreateSetRequest request = CreateSetRequest.builder()
            .name("Integration Test Set")
            .description("Test Description")
            .category(SetCategory.VOCABULARY)
            .build();
        
        String token = getAuthToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<CreateSetRequest> entity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/v1/sets", entity, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isSuccess()).isTrue();
        
        // Verify in database
        List<Set> sets = setRepository.findAll();
        assertThat(sets).hasSize(1);
        assertThat(sets.get(0).getName()).isEqualTo("Integration Test Set");
    }
}
```

## 7. Performance Optimization

### 7.1 Caching Strategy
```java
@Service
@RequiredArgsConstructor
public class CachedSetService {
    
    private final SetRepository setRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Cacheable(value = "sets", key = "#userId + '_' + #setId")
    public SetResponse getSet(UUID setId, String userId) {
        return setRepository.findByIdAndUserId(setId, UUID.fromString(userId))
            .map(setMapper::toResponse)
            .orElseThrow(() -> new NotFoundException("Set not found"));
    }
    
    @CacheEvict(value = "sets", key = "#userId + '_' + #setId")
    public SetResponse updateSet(UUID setId, UpdateSetRequest request, String userId) {
        // Update logic
    }
    
    @CacheEvict(value = "sets", allEntries = true)
    public void evictAllSetsCache() {
        // Evict all cache entries
    }
}
```

### 7.2 Database Optimization
```java
@Repository
public interface SetRepository extends JpaRepository<Set, UUID> {
    
    @Query("SELECT s FROM Set s LEFT JOIN FETCH s.items WHERE s.userId = :userId AND s.deletedAt IS NULL")
    List<Set> findByUserIdWithItems(@Param("userId") UUID userId);
    
    @Query("SELECT s FROM Set s LEFT JOIN FETCH s.cycles WHERE s.userId = :userId AND s.deletedAt IS NULL")
    List<Set> findByUserIdWithCycles(@Param("userId") UUID userId);
    
    @Query(value = """
        SELECT s.*, COUNT(si.id) as item_count, 
               AVG(rh.score) as avg_score
        FROM sets s 
        LEFT JOIN set_items si ON s.id = si.set_id 
        LEFT JOIN review_histories rh ON s.id = rh.set_id 
        WHERE s.user_id = :userId AND s.deleted_at IS NULL
        GROUP BY s.id
        """, nativeQuery = true)
    List<Object[]> findSetsWithStatistics(@Param("userId") UUID userId);
}
```

## 8. Monitoring and Logging

### 8.1 Structured Logging
```java
@Component
@Slf4j
public class AuditLogger {
    
    public void logSetOperation(String operation, UUID setId, String userId, Object details) {
        Map<String, Object> logData = Map.of(
            "operation", operation,
            "setId", setId.toString(),
            "userId", userId,
            "timestamp", Instant.now(),
            "details", details
        );
        
        log.info("Set operation: {}", logData);
    }
    
    public void logUserAction(String action, String userId, String ipAddress, String userAgent) {
        Map<String, Object> logData = Map.of(
            "action", action,
            "userId", userId,
            "ipAddress", ipAddress,
            "userAgent", userAgent,
            "timestamp", Instant.now()
        );
        
        log.info("User action: {}", logData);
    }
}
```

### 8.2 Metrics Collection
```java
@Component
@RequiredArgsConstructor
public class MetricsCollector {
    
    private final MeterRegistry meterRegistry;
    
    public void recordSetCreated(String userId) {
        Counter.builder("sets.created")
            .tag("user", userId)
            .register(meterRegistry)
            .increment();
    }
    
    public void recordSetReviewed(String userId, BigDecimal score) {
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("sets.review.duration")
            .tag("user", userId)
            .register(meterRegistry));
        
        Gauge.builder("sets.review.score")
            .tag("user", userId)
            .register(meterRegistry, score, BigDecimal::doubleValue);
    }
}
```

Tài liệu này cung cấp implementation chi tiết cho kiến trúc ứng dụng RepeatWise, bao gồm:

1. **Package Structure**: Cấu trúc thư mục rõ ràng theo layered architecture
2. **Controller Layer**: Implementation chi tiết với error handling và validation
3. **Service Layer**: Business logic với transaction management
4. **Repository Layer**: Data access với custom queries
5. **Entity Layer**: JPA entities với business methods
6. **Design Patterns**: Dependency injection, repository pattern, service pattern
7. **Error Handling**: Global exception handler và custom exceptions
8. **Configuration**: Application properties và configuration classes
9. **Testing**: Unit tests và integration tests
10. **Performance**: Caching và database optimization
11. **Monitoring**: Structured logging và metrics collection

Các developer có thể sử dụng tài liệu này để implement code ngay lập tức với đầy đủ best practices và patterns đã được định nghĩa.
