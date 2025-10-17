# Backend Detailed Design - RepeatWise MVP

## 1. Overview

Backend được thiết kế theo **Layered Architecture** với Spring Boot 3, sử dụng các design patterns phổ biến để đảm bảo maintainability và extensibility.

**Technology Stack**:
- Java 17
- Spring Boot 3.x
- Spring Data JPA (Hibernate)
- PostgreSQL 15
- Apache POI (Excel processing)
- OpenCSV (CSV processing)
- MapStruct (DTO mapping)

## 2. Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│  - REST endpoints (@RestController)                     │
│  - Request/Response DTOs                                 │
│  - Input validation (@Valid)                             │
│  - Exception handling (@RestControllerAdvice)            │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                     Service Layer                        │
│  - Business logic (interfaces + implementations)         │
│  - Transaction management (@Transactional)               │
│  - Domain events publishing                              │
│  - Strategy pattern implementations                      │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│  - Spring Data JPA repositories                          │
│  - Custom queries (@Query)                               │
│  - Database access                                       │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                    Database (PostgreSQL)                 │
└─────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

#### Controller Layer
- **HTTP request/response** handling
- **DTO validation** với Bean Validation
- **Route mapping** và versioning
- **Authentication** check (JWT)
- **Error response** formatting
- **NOT** business logic

#### Service Layer
- **Business logic** implementation
- **Transaction boundaries** (@Transactional)
- **Domain events** publishing
- **Coordination** giữa multiple repositories
- **Entity ↔ DTO** mapping (via MapStruct)

#### Repository Layer
- **CRUD operations** via Spring Data JPA
- **Custom queries** với JPQL/native SQL
- **Database transactions**
- **NOT** business logic

## 3. Package Structure

```
src/main/java/com/repeatwise/
│
├── RepeatWiseApplication.java          # Main application class
│
├── config/                             # Configuration classes
│   ├── SecurityConfig.java             # Spring Security, JWT filter
│   ├── CorsConfig.java                 # CORS configuration
│   ├── AsyncConfig.java                # @Async thread pool
│   ├── JpaAuditingConfig.java          # Auditing @CreatedDate
│   └── MapStructConfig.java            # MapStruct configuration
│
├── controller/                         # REST Controllers
│   ├── AuthController.java             # /api/auth/**
│   ├── UserController.java             # /api/users/**
│   ├── FolderController.java           # /api/folders/**
│   ├── DeckController.java             # /api/decks/**
│   ├── CardController.java             # /api/cards/**
│   ├── ReviewController.java           # /api/review/**
│   ├── SrsSettingsController.java      # /api/srs/settings
│   └── StatsController.java            # /api/stats/**
│
├── service/                            # Service Interfaces
│   ├── IAuthService.java
│   ├── IUserService.java
│   ├── IFolderService.java
│   ├── IDeckService.java
│   ├── ICardService.java
│   ├── IReviewService.java
│   ├── ISrsService.java
│   ├── IStatsService.java
│   ├── IImportExportService.java
│   └── INotificationService.java
│
├── service/impl/                       # Service Implementations
│   ├── AuthServiceImpl.java
│   ├── UserServiceImpl.java
│   ├── FolderServiceImpl.java
│   ├── DeckServiceImpl.java
│   ├── CardServiceImpl.java
│   ├── ReviewServiceImpl.java
│   ├── SrsServiceImpl.java
│   ├── StatsServiceImpl.java
│   ├── ImportExportServiceImpl.java
│   └── NotificationServiceImpl.java
│
├── repository/                         # Spring Data JPA Repositories
│   ├── UserRepository.java
│   ├── RefreshTokenRepository.java
│   ├── FolderRepository.java
│   ├── DeckRepository.java
│   ├── CardRepository.java
│   ├── CardBoxPositionRepository.java
│   ├── ReviewLogRepository.java
│   ├── SrsSettingsRepository.java
│   ├── UserStatsRepository.java
│   └── FolderStatsRepository.java
│
├── entity/                             # JPA Entities
│   ├── User.java
│   ├── RefreshToken.java
│   ├── Folder.java
│   ├── Deck.java
│   ├── Card.java
│   ├── CardBoxPosition.java
│   ├── ReviewLog.java
│   ├── SrsSettings.java
│   ├── UserStats.java
│   ├── FolderStats.java
│   └── base/                           # Base entities
│       ├── BaseEntity.java             # id, createdAt, updatedAt
│       └── SoftDeletableEntity.java    # deletedAt
│
├── dto/                                # Data Transfer Objects
│   ├── request/                        # Request DTOs
│   │   ├── auth/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── RefreshTokenRequest.java
│   │   ├── folder/
│   │   │   ├── CreateFolderRequest.java
│   │   │   ├── UpdateFolderRequest.java
│   │   │   ├── MoveFolderRequest.java
│   │   │   └── CopyFolderRequest.java
│   │   ├── deck/
│   │   │   ├── CreateDeckRequest.java
│   │   │   ├── UpdateDeckRequest.java
│   │   │   ├── MoveDeckRequest.java
│   │   │   └── CopyDeckRequest.java
│   │   ├── card/
│   │   │   ├── CreateCardRequest.java
│   │   │   ├── UpdateCardRequest.java
│   │   │   └── ImportCardsRequest.java
│   │   └── review/
│   │       ├── ReviewSubmitRequest.java
│   │       └── GetDueCardsRequest.java
│   │
│   └── response/                       # Response DTOs
│       ├── auth/
│       │   ├── AuthResponse.java       # access_token, user
│       │   └── RefreshTokenResponse.java
│       ├── folder/
│       │   ├── FolderResponse.java
│       │   ├── FolderTreeResponse.java
│       │   ├── FolderStatsResponse.java
│       │   └── CopyJobResponse.java    # job_id, status
│       ├── deck/
│       │   ├── DeckResponse.java
│       │   └── DeckStatsResponse.java
│       ├── card/
│       │   ├── CardResponse.java
│       │   └── ImportResultResponse.java
│       ├── review/
│       │   ├── ReviewSessionResponse.java
│       │   └── ReviewResultResponse.java
│       └── stats/
│           ├── UserStatsResponse.java
│           └── BoxDistributionResponse.java
│
├── mapper/                             # MapStruct Mappers
│   ├── UserMapper.java
│   ├── FolderMapper.java
│   ├── DeckMapper.java
│   ├── CardMapper.java
│   ├── ReviewMapper.java
│   └── StatsMapper.java
│
├── strategy/                           # Strategy Pattern
│   ├── review/                         # Review order strategies
│   │   ├── ReviewOrderStrategy.java    # Interface
│   │   ├── AscendingReviewStrategy.java
│   │   ├── DescendingReviewStrategy.java
│   │   └── RandomReviewStrategy.java
│   └── forgotten/                      # Forgotten card strategies
│       ├── ForgottenCardActionStrategy.java
│       ├── MoveToBox1Strategy.java
│       ├── MoveDownNBoxesStrategy.java
│       └── StayInBoxStrategy.java
│
├── visitor/                            # Visitor Pattern
│   ├── FolderVisitor.java              # Interface
│   └── FolderStatsVisitor.java         # Stats calculation
│
├── event/                              # Domain Events
│   ├── CardReviewedEvent.java
│   ├── FolderMovedEvent.java
│   ├── DeckCreatedEvent.java
│   ├── CardImportedEvent.java
│   └── listener/                       # Event Listeners
│       ├── UserStatsUpdateListener.java
│       ├── FolderStatsInvalidationListener.java
│       └── NotificationListener.java
│
├── exception/                          # Custom Exceptions
│   ├── GlobalExceptionHandler.java     # @RestControllerAdvice
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── ValidationException.java
│   ├── MaxDepthExceededException.java
│   ├── CircularReferenceException.java
│   ├── FolderTooLargeException.java
│   └── InvalidCredentialsException.java
│
├── security/                           # Security Components
│   ├── JwtAuthenticationFilter.java    # JWT token validation
│   ├── JwtTokenProvider.java           # Token generation/validation
│   ├── UserPrincipal.java              # UserDetails implementation
│   └── SecurityUtils.java              # Current user helper
│
├── util/                               # Utility Classes
│   ├── DateUtils.java                  # Date calculations
│   ├── ValidationUtils.java            # Common validations
│   └── FileUtils.java                  # File operations
│
└── job/                                # Background Jobs
    ├── FolderCopyJob.java              # Async folder copy
    ├── DeckCopyJob.java                # Async deck copy
    ├── ImportJob.java                  # Large import job
    ├── StatsRecalculationJob.java      # Periodic stats update
    └── JobStatusStore.java             # In-memory job tracking
```

## 4. Design Patterns Implementation

### 4.1 Composite Pattern (Folder Tree)

```java
@Entity
@Table(name = "folders")
public class Folder extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;  // Composite pattern

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> subFolders = new ArrayList<>();  // Children

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<Deck> decks = new ArrayList<>();  // Leaves

    // Business methods
    public void addSubFolder(Folder subFolder) {
        if (this.depth >= 9) {
            throw new MaxDepthExceededException("Max depth 10 exceeded");
        }
        subFolder.parentFolder = this;
        subFolder.depth = this.depth + 1;
        subFolder.updatePath();
        this.subFolders.add(subFolder);
    }

    public int countTotalItems() {
        int count = 1; // Self
        for (Folder sub : subFolders) {
            count += sub.countTotalItems(); // Recursive
        }
        count += decks.size();
        return count;
    }
}
```

**Benefits**:
- Uniform treatment: Folder treated same whether leaf or composite
- Easy traversal: Recursive methods for statistics
- Flexible structure: Unlimited nesting (with depth constraint)

### 4.2 Strategy Pattern (Review Order)

```java
// Strategy Interface
public interface ReviewOrderStrategy {
    List<Card> order(List<Card> cards);
}

// Concrete Strategies
@Component
public class AscendingReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<Card> order(List<Card> cards) {
        return cards.stream()
            .sorted(Comparator.comparing(card ->
                card.getBoxPosition().getCurrentBox()))
            .collect(Collectors.toList());
    }
}

@Component
public class RandomReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<Card> order(List<Card> cards) {
        List<Card> shuffled = new ArrayList<>(cards);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}

// Context (Service)
@Service
public class ReviewServiceImpl implements IReviewService {

    private final Map<ReviewOrder, ReviewOrderStrategy> strategies;

    @Autowired
    public ReviewServiceImpl(List<ReviewOrderStrategy> strategyList) {
        // Auto-inject all strategy beans
        this.strategies = Map.of(
            ReviewOrder.ASCENDING, findStrategy(AscendingReviewStrategy.class, strategyList),
            ReviewOrder.DESCENDING, findStrategy(DescendingReviewStrategy.class, strategyList),
            ReviewOrder.RANDOM, findStrategy(RandomReviewStrategy.class, strategyList)
        );
    }

    @Override
    public List<Card> getDueCards(UUID userId, ReviewOrder order) {
        List<Card> dueCards = cardRepository.findDueCards(userId, LocalDate.now());
        ReviewOrderStrategy strategy = strategies.get(order);
        return strategy.order(dueCards);
    }
}
```

**Benefits**:
- Easy to add new strategies (just implement interface)
- Strategy selected at runtime based on user settings
- Testable in isolation

### 4.3 Visitor Pattern (Folder Statistics)

```java
// Visitor Interface
public interface FolderVisitor<T> {
    T visit(Folder folder);
}

// Concrete Visitor
@Component
public class FolderStatsVisitor implements FolderVisitor<FolderStats> {

    @Override
    public FolderStats visit(Folder folder) {
        FolderStats stats = new FolderStats();
        stats.setFolderId(folder.getId());

        // Count cards in this folder's decks
        int totalCards = 0;
        int dueCards = 0;
        for (Deck deck : folder.getDecks()) {
            totalCards += deck.getCards().size();
            dueCards += countDueCards(deck);
        }

        // Recursively visit sub-folders
        for (Folder subFolder : folder.getSubFolders()) {
            FolderStats subStats = visit(subFolder);  // Recursive
            totalCards += subStats.getTotalCardsCount();
            dueCards += subStats.getDueCardsCount();
        }

        stats.setTotalCardsCount(totalCards);
        stats.setDueCardsCount(dueCards);
        return stats;
    }
}

// Usage in Service
@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private FolderStatsVisitor statsVisitor;

    @Override
    public FolderStatsResponse getFolderStats(UUID folderId) {
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        FolderStats stats = statsVisitor.visit(folder);
        folderStatsRepository.save(stats);

        return folderStatsMapper.toResponse(stats);
    }
}
```

**Benefits**:
- Separation of concerns: Stats logic separate from Folder entity
- Easy to add new operations: Just create new visitor
- Reusable: Same visitor for multiple folders

### 4.4 Repository Pattern (Spring Data JPA)

```java
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    // Spring Data JPA method naming
    List<Folder> findByUserAndParentFolderIsNullAndDeletedAtIsNull(User user);

    Optional<Folder> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    // Custom JPQL query
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId " +
           "AND f.path LIKE CONCAT(:pathPrefix, '%') " +
           "AND f.deletedAt IS NULL")
    List<Folder> findDescendants(@Param("userId") UUID userId,
                                  @Param("pathPrefix") String pathPrefix);

    // Native SQL for performance
    @Query(value = "SELECT * FROM folders WHERE user_id = :userId " +
                   "AND path ~ :pathPattern AND deleted_at IS NULL",
           nativeQuery = true)
    List<Folder> findByPathPattern(@Param("userId") UUID userId,
                                    @Param("pathPattern") String pathPattern);
}
```

**Benefits**:
- Testable: Easy to mock repositories
- Swappable: Can change implementation without affecting service layer
- Clean API: Business logic doesn't know about SQL

### 4.5 DTO Pattern (MapStruct)

```java
// Entity
@Entity
public class Folder {
    private UUID id;
    private String name;
    private String description;
    private Folder parentFolder;
    private List<Folder> subFolders;
    private List<Deck> decks;
    // ... fields
}

// Response DTO
public class FolderResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID parentFolderId;
    private int subFolderCount;
    private int deckCount;
}

// MapStruct Mapper
@Mapper(componentModel = "spring")
public interface FolderMapper {

    @Mapping(source = "parentFolder.id", target = "parentFolderId")
    @Mapping(expression = "java(folder.getSubFolders().size())",
             target = "subFolderCount")
    @Mapping(expression = "java(folder.getDecks().size())",
             target = "deckCount")
    FolderResponse toResponse(Folder folder);

    List<FolderResponse> toResponseList(List<Folder> folders);
}
```

**Benefits**:
- Decouple API contract from domain model
- Control what data is exposed
- Type-safe mapping (compile-time checked)

## 5. Service Layer Design

### 5.1 Interface-Based Design

```java
public interface IFolderService {
    // CRUD operations
    FolderResponse createFolder(CreateFolderRequest request, UUID userId);
    FolderResponse updateFolder(UUID folderId, UpdateFolderRequest request, UUID userId);
    void deleteFolder(UUID folderId, UUID userId);
    FolderResponse getFolder(UUID folderId, UUID userId);
    List<FolderResponse> getFoldersByUser(UUID userId);

    // Tree operations
    FolderResponse moveFolder(UUID folderId, UUID newParentId, UUID userId);
    CopyJobResponse copyFolder(UUID folderId, UUID destinationId, UUID userId);

    // Statistics
    FolderStatsResponse getFolderStats(UUID folderId, UUID userId);

    // Tree navigation
    List<FolderResponse> getSubFolders(UUID folderId, UUID userId);
    List<FolderResponse> getDescendants(UUID folderId, UUID userId);
    List<BreadcrumbItem> getBreadcrumb(UUID folderId, UUID userId);
}
```

**Why interfaces?**
- Easy to mock for testing
- Can have multiple implementations (e.g., cached version)
- Clear contract between layers

### 5.2 Transaction Management

```java
@Service
@Transactional(readOnly = true)  // Default to read-only
public class FolderServiceImpl implements IFolderService {

    @Transactional  // Override for write operations
    @Override
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        // Validate depth
        Folder parent = null;
        if (request.getParentFolderId() != null) {
            parent = folderRepository.findById(request.getParentFolderId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));

            if (parent.getDepth() >= 9) {
                throw new MaxDepthExceededException("Cannot create folder beyond depth 10");
            }
        }

        // Create folder
        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setDescription(request.getDescription());
        folder.setUser(userRepository.getReferenceById(userId));
        folder.setParentFolder(parent);
        folder.setDepth(parent == null ? 0 : parent.getDepth() + 1);
        folder.updatePath();

        Folder saved = folderRepository.save(folder);

        // Publish domain event (async)
        eventPublisher.publishEvent(new FolderCreatedEvent(saved.getId(), userId));

        return folderMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void deleteFolder(UUID folderId, UUID userId) {
        Folder folder = folderRepository.findByIdAndUserId(folderId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        // Soft delete (cascade to children)
        folder.softDelete();
        folderRepository.save(folder);

        // Publish event
        eventPublisher.publishEvent(new FolderDeletedEvent(folderId, userId));
    }
}
```

**Transaction Rules**:
- Read-only by default (optimization)
- Write operations explicitly marked `@Transactional`
- Domain events published within transaction (synchronous)
- Event listeners run async (separate transaction)

### 5.3 Notification Service (UC-024)

```java
@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationSettingsRepository notificationSettingsRepo;
    private final NotificationLogRepository notificationLogRepo;
    private final CardBoxPositionRepository cardBoxPositionRepo;
    private final EmailService emailService;
    private final UserRepository userRepo;

    // Get notification settings
    public NotificationSettingsResponse getSettings(UUID userId) {
        NotificationSettings settings = notificationSettingsRepo
            .findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));
        return notificationMapper.toResponse(settings);
    }

    // Update notification settings
    @Transactional
    public NotificationSettingsResponse updateSettings(
        UUID userId, UpdateNotificationSettingsRequest request
    ) {
        NotificationSettings settings = notificationSettingsRepo
            .findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));

        settings.setDailyReminderEnabled(request.getDailyReminderEnabled());
        settings.setDailyReminderTime(request.getDailyReminderTime());
        settings.setDailyReminderDays(String.join(",", request.getDailyReminderDays()));
        settings.setNotificationMethod(request.getNotificationMethod());
        settings.setNotificationEmail(request.getNotificationEmail());

        NotificationSettings saved = notificationSettingsRepo.save(settings);
        return notificationMapper.toResponse(saved);
    }

    // Send test notification
    @Async
    @Transactional
    public void sendTestNotification(UUID userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        NotificationSettings settings = notificationSettingsRepo
            .findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));

        int dueCount = cardBoxPositionRepo.countDueCards(userId, LocalDate.now());

        Map<String, Object> metadata = Map.of(
            "dueCardsCount", dueCount,
            "testNotification", true
        );

        sendDailyReminder(user, settings, dueCount, metadata);
    }

    // Get notification logs
    public Page<NotificationLogResponse> getLogs(UUID userId, Pageable pageable) {
        Page<NotificationLog> logs = notificationLogRepo.findByUserId(userId, pageable);
        return logs.map(notificationMapper::toLogResponse);
    }

    // Send daily reminder (called by scheduler)
    @Transactional
    public void sendDailyReminder(User user, NotificationSettings settings,
                                   int dueCount, Map<String, Object> metadata) {
        String recipient = settings.getEffectiveEmail();
        String subject = String.format("📚 [RepeatWise] You have %d cards due for review", dueCount);
        String body = buildEmailBody(user, dueCount, metadata);

        NotificationLog log = NotificationLog.createPending(
            user,
            NotificationLog.NotificationType.DAILY_REMINDER,
            NotificationLog.NotificationMethod.EMAIL,
            recipient,
            subject,
            body,
            metadata
        );

        try {
            emailService.sendEmail(recipient, subject, body);
            log.markAsSent();
        } catch (Exception e) {
            log.markAsFailed(e.getMessage());
        }

        notificationLogRepo.save(log);
    }

    private NotificationSettings createDefaultSettings(UUID userId) {
        User user = userRepo.getReferenceById(userId);
        NotificationSettings settings = NotificationSettings.createDefault(user);
        return notificationSettingsRepo.save(settings);
    }

    private String buildEmailBody(User user, int dueCount, Map<String, Object> metadata) {
        return String.format("""
            Hi %s,

            You have %d cards due for review today.

            Keep up your study streak! 🔥

            [Start Review]

            Happy learning!
            - RepeatWise Team
            """, user.getName(), dueCount);
    }
}
```

### 5.4 Notification Scheduler (Cron Job)

```java
@Component
@EnableScheduling
public class NotificationScheduler {

    private final NotificationSettingsRepository settingsRepo;
    private final CardBoxPositionRepository cardBoxPositionRepo;
    private final NotificationService notificationService;
    private final UserRepository userRepo;

    @Scheduled(cron = "0 * * * * *")  // Run every minute
    @Transactional(readOnly = true)
    public void sendScheduledNotifications() {
        LocalTime now = LocalTime.now();
        String dayOfWeek = LocalDate.now().getDayOfWeek().name().substring(0, 3);

        List<NotificationSettings> settingsList = settingsRepo
            .findByDailyReminderEnabledTrueAndDailyReminderTime(now);

        for (NotificationSettings settings : settingsList) {
            if (!settings.shouldNotifyOnDay(dayOfWeek)) {
                continue;
            }

            User user = settings.getUser();
            int dueCount = cardBoxPositionRepo.countDueCards(
                user.getId(), LocalDate.now()
            );

            if (dueCount == 0) {
                continue;  // Skip if no cards due
            }

            Map<String, Object> metadata = Map.of(
                "dueCardsCount", dueCount,
                "scheduledTime", now.toString(),
                "dayOfWeek", dayOfWeek
            );

            notificationService.sendDailyReminder(user, settings, dueCount, metadata);
        }
    }

    @Scheduled(cron = "0 0 3 * * *")  // Run daily at 3 AM
    @Transactional
    public void cleanupOldLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(90);
        notificationLogRepo.deleteByStatusInAndSentAtBefore(
            List.of(NotificationStatus.SENT, NotificationStatus.DELIVERED),
            cutoff
        );
    }
}
```

**Key Design Decisions**:
- **Scheduler runs every minute**: Checks for notifications to send
- **Skip if no cards due**: Don't spam users with empty notifications
- **Async test notifications**: Non-blocking for better UX
- **Log cleanup**: Auto-delete old logs after 90 days
- **Timezone handling**: Store time in user's local timezone, convert to UTC
- **Email service abstraction**: Easy to swap SMTP providers

**Requirements Mapping**:
- [UC-024: Manage Notifications](../../02-system-analysis/use-cases/UC-024-manage-notifications.md)
- [schema.md](../database/schema.md) Section 3.4-3.5
- [api-endpoints-summary.md](../api/api-endpoints-summary.md) Section 9

---

## 6. Exception Handling

### 6.1 Custom Exceptions

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class MaxDepthExceededException extends ValidationException {
    public MaxDepthExceededException(String message) {
        super(message);
    }
}
```

### 6.2 Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorResponse error = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Validation failed")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .build();

        return ResponseEntity.badRequest().body(error);
    }
}
```

## 7. Validation Strategy

### 7.1 Request DTO Validation

```java
public class CreateFolderRequest {

    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private UUID parentFolderId;  // Optional
}

// Controller
@PostMapping
public ResponseEntity<FolderResponse> createFolder(
        @Valid @RequestBody CreateFolderRequest request) {
    // @Valid triggers validation
    FolderResponse response = folderService.createFolder(request, getCurrentUserId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### 7.2 Custom Validators

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FolderDepthValidator.class)
public @interface ValidFolderDepth {
    String message() default "Folder depth exceeds maximum allowed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class FolderDepthValidator implements
        ConstraintValidator<ValidFolderDepth, CreateFolderRequest> {

    @Autowired
    private FolderRepository folderRepository;

    @Override
    public boolean isValid(CreateFolderRequest request,
                          ConstraintValidatorContext context) {
        if (request.getParentFolderId() == null) {
            return true;  // Root level folder
        }

        Folder parent = folderRepository.findById(request.getParentFolderId())
            .orElse(null);

        if (parent == null || parent.getDepth() >= 9) {
            return false;
        }

        return true;
    }
}
```

## 8. Testing Strategy

### 8.1 Repository Tests

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class FolderRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private FolderRepository folderRepository;

    @Test
    void shouldFindRootFolders() {
        // Given
        User user = createUser();
        Folder root1 = createFolder("Root 1", null, user);
        Folder root2 = createFolder("Root 2", null, user);
        Folder child = createFolder("Child", root1, user);

        // When
        List<Folder> roots = folderRepository
            .findByUserAndParentFolderIsNullAndDeletedAtIsNull(user);

        // Then
        assertThat(roots).hasSize(2);
        assertThat(roots).extracting(Folder::getName)
            .containsExactlyInAnyOrder("Root 1", "Root 2");
    }
}
```

### 8.2 Service Tests

```java
@ExtendWith(MockitoExtension.class)
class FolderServiceImplTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FolderMapper folderMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Test
    void shouldCreateFolderSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("Test Folder");

        Folder savedFolder = new Folder();
        savedFolder.setId(UUID.randomUUID());
        savedFolder.setName("Test Folder");

        when(folderRepository.save(any(Folder.class))).thenReturn(savedFolder);
        when(folderMapper.toResponse(savedFolder))
            .thenReturn(new FolderResponse(savedFolder.getId(), "Test Folder"));

        // When
        FolderResponse response = folderService.createFolder(request, userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Folder");
        verify(folderRepository).save(any(Folder.class));
        verify(eventPublisher).publishEvent(any(FolderCreatedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenMaxDepthExceeded() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        Folder parent = new Folder();
        parent.setDepth(9);  // Already at max depth - 1

        CreateFolderRequest request = new CreateFolderRequest();
        request.setParentFolderId(parentId);

        when(folderRepository.findById(parentId)).thenReturn(Optional.of(parent));

        // When & Then
        assertThatThrownBy(() -> folderService.createFolder(request, userId))
            .isInstanceOf(MaxDepthExceededException.class)
            .hasMessageContaining("depth 10");
    }
}
```

### 8.3 Controller Tests

```java
@WebMvcTest(FolderController.class)
class FolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IFolderService folderService;

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateFolder() throws Exception {
        // Given
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("Test Folder");

        FolderResponse response = new FolderResponse(UUID.randomUUID(), "Test Folder");

        when(folderService.createFolder(any(), any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Test Folder"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        CreateFolderRequest request = new CreateFolderRequest();
        request.setName("");  // Invalid

        // When & Then
        mockMvc.perform(post("/api/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
```

## 9. Performance Considerations

### 9.1 N+1 Problem Prevention

```java
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    // BAD: N+1 problem
    @Query("SELECT f FROM Folder f WHERE f.user.id = :userId")
    List<Folder> findAllByUser(@Param("userId") UUID userId);
    // Will execute 1 query for folders + N queries for each folder's decks

    // GOOD: JOIN FETCH
    @Query("SELECT DISTINCT f FROM Folder f " +
           "LEFT JOIN FETCH f.decks " +
           "WHERE f.user.id = :userId")
    List<Folder> findAllByUserWithDecks(@Param("userId") UUID userId);
    // Single query with JOIN

    // GOOD: @EntityGraph
    @EntityGraph(attributePaths = {"decks", "subFolders"})
    List<Folder> findByUserId(UUID userId);
}
```

### 9.2 Batch Operations

```java
@Service
public class CardServiceImpl implements ICardService {

    @Transactional
    @Override
    public ImportResultResponse importCards(UUID deckId, List<CardImportDto> cardDtos) {
        Deck deck = deckRepository.findById(deckId)
            .orElseThrow(() -> new ResourceNotFoundException("Deck not found"));

        int batchSize = 1000;
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < cardDtos.size(); i++) {
            CardImportDto dto = cardDtos.get(i);

            try {
                Card card = new Card();
                card.setDeck(deck);
                card.setFront(dto.getFront());
                card.setBack(dto.getBack());
                deck.getCards().add(card);
                successCount++;
            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }

            // Flush batch
            if (i > 0 && i % batchSize == 0) {
                deckRepository.flush();
                deckRepository.clear();  // Clear persistence context
            }
        }

        deckRepository.flush();

        return new ImportResultResponse(successCount, errors.size(), errors);
    }
}
```

## 10. Configuration Examples

### 10.1 Application Properties

```yaml
# application.yml
spring:
  application:
    name: repeatwise

  datasource:
    url: jdbc:postgresql://localhost:5432/repeatwise
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000

  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for migrations
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_batch_fetch_size: 20
        jdbc:
          batch_size: 1000
        order_inserts: true
        order_updates: true

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

# JWT Config
jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 900000  # 15 minutes
  refresh-token-expiration: 604800000  # 7 days

# Async Config
async:
  core-pool-size: 5
  max-pool-size: 10
  queue-capacity: 100
```

## 11. References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MapStruct](https://mapstruct.org/)
- [Design Patterns](https://refactoring.guru/design-patterns)

---

**Next Steps**:
1. Review JPA entity design: [jpa-entity-design.md](../database/jpa-entity-design.md)
2. Review async operations: [async-operations-design.md](async-operations-design.md)
3. Review domain events: [domain-events.md](domain-events.md)
