# Domain Model - RepeatWise MVP

## 1. Overview

Domain Model của RepeatWise được thiết kế theo Domain-Driven Design (DDD) principles với các entities rõ ràng và business logic encapsulation. Hệ thống tập trung vào **flashcard learning** với **hierarchical organization** và **spaced repetition algorithm**.

## 2. Bounded Contexts

### 2.1 User Management Context
**Responsibility**: Quản lý authentication, authorization, user profile

**Key Entities**: User, UserProfile, SRSSettings

**Business Rules**:
- Email must be unique
- Password hashed with bcrypt (cost factor 12)
- User can only access their own data
- Timezone affects due date calculations

### 2.2 Content Organization Context ⭐
**Responsibility**: Quản lý folder hierarchy, decks, cards

**Key Entities**: Folder, Deck, Card

**Business Rules**:
- Folder can contain sub-folders AND decks (Composite Pattern)
- Max folder depth = 10 levels
- Folder name unique within same parent
- Deck belongs to one folder (nullable for root-level decks)
- Card belongs to one deck

**Design Patterns**:
- **Composite Pattern**: Folder contains Folders + Decks
- **Visitor Pattern**: Traverse folder tree for statistics

### 2.3 Spaced Repetition Context ⭐
**Responsibility**: SRS algorithm, box positions, review scheduling

**Key Entities**: CardBoxPosition, ReviewLog, SRSSettings

**Business Rules**:
- 7 fixed boxes with fixed intervals
- Each card has one box position per user
- Review updates box position based on rating
- Due date calculated from box + interval
- Forgotten card action configurable per user

**Design Patterns**:
- **Strategy Pattern**: ReviewOrderStrategy, ForgottenCardActionStrategy

### 2.4 Statistics & Analytics Context
**Responsibility**: Track learning progress, calculate metrics

**Key Entities**: UserStats, FolderStats

**Business Rules**:
- User stats updated after each review
- Folder stats cached (TTL = 5 minutes)
- Recursive calculation for nested folders
- Streak counter based on last_study_date

## 3. Core Domain Entities

### 3.1 User Entity

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language = Language.VIETNAMESE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Theme theme = Theme.SYSTEM;

    @Column(nullable = false)
    private String timezone = "Asia/Ho_Chi_Minh";

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SRSSettings srsSettings;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserStats userStats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Folder> folders;

    // Business methods
    public void updateProfile(String name, Language language, Theme theme, String timezone) {
        this.name = name;
        this.language = language;
        this.theme = theme;
        this.timezone = timezone;
    }

    public boolean authenticate(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, this.passwordHash);
    }
}

enum Language { VIETNAMESE, ENGLISH }
enum Theme { LIGHT, DARK, SYSTEM }
```

**Aggregate Root**: User

**Invariants**:
- Email must be valid format and unique
- Password must meet complexity requirements (min 8 chars)
- Timezone must be valid IANA timezone

---

### 3.2 RefreshToken Entity ⭐ (MVP)

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", unique = true, nullable = false)
    private String tokenHash; // bcrypt hashed

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public static RefreshToken create(User user, String rawToken, PasswordEncoder encoder) {
        RefreshToken token = new RefreshToken();
        token.user = user;
        token.tokenHash = encoder.encode(rawToken); // bcrypt hash
        token.expiresAt = LocalDateTime.now().plusDays(7);
        return token;
    }

    public boolean isValid(String rawToken, PasswordEncoder encoder) {
        if (revokedAt != null) return false;
        if (LocalDateTime.now().isAfter(expiresAt)) return false;
        return encoder.matches(rawToken, tokenHash);
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}
```

**Aggregate Root**: User (RefreshToken is part of User aggregate)

**Invariants**:
- Token must belong to a user
- Expires at must be > created at
- Revoked at (if set) must be >= created at
- Token hash must be unique (bcrypt, not plain text)

**Business Rules**:
- One-time use: Token revoked after refresh
- 7-day expiration from creation
- Revoked on logout or password change
- Cleanup job deletes tokens > 30 days old

---

### 3.3 Folder Entity ⭐

```java
@Entity
@Table(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_folder_id")
    private Folder parentFolder;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String path; // Materialized path: /1/5/12

    @Column(nullable = false)
    private Integer depth; // 0 = root level, max 10

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // Soft delete

    // Relationships (Composite Pattern)
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<Deck> decks = new ArrayList<>();

    // Business methods
    public void addSubFolder(Folder subFolder) throws MaxDepthExceededException {
        if (this.depth >= 9) { // Parent at depth 9 → child would be depth 10
            throw new MaxDepthExceededException("Cannot create folder beyond depth 10");
        }
        subFolder.parentFolder = this;
        subFolder.depth = this.depth + 1;
        subFolder.path = this.path + "/" + subFolder.id;
        this.subFolders.add(subFolder);
    }

    public void moveTo(Folder newParent) throws MaxDepthExceededException {
        // Prevent circular reference
        if (newParent != null && newParent.path.startsWith(this.path)) {
            throw new CircularReferenceException("Cannot move folder into itself or its descendants");
        }

        // Validate depth after move
        int newDepth = (newParent == null) ? 0 : newParent.depth + 1;
        int depthDelta = newDepth - this.depth;
        int maxDescendantDepth = calculateMaxDescendantDepth();

        if (maxDescendantDepth + depthDelta > 10) {
            throw new MaxDepthExceededException("Move would exceed max depth 10");
        }

        // Update parent and recalculate paths
        this.parentFolder = newParent;
        recalculatePathAndDepth();
    }

    public Folder deepCopy(Folder destinationParent) {
        Folder copy = new Folder();
        copy.user = this.user;
        copy.name = this.name + " (Copy)";
        copy.description = this.description;
        copy.parentFolder = destinationParent;

        // Recursively copy sub-folders
        for (Folder subFolder : this.subFolders) {
            copy.addSubFolder(subFolder.deepCopy(copy));
        }

        // Recursively copy decks
        for (Deck deck : this.decks) {
            copy.decks.add(deck.deepCopy(copy));
        }

        return copy;
    }

    public int countTotalItems() {
        int count = 1; // Self
        for (Folder subFolder : subFolders) {
            count += subFolder.countTotalItems();
        }
        count += decks.size();
        return count;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        // Cascade soft delete to children
        for (Folder subFolder : subFolders) {
            subFolder.softDelete();
        }
        for (Deck deck : decks) {
            deck.softDelete();
        }
    }
}
```

**Aggregate Root**: Folder (for folder tree operations)

**Invariants**:
- Depth must be between 0 and 10
- Name must not be empty
- Path must follow format `/id/id/id`
- Cannot have circular parent-child references

### 3.4 Deck Entity

```java
@Entity
@Table(name = "decks")
public class Deck {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder; // Nullable for root-level decks

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // Relationships
    @OneToMany(mappedBy = "deck", cascade = CascadeType.ALL)
    private List<Card> cards = new ArrayList<>();

    // Business methods
    public void moveTo(Folder newFolder) {
        this.folder = newFolder;
    }

    public Deck deepCopy(Folder destinationFolder) {
        Deck copy = new Deck();
        copy.user = this.user;
        copy.name = this.name + " (Copy)";
        copy.description = this.description;
        copy.folder = destinationFolder;

        // Deep copy all cards
        for (Card card : this.cards) {
            copy.cards.add(card.deepCopy(copy));
        }

        return copy;
    }

    public int getCardCount() {
        return (int) cards.stream()
            .filter(card -> card.getDeletedAt() == null)
            .count();
    }

    public int getDueCardCount(LocalDate today) {
        return (int) cards.stream()
            .filter(card -> card.getDeletedAt() == null)
            .filter(card -> card.getBoxPosition() != null &&
                           card.getBoxPosition().getDueDate().isBefore(today.plusDays(1)))
            .count();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        for (Card card : cards) {
            card.softDelete();
        }
    }
}
```

**Invariants**:
- Name must not be empty
- Name must be unique within same folder
- Max 10,000 cards per deck

### 3.5 Card Entity

```java
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id", nullable = false)
    private Deck deck;

    @Column(nullable = false, length = 5000)
    private String front; // Question

    @Column(nullable = false, length = 5000)
    private String back; // Answer

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // Relationships
    @OneToOne(mappedBy = "card", cascade = CascadeType.ALL)
    private CardBoxPosition boxPosition;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<ReviewLog> reviewLogs = new ArrayList<>();

    // Business methods
    public Card deepCopy(Deck destinationDeck) {
        Card copy = new Card();
        copy.deck = destinationDeck;
        copy.front = this.front;
        copy.back = this.back;
        // Note: Do NOT copy boxPosition or reviewLogs (new card for user)
        return copy;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
```

**Invariants**:
- Front and back must not be empty
- Max 5000 characters for front/back

### 3.6 CardBoxPosition Entity ⭐ (SRS Core)

```java
@Entity
@Table(name = "card_box_position",
       indexes = {
           @Index(name = "idx_card_box_user_due",
                  columnList = "user_id, due_date, current_box")
       })
public class CardBoxPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer currentBox = 1; // 1-7

    @Column(nullable = false)
    private BigDecimal easeFactor = new BigDecimal("2.5");

    @Column(nullable = false)
    private Integer intervalDays; // Interval based on box

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime lastReviewedAt;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Integer lapseCount = 0; // Count of "Again" ratings

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public void updateAfterReview(Rating rating, SRSSettings settings) {
        this.lastReviewedAt = LocalDateTime.now();
        this.reviewCount++;

        switch (rating) {
            case AGAIN: // < 1 min
                handleForgottenCard(settings);
                this.lapseCount++;
                break;
            case HARD: // < 6 min
                // Stay in same box, reduce interval
                this.intervalDays = getBoxInterval(this.currentBox) / 2;
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case GOOD: // Next interval
                moveToNextBox();
                break;
            case EASY: // 4x interval
                moveToNextBox();
                this.intervalDays *= 4;
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
        }
    }

    private void handleForgottenCard(SRSSettings settings) {
        switch (settings.getForgottenCardAction()) {
            case MOVE_TO_BOX_1:
                this.currentBox = 1;
                this.intervalDays = getBoxInterval(1);
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case MOVE_DOWN_N_BOXES:
                int targetBox = Math.max(1, this.currentBox - settings.getMoveDownBoxes());
                this.currentBox = targetBox;
                this.intervalDays = getBoxInterval(targetBox);
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
            case STAY_IN_BOX:
                // Keep same box, reduce interval
                this.intervalDays = getBoxInterval(this.currentBox) / 2;
                this.dueDate = LocalDate.now().plusDays(this.intervalDays);
                break;
        }
    }

    private void moveToNextBox() {
        if (this.currentBox < 7) {
            this.currentBox++;
        }
        this.intervalDays = getBoxInterval(this.currentBox);
        this.dueDate = LocalDate.now().plusDays(this.intervalDays);
    }

    private Integer getBoxInterval(Integer box) {
        // Fixed intervals for 7-box system
        return switch (box) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 7;
            case 4 -> 14;
            case 5 -> 30;
            case 6 -> 60;
            case 7 -> 120;
            default -> 1;
        };
    }
}

enum Rating { AGAIN, HARD, GOOD, EASY }
```

**Aggregate Root**: CardBoxPosition

**Invariants**:
- Current box must be between 1 and 7
- Due date cannot be in the past (except for overdue cards)
- Review count and lapse count must be non-negative

### 3.7 SRSSettings Entity

```java
@Entity
@Table(name = "srs_settings")
public class SRSSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalBoxes = 7; // Fixed for MVP

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewOrder reviewOrder = ReviewOrder.RANDOM;

    @Column(nullable = false)
    private Boolean notificationEnabled = true;

    @Column(nullable = false)
    private LocalTime notificationTime = LocalTime.of(9, 0);

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForgottenCardAction forgottenCardAction = ForgottenCardAction.MOVE_TO_BOX_1;

    @Column(nullable = false)
    private Integer moveDownBoxes = 1; // Used when action = MOVE_DOWN_N_BOXES

    @Column(nullable = false)
    private Integer newCardsPerDay = 20;

    @Column(nullable = false)
    private Integer maxReviewsPerDay = 200;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public void updateSettings(ReviewOrder order, ForgottenCardAction action,
                               Integer moveDownBoxes, Integer newCards, Integer maxReviews) {
        this.reviewOrder = order;
        this.forgottenCardAction = action;
        this.moveDownBoxes = moveDownBoxes;
        this.newCardsPerDay = newCards;
        this.maxReviewsPerDay = maxReviews;
    }
}

enum ReviewOrder { ASCENDING, DESCENDING, RANDOM }
enum ForgottenCardAction { MOVE_TO_BOX_1, MOVE_DOWN_N_BOXES, STAY_IN_BOX }
```

### 3.8 ReviewLog Entity

```java
@Entity
@Table(name = "review_logs",
       indexes = {
           @Index(name = "idx_review_logs_user_date",
                  columnList = "user_id, reviewed_at")
       })
public class ReviewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rating rating;

    @Column(nullable = false)
    private Integer previousBox;

    @Column(nullable = false)
    private Integer newBox;

    @Column(nullable = false)
    private Integer intervalDays;

    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    // Business methods
    public static ReviewLog create(Card card, User user, Rating rating,
                                    Integer prevBox, Integer newBox, Integer interval) {
        ReviewLog log = new ReviewLog();
        log.card = card;
        log.user = user;
        log.rating = rating;
        log.previousBox = prevBox;
        log.newBox = newBox;
        log.intervalDays = interval;
        log.reviewedAt = LocalDateTime.now();
        return log;
    }
}
```

### 3.9 UserStats Entity

```java
@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalCardsLearned = 0;

    @Column(nullable = false)
    private Integer streakDays = 0;

    private LocalDate lastStudyDate;

    @Column(nullable = false)
    private Integer totalStudyTimeMinutes = 0;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Business methods
    public void recordReview(LocalDate today) {
        this.totalCardsLearned++;
        updateStreak(today);
        this.updatedAt = LocalDateTime.now();
    }

    private void updateStreak(LocalDate today) {
        if (lastStudyDate == null) {
            streakDays = 1;
        } else if (lastStudyDate.equals(today)) {
            // Same day, no change
        } else if (lastStudyDate.equals(today.minusDays(1))) {
            // Consecutive day
            streakDays++;
        } else {
            // Streak broken
            streakDays = 1;
        }
        lastStudyDate = today;
    }
}
```

### 3.10 FolderStats Entity (Denormalized Cache)

```java
@Entity
@Table(name = "folder_stats")
@IdClass(FolderStatsId.class)
public class FolderStats {
    @Id
    @Column(name = "folder_id")
    private UUID folderId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private Integer totalCardsCount = 0; // Recursive

    @Column(nullable = false)
    private Integer dueCardsCount = 0; // Recursive

    @Column(nullable = false)
    private Integer newCardsCount = 0;

    @Column(nullable = false)
    private Integer matureCardsCount = 0; // Box >= 5

    private LocalDateTime lastComputedAt;

    // Business methods
    public boolean isStale(int maxAgeMinutes) {
        if (lastComputedAt == null) return true;
        return lastComputedAt.isBefore(LocalDateTime.now().minusMinutes(maxAgeMinutes));
    }
}

@Embeddable
class FolderStatsId implements Serializable {
    private UUID folderId;
    private UUID userId;
}
```

## 4. Value Objects

### 4.1 FolderPath (Value Object)

```java
@Embeddable
public class FolderPath {
    private String path; // Format: /uuid1/uuid2/uuid3

    public FolderPath(String path) {
        validate(path);
        this.path = path;
    }

    public FolderPath append(UUID childId) {
        return new FolderPath(this.path + "/" + childId.toString());
    }

    public boolean isDescendantOf(FolderPath ancestorPath) {
        return this.path.startsWith(ancestorPath.path + "/");
    }

    public int getDepth() {
        if (path.equals("/")) return 0;
        return path.split("/").length - 1;
    }

    private void validate(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path must start with /");
        }
    }
}
```

## 5. Domain Services

### 5.1 FolderCopyService (Domain Service)

```java
@Service
public class FolderCopyService {

    public CopyResult copyFolder(Folder source, Folder destination, User user) {
        int totalItems = source.countTotalItems();

        if (totalItems <= 50) {
            // Sync copy
            Folder copy = source.deepCopy(destination);
            folderRepository.save(copy);
            return CopyResult.sync(copy);
        } else if (totalItems <= 500) {
            // Async copy
            String jobId = UUID.randomUUID().toString();
            asyncCopyExecutor.execute(() -> {
                Folder copy = source.deepCopy(destination);
                folderRepository.save(copy);
                notificationService.send(user, "Folder copied successfully");
            });
            return CopyResult.async(jobId);
        } else {
            throw new FolderTooLargeException("Max 500 items allowed");
        }
    }
}
```

### 5.2 SRSCalculationService (Domain Service)

```java
@Service
public class SRSCalculationService {

    public List<Card> getDueCards(User user, SRSSettings settings, LocalDate today) {
        // Query due cards
        List<CardBoxPosition> duePositions = cardBoxPositionRepository
            .findByUserAndDueDateLessThanEqual(user, today);

        // Apply review order strategy
        ReviewOrderStrategy strategy = getStrategy(settings.getReviewOrder());
        List<Card> cards = duePositions.stream()
            .map(CardBoxPosition::getCard)
            .collect(Collectors.toList());

        return strategy.order(cards);
    }

    private ReviewOrderStrategy getStrategy(ReviewOrder order) {
        return switch (order) {
            case ASCENDING -> new AscendingReviewStrategy();
            case DESCENDING -> new DescendingReviewStrategy();
            case RANDOM -> new RandomReviewStrategy();
        };
    }
}
```

## 6. Domain Events

### 6.1 CardReviewed Event

```java
public class CardReviewedEvent extends DomainEvent {
    private final UUID cardId;
    private final UUID userId;
    private final Rating rating;
    private final Integer previousBox;
    private final Integer newBox;
    private final LocalDateTime reviewedAt;

    // Triggers:
    // 1. Update UserStats (increment cards reviewed, update streak)
    // 2. Create ReviewLog
    // 3. Invalidate FolderStats cache
}
```

### 6.2 FolderMovedEvent

```java
public class FolderMovedEvent extends DomainEvent {
    private final UUID folderId;
    private final UUID oldParentId;
    private final UUID newParentId;

    // Triggers:
    // 1. Invalidate FolderStats for old parent
    // 2. Invalidate FolderStats for new parent
    // 3. Recalculate paths for all descendants
}
```

## 7. Repositories (Interfaces)

```java
public interface FolderRepository extends JpaRepository<Folder, UUID> {
    List<Folder> findByUserAndParentFolderIsNullAndDeletedAtIsNull(User user);
    List<Folder> findByUserAndPathStartingWith(User user, String pathPrefix);
    Optional<Folder> findByIdAndUserId(UUID id, UUID userId);
}

public interface CardBoxPositionRepository extends JpaRepository<CardBoxPosition, UUID> {
    @Query("SELECT cbp FROM CardBoxPosition cbp " +
           "WHERE cbp.user.id = :userId AND cbp.dueDate <= :date " +
           "ORDER BY cbp.dueDate ASC, cbp.currentBox ASC")
    List<CardBoxPosition> findDueCards(@Param("userId") UUID userId,
                                        @Param("date") LocalDate date);

    @Query("SELECT cbp.currentBox, COUNT(cbp) FROM CardBoxPosition cbp " +
           "WHERE cbp.user.id = :userId GROUP BY cbp.currentBox")
    Map<Integer, Long> getBoxDistribution(@Param("userId") UUID userId);
}
```

## 8. Entity Relationships Diagram

```
User (1) ──────────< (N) Folder
  │                     │
  │                     │ parent_folder_id (self-referencing)
  │                     │
  │                  Folder (Composite Pattern)
  │                     │
  │                     ├─── (N) SubFolders
  │                     └─── (N) Decks
  │
  ├─── (1:1) SRSSettings
  ├─── (1:1) UserStats
  │
  └────< (N) CardBoxPosition
              │
              └─── (1:1) Card
                     │
                     ├─── (N) ReviewLog
                     └─── (N:1) Deck
```

## 9. Domain Events ⭐

### 9.1 Overview

Domain Events là cách để decouple business logic và side effects. Events được publish khi aggregate state thay đổi, và listeners xử lý async operations như update statistics, send notifications.

### 9.2 Event Types

#### CardReviewedEvent
**Published when**: User submits review rating cho một card

**Event Data**:
```java
public class CardReviewedEvent {
    private UUID cardId;
    private UUID userId;
    private UUID deckId;
    private Rating rating; // AGAIN, HARD, GOOD, EASY
    private int previousBox;
    private int newBox;
    private LocalDateTime reviewedAt;
}
```

**Listeners**:
1. **UserStatsUpdateListener**: Increment total reviews, update streak
2. **FolderStatsInvalidationListener**: Mark folder stats as stale
3. **NotificationListener** (Future): Send achievement notifications

**Use Case**: UC-019 Review Cards with SRS

---

#### FolderMovedEvent
**Published when**: Folder moved to different parent

**Event Data**:
```java
public class FolderMovedEvent {
    private UUID folderId;
    private UUID userId;
    private UUID oldParentId;
    private UUID newParentId;
    private String oldPath; // "/1/5/12"
    private String newPath; // "/1/8/12"
}
```

**Listeners**:
1. **FolderPathUpdateListener**: Update path của all descendants
2. **FolderStatsRecalculationListener**: Recalculate stats cho old & new parents

**Use Case**: UC-007 Move Folder

---

#### DeckCreatedEvent
**Published when**: New deck created

**Event Data**:
```java
public class DeckCreatedEvent {
    private UUID deckId;
    private UUID userId;
    private UUID folderId; // Nullable
}
```

**Listeners**:
1. **FolderStatsInvalidationListener**: Increment deck count cho folder

**Use Case**: UC-011 Create Deck

---

#### CardImportedEvent
**Published when**: Bulk import completes

**Event Data**:
```java
public class CardImportedEvent {
    private UUID deckId;
    private UUID userId;
    private int cardsImported;
    private int cardsFailed;
}
```

**Listeners**:
1. **FolderStatsRecalculationListener**: Update card counts
2. **NotificationListener**: Send import completion notification

**Use Case**: UC-015 Import Cards from File

---

### 9.3 Event Bus Implementation (MVP)

**Technology**: Spring ApplicationEventPublisher (in-process)

**Publishing Events**:
```java
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void submitReview(UUID cardId, Rating rating) {
        // ... update card box position ...

        // Publish event
        CardReviewedEvent event = new CardReviewedEvent(
            cardId, userId, deckId, rating, previousBox, newBox, LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
    }
}
```

**Listening to Events**:
```java
@Component
public class UserStatsUpdateListener {

    @Async // Run asynchronously
    @EventListener
    public void onCardReviewed(CardReviewedEvent event) {
        // Update user stats
        userStatsRepository.incrementReviewCount(event.getUserId());
        userStatsRepository.updateStreak(event.getUserId(), event.getReviewedAt());
    }
}
```

**Async Configuration**:
```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}
```

### 9.4 Benefits

1. **Decoupling**: Review logic không phụ thuộc vào stats update logic
2. **Performance**: Stats update async, không block review response
3. **Extensibility**: Thêm listeners mới dễ dàng (e.g., notification, analytics)
4. **Testability**: Test domain logic độc lập với side effects

### 9.5 Trade-offs

**Pros**:
- Clean separation of concerns
- Easy to add new listeners
- Non-blocking operations

**Cons**:
- Eventual consistency (stats không real-time)
- Debugging phức tạp hơn (async flow)
- Memory overhead (event objects)

**Acceptable for MVP**: Yes, vì stats không cần real-time

### 9.6 Future Enhancement

**Production**: External Message Queue (RabbitMQ, Kafka)

**Why**:
- Distributed systems: Multiple backend instances
- Retry mechanism: Guaranteed delivery
- Persistence: Events không mất nếu server crash
- Monitoring: Track event processing metrics

**Implementation**:
```java
// Replace ApplicationEventPublisher with RabbitTemplate
@Autowired
private RabbitTemplate rabbitTemplate;

rabbitTemplate.convertAndSend("card.reviewed", event);
```

---

## 10. Conclusion

Domain Model của RepeatWise tập trung vào:
1. **Hierarchical Organization**: Composite Pattern cho folder tree
2. **SRS Algorithm**: Strategy Pattern cho review order và forgotten actions
3. **Performance**: Denormalized FolderStats cho recursive queries
4. **Data Integrity**: Aggregate roots, invariants, soft delete
5. **Scalability**: Indexed queries, async operations
6. **Event-Driven Architecture**: Domain events cho async stats update

**Next**: See [data-dictionary.md](./data-dictionary.md) for detailed database schema.
