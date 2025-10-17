# Design Patterns Implementation - RepeatWise MVP

## Overview

RepeatWise sử dụng các design patterns phổ biến để đảm bảo code maintainable, testable và extensible. Document này mô tả chi tiết cách implement các patterns.

---

## 1. Composite Pattern ⭐ (Folder Tree Structure)

### Problem
Cần tổ chức folders theo cấu trúc cây phân cấp (hierarchical tree):
- Folder có thể chứa sub-folders
- Folder có thể chứa decks
- Depth không giới hạn (tối đa 10 levels)
- Cần traverse tree để tính statistics

### Solution
Composite pattern: Folder là composite node, có thể chứa Folders hoặc Decks (leaf nodes).

### Implementation

#### Folder Entity (Composite)
```java
@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    // Self-referencing relationship (composite)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> childFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Deck> decks = new ArrayList<>();

    // Materialized path for quick tree queries
    @Column(name = "path")
    private String path; // e.g., "/uuid1/uuid2/uuid3"

    @Column(name = "depth")
    private Integer depth; // 0 for root level

    // Helper methods
    public void addChildFolder(Folder child) {
        childFolders.add(child);
        child.setParentFolder(this);
        child.setDepth(this.depth + 1);
        child.setPath(this.path + "/" + child.getId());
    }

    public void addDeck(Deck deck) {
        decks.add(deck);
        deck.setFolder(this);
    }

    public boolean isRoot() {
        return parentFolder == null;
    }

    public boolean isLeaf() {
        return childFolders.isEmpty() && decks.isEmpty();
    }

    // Accept visitor for statistics calculation
    public void accept(FolderVisitor visitor) {
        visitor.visit(this);
        for (Folder child : childFolders) {
            child.accept(visitor);
        }
    }
}
```

#### Folder Service Operations
```java
@Service
public class FolderServiceImpl implements IFolderService {

    @Transactional
    public FolderResponse createFolder(CreateFolderRequest request, UUID userId) {
        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setUserId(userId);

        if (request.getParentFolderId() != null) {
            Folder parent = folderRepository.findById(request.getParentFolderId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent folder not found"));

            // Validate depth constraint
            if (parent.getDepth() >= 9) { // Max depth = 10
                throw new ValidationException("Maximum folder depth (10 levels) exceeded");
            }

            parent.addChildFolder(folder); // Composite pattern method
        } else {
            // Root level folder
            folder.setDepth(0);
            folder.setPath("/" + folder.getId());
        }

        return folderMapper.toResponse(folderRepository.save(folder));
    }

    // Get all descendants using materialized path
    public List<Folder> getDescendants(UUID folderId) {
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        // Query: WHERE path LIKE '/parent_id/%'
        return folderRepository.findByPathStartingWith(folder.getPath() + "/");
    }
}
```

### Benefits
- **Uniform treatment**: Treat individual folders và composite folders uniformly
- **Easy traversal**: Recursive operations via `accept(visitor)`
- **Hierarchical organization**: Natural tree structure
- **Scalability**: Materialized path cho fast queries

---

## 2. Strategy Pattern ⭐ (SRS Behaviors)

### Problem
SRS algorithm có nhiều configurable behaviors:
- Review order: Ascending, Descending, Random
- Forgotten card actions: Move to Box 1, Move down N boxes, Stay in box

Cần dễ dàng thêm strategies mới mà không sửa code cũ.

### Solution
Strategy pattern: Define strategy interfaces, implement multiple concrete strategies.

### Implementation

#### 2.1 Review Order Strategy

**Strategy Interface**
```java
public interface ReviewOrderStrategy {
    List<CardBoxPosition> applyOrder(List<CardBoxPosition> dueCards);
}
```

**Concrete Strategies**
```java
@Component
public class AscendingReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<CardBoxPosition> applyOrder(List<CardBoxPosition> dueCards) {
        // Review from Box 1 → Box 7 (harder cards first)
        return dueCards.stream()
            .sorted(Comparator.comparing(CardBoxPosition::getCurrentBox))
            .toList();
    }
}

@Component
public class DescendingReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<CardBoxPosition> applyOrder(List<CardBoxPosition> dueCards) {
        // Review from Box 7 → Box 1 (easier cards first)
        return dueCards.stream()
            .sorted(Comparator.comparing(CardBoxPosition::getCurrentBox).reversed())
            .toList();
    }
}

@Component
public class RandomReviewStrategy implements ReviewOrderStrategy {
    @Override
    public List<CardBoxPosition> applyOrder(List<CardBoxPosition> dueCards) {
        // Shuffle cards randomly
        List<CardBoxPosition> shuffled = new ArrayList<>(dueCards);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
```

**Strategy Factory**
```java
@Component
public class ReviewOrderStrategyFactory {

    private final Map<ReviewOrder, ReviewOrderStrategy> strategies;

    @Autowired
    public ReviewOrderStrategyFactory(
            AscendingReviewStrategy ascending,
            DescendingReviewStrategy descending,
            RandomReviewStrategy random) {

        this.strategies = Map.of(
            ReviewOrder.ASCENDING, ascending,
            ReviewOrder.DESCENDING, descending,
            ReviewOrder.RANDOM, random
        );
    }

    public ReviewOrderStrategy getStrategy(ReviewOrder order) {
        ReviewOrderStrategy strategy = strategies.get(order);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown review order: " + order);
        }
        return strategy;
    }
}
```

**Usage in Service**
```java
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewOrderStrategyFactory strategyFactory;

    public List<CardResponse> getDueCards(UUID userId, ReviewRequest request) {
        // Get user's review order preference
        SrsSettings settings = srsSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("SRS settings not found"));

        // Fetch due cards from database
        List<CardBoxPosition> dueCards = cardBoxPositionRepository
            .findDueCards(userId, LocalDate.now());

        // Apply review order strategy
        ReviewOrderStrategy strategy = strategyFactory.getStrategy(settings.getReviewOrder());
        List<CardBoxPosition> ordered = strategy.applyOrder(dueCards);

        // Map to response DTOs
        return ordered.stream()
            .map(cardMapper::toReviewResponse)
            .toList();
    }
}
```

#### 2.2 Forgotten Card Action Strategy

**Strategy Interface**
```java
public interface ForgottenCardActionStrategy {
    CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings);
}
```

**Concrete Strategies**
```java
@Component
public class MoveToBox1Strategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        // Reset card to Box 1
        cardPosition.setCurrentBox(1);
        cardPosition.setIntervalDays(1);
        cardPosition.setDueDate(LocalDate.now().plusDays(1));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }
}

@Component
public class MoveDownNBoxesStrategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        // Move down N boxes (configurable)
        int moveDown = settings.getMoveDownBoxes(); // e.g., 1, 2, 3
        int newBox = Math.max(1, cardPosition.getCurrentBox() - moveDown);

        cardPosition.setCurrentBox(newBox);
        cardPosition.setIntervalDays(getIntervalForBox(newBox));
        cardPosition.setDueDate(LocalDate.now().plusDays(cardPosition.getIntervalDays()));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }

    private int getIntervalForBox(int box) {
        int[] intervals = {1, 3, 7, 14, 30, 60, 120};
        return intervals[box - 1];
    }
}

@Component
public class StayInBoxStrategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        // Stay in current box, but reduce interval slightly
        int currentInterval = cardPosition.getIntervalDays();
        int reducedInterval = Math.max(1, currentInterval / 2);

        cardPosition.setIntervalDays(reducedInterval);
        cardPosition.setDueDate(LocalDate.now().plusDays(reducedInterval));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }
}
```

**Strategy Factory**
```java
@Component
public class ForgottenCardActionStrategyFactory {

    private final Map<ForgottenCardAction, ForgottenCardActionStrategy> strategies;

    @Autowired
    public ForgottenCardActionStrategyFactory(
            MoveToBox1Strategy moveToBox1,
            MoveDownNBoxesStrategy moveDownN,
            StayInBoxStrategy stayInBox) {

        this.strategies = Map.of(
            ForgottenCardAction.MOVE_TO_BOX_1, moveToBox1,
            ForgottenCardAction.MOVE_DOWN_N_BOXES, moveDownN,
            ForgottenCardAction.STAY_IN_BOX, stayInBox
        );
    }

    public ForgottenCardActionStrategy getStrategy(ForgottenCardAction action) {
        return strategies.get(action);
    }
}
```

### Benefits
- **Open/Closed Principle**: Easy to add new strategies without modifying existing code
- **Testable**: Each strategy can be tested independently
- **Runtime selection**: User can configure strategies dynamically
- **Clean separation**: Each strategy in its own class

---

## 3. Visitor Pattern ⭐ (Folder Statistics Calculation)

### Problem
Cần traverse folder tree để tính statistics (recursive):
- Total cards trong folder và tất cả sub-folders
- Due cards count
- New cards count
- Mature cards count

Không muốn thêm logic statistics vào Folder entity.

### Solution
Visitor pattern: Separate algorithm (statistics calculation) from object structure (Folder tree).

### Implementation

**Visitor Interface**
```java
public interface FolderVisitor {
    void visit(Folder folder);
}
```

**Concrete Visitor: Folder Statistics**
```java
@Component
public class FolderStatsVisitor implements FolderVisitor {

    @Autowired
    private CardBoxPositionRepository cardBoxPositionRepository;

    @Autowired
    private CardRepository cardRepository;

    private FolderStats stats;
    private UUID userId;

    public FolderStats calculateStats(Folder rootFolder, UUID userId) {
        this.userId = userId;
        this.stats = new FolderStats();
        stats.setFolderId(rootFolder.getId());
        stats.setUserId(userId);

        // Traverse tree
        rootFolder.accept(this);

        stats.setLastComputedAt(Instant.now());
        return stats;
    }

    @Override
    public void visit(Folder folder) {
        // Count cards in this folder's decks
        for (Deck deck : folder.getDecks()) {
            List<Card> cards = cardRepository.findByDeckId(deck.getId());
            stats.setTotalCardsCount(stats.getTotalCardsCount() + cards.size());

            // Count due cards
            long dueCount = cardBoxPositionRepository.countDueCardsByDeck(deck.getId(), userId, LocalDate.now());
            stats.setDueCardsCount(stats.getDueCardsCount() + (int) dueCount);

            // Count new cards (review_count = 0)
            long newCount = cardBoxPositionRepository.countNewCardsByDeck(deck.getId(), userId);
            stats.setNewCardsCount(stats.getNewCardsCount() + (int) newCount);

            // Count mature cards (box >= 5)
            long matureCount = cardBoxPositionRepository.countMatureCardsByDeck(deck.getId(), userId);
            stats.setMatureCardsCount(stats.getMatureCardsCount() + (int) matureCount);
        }
    }
}
```

**Usage in Service**
```java
@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private FolderStatsVisitor statsVisitor;

    @Autowired
    private FolderStatsRepository folderStatsRepository;

    @Transactional
    public FolderStatsResponse getFolderStatistics(UUID folderId, UUID userId) {
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        // Check cached stats
        Optional<FolderStats> cachedStats = folderStatsRepository.findByFolderIdAndUserId(folderId, userId);

        if (cachedStats.isPresent() && !isStale(cachedStats.get())) {
            // Return cached stats (TTL: 5 minutes)
            return statsMapper.toResponse(cachedStats.get());
        }

        // Recalculate stats using visitor pattern
        FolderStats stats = statsVisitor.calculateStats(folder, userId);

        // Save to cache
        folderStatsRepository.save(stats);

        return statsMapper.toResponse(stats);
    }

    private boolean isStale(FolderStats stats) {
        Instant fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES);
        return stats.getLastComputedAt().isBefore(fiveMinutesAgo);
    }
}
```

### Benefits
- **Separation of concerns**: Statistics logic separated from Folder entity
- **Reusable**: Visitor can be used for multiple folders
- **Extensible**: Easy to add new visitors (e.g., ExportVisitor, ValidationVisitor)
- **Performance**: Combine with caching to avoid expensive recursive queries

---

## 4. Repository Pattern (Data Access)

### Problem
Decouple business logic from database access, enable testability.

### Solution
Spring Data JPA repositories with custom queries.

### Implementation

**Repository Interface**
```java
@Repository
public interface FolderRepository extends JpaRepository<Folder, UUID> {

    // Derived query methods (Spring Data JPA generates implementation)
    List<Folder> findByUserIdAndParentFolderIsNull(UUID userId);

    List<Folder> findByParentFolderId(UUID parentFolderId);

    // Custom query with @Query
    @Query("SELECT f FROM Folder f WHERE f.userId = :userId AND f.path LIKE :pathPattern")
    List<Folder> findDescendants(@Param("userId") UUID userId, @Param("pathPattern") String pathPattern);

    // Native SQL query for complex operations
    @Query(value = """
        WITH RECURSIVE folder_tree AS (
            SELECT id, name, parent_folder_id, path, depth
            FROM folders
            WHERE id = :folderId

            UNION ALL

            SELECT f.id, f.name, f.parent_folder_id, f.path, f.depth
            FROM folders f
            INNER JOIN folder_tree ft ON f.parent_folder_id = ft.id
        )
        SELECT * FROM folder_tree
        """, nativeQuery = true)
    List<Folder> findFolderTreeRecursive(@Param("folderId") UUID folderId);
}
```

**Usage in Service**
```java
@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private FolderRepository folderRepository;

    public List<FolderResponse> getRootFolders(UUID userId) {
        List<Folder> folders = folderRepository.findByUserIdAndParentFolderIsNull(userId);
        return folders.stream()
            .map(folderMapper::toResponse)
            .toList();
    }

    public List<FolderResponse> getDescendants(UUID folderId, UUID userId) {
        Folder folder = folderRepository.findById(folderId)
            .orElseThrow(() -> new ResourceNotFoundException("Folder not found"));

        String pathPattern = folder.getPath() + "/%";
        List<Folder> descendants = folderRepository.findDescendants(userId, pathPattern);

        return descendants.stream()
            .map(folderMapper::toResponse)
            .toList();
    }
}
```

### Benefits
- **Abstraction**: Business logic không phụ thuộc vào database implementation
- **Testability**: Dễ mock repositories trong unit tests
- **Maintainability**: Database queries centralized trong repositories
- **Spring Data JPA**: Automatic query generation

---

## 5. DTO Pattern (Data Transfer Objects)

### Problem
Không expose domain entities trực tiếp qua API. Cần decouple API contract from domain model.

### Solution
Use DTOs với MapStruct for mapping.

### Implementation

**Request DTO**
```java
public record CreateFolderRequest(
    @NotBlank(message = "Folder name is required")
    @Size(max = 100, message = "Folder name must not exceed 100 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    UUID parentFolderId
) {}
```

**Response DTO**
```java
public record FolderResponse(
    UUID id,
    String name,
    String description,
    UUID parentId,
    Integer depth,
    String path,
    Integer childrenCount,
    Integer deckCount,
    Instant createdAt,
    Instant updatedAt
) {}
```

**MapStruct Mapper**
```java
@Mapper(componentModel = "spring")
public interface FolderMapper {

    @Mapping(target = "parentId", source = "parentFolder.id")
    @Mapping(target = "childrenCount", expression = "java(folder.getChildFolders().size())")
    @Mapping(target = "deckCount", expression = "java(folder.getDecks().size())")
    FolderResponse toResponse(Folder folder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "childFolders", ignore = true)
    @Mapping(target = "decks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Folder toEntity(CreateFolderRequest request);

    List<FolderResponse> toResponseList(List<Folder> folders);
}
```

**Controller Usage**
```java
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Autowired
    private IFolderService folderService;

    @PostMapping
    public ResponseEntity<FolderResponse> createFolder(
            @Valid @RequestBody CreateFolderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        FolderResponse response = folderService.createFolder(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

### Benefits
- **Decoupling**: API contract independent of domain model
- **Validation**: Input validation at DTO level
- **Security**: Don't expose internal entity structure
- **Flexibility**: Different DTOs for different use cases (create vs update vs response)
- **Auto-mapping**: MapStruct generates mapping code at compile time (no reflection overhead)

---

## 6. Domain Events Pattern (Async Operations)

### Problem
Khi card được reviewed, cần update nhiều thứ:
- Update user stats (streak, cards reviewed)
- Invalidate folder stats cache
- Send notification if daily goal achieved

Không muốn couple review logic với stats/notification logic.

### Solution
Domain events với Spring's `ApplicationEventPublisher`.

### Implementation

**Domain Event**
```java
public record CardReviewedEvent(
    UUID cardId,
    UUID userId,
    UUID deckId,
    ReviewRating rating,
    int previousBox,
    int newBox,
    Instant reviewedAt
) {}
```

**Event Publisher (in Service)**
```java
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public ReviewResponse submitReview(UUID userId, ReviewRequest request) {
        // Core review logic
        CardBoxPosition cardPosition = // ... fetch and update

        // Save review log
        reviewLogRepository.save(reviewLog);

        // Publish domain event
        eventPublisher.publishEvent(new CardReviewedEvent(
            request.getCardId(),
            userId,
            cardPosition.getCard().getDeck().getId(),
            request.getRating(),
            previousBox,
            newBox,
            Instant.now()
        ));

        return reviewMapper.toResponse(cardPosition);
    }
}
```

**Event Listeners**
```java
@Component
public class UserStatsUpdateListener {

    @Autowired
    private UserStatsRepository userStatsRepository;

    @EventListener
    @Async
    public void handleCardReviewed(CardReviewedEvent event) {
        UserStats stats = userStatsRepository.findByUserId(event.userId())
            .orElseGet(() -> createDefaultStats(event.userId()));

        stats.incrementCardsReviewed();
        stats.updateStreak(event.reviewedAt());

        userStatsRepository.save(stats);
    }
}

@Component
public class FolderStatsInvalidationListener {

    @Autowired
    private FolderStatsRepository folderStatsRepository;

    @EventListener
    @Async
    public void handleCardReviewed(CardReviewedEvent event) {
        // Invalidate folder stats cache for all ancestor folders
        Folder folder = deckRepository.findById(event.deckId())
            .orElseThrow()
            .getFolder();

        invalidateAncestorStats(folder);
    }

    private void invalidateAncestorStats(Folder folder) {
        folderStatsRepository.deleteByFolderId(folder.getId());

        if (folder.getParentFolder() != null) {
            invalidateAncestorStats(folder.getParentFolder());
        }
    }
}
```

### Benefits
- **Decoupling**: Review service không biết về stats/notification logic
- **Async processing**: Listeners execute asynchronously (@Async)
- **Extensibility**: Dễ add new listeners mà không modify existing code
- **Testability**: Event listeners có thể test independently

---

## Summary

| Pattern | Use Case | Benefits |
|---------|----------|----------|
| **Composite** | Folder tree structure | Uniform treatment, easy traversal |
| **Strategy** | SRS behaviors (review order, forgotten card actions) | Open/Closed, testable, runtime selection |
| **Visitor** | Folder statistics calculation | Separation of concerns, reusable |
| **Repository** | Data access abstraction | Testability, maintainability |
| **DTO** | API request/response | Decoupling, validation, security |
| **Domain Events** | Async updates (stats, notifications) | Decoupling, async, extensibility |

---

**Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: Design Complete
