# SRS Algorithm Design - RepeatWise MVP

## Overview

RepeatWise sử dụng **Box-Based Spaced Repetition System** (dựa trên Leitner System) với 7 boxes cố định và intervals được định nghĩa sẵn.

**Algorithm Type**: Leitner Box System (simplified)
**Total Boxes**: 7 (fixed, không configurable trong MVP)
**Rating Options**: 4 levels (Again, Hard, Good, Easy)

---

## 1. Box System Configuration

### 1.1 Fixed Intervals

| Box | Interval (days) | Description |
|-----|----------------|-------------|
| Box 1 | 1 day | New cards hoặc forgotten cards |
| Box 2 | 3 days | Early learning |
| Box 3 | 7 days | Transitioning to long-term memory |
| Box 4 | 14 days | Mid-term retention |
| Box 5 | 30 days | Maturing cards |
| Box 6 | 60 days | Mature cards |
| Box 7 | 120 days | Well-known cards |

**Constants**:
```java
public class SrsConstants {
    public static final int TOTAL_BOXES = 7;

    public static final Map<Integer, Integer> BOX_INTERVALS = Map.of(
        1, 1,
        2, 3,
        3, 7,
        4, 14,
        5, 30,
        6, 60,
        7, 120
    );
}
```

### 1.2 Card Lifecycle

```
New Card
   │
   ├─> Box 1 (1 day) ──┐
   │                   │
   │                   ▼
   │            Review Session
   │                   │
   │         ┌─────────┼──────────┐
   │         │         │          │
   │      Again     Hard       Good      Easy
   │         │         │          │        │
   │         ▼         ▼          ▼        ▼
   │    Box 1      Stay      Next Box  Skip +1 Box
   │   (reset)   (retry)    (progress)  (fast track)
   │         │         │          │        │
   │         └─────────┴──────────┴────────┘
   │                   │
   │                   ▼
   │            Box 2 (3 days) ──> ... ──> Box 7 (120 days)
   │                                            │
   │                                            ▼
   │                                    Mature card (long intervals)
   └──────────────────────────────────────────┘
```

---

## 2. Rating System

### 2.1 Rating Definitions

```java
public enum ReviewRating {
    AGAIN,  // Không nhớ, cần học lại
    HARD,   // Nhớ khó khăn
    GOOD,   // Nhớ tốt (default)
    EASY    // Nhớ rất dễ
}
```

### 2.2 Rating Effects

| Rating | Effect | Next Interval | Example |
|--------|--------|---------------|---------|
| **AGAIN** | Apply forgotten card action | Depends on setting | Box 3 → Box 1 (default) |
| **HARD** | Stay in current box, reduce interval | Current interval / 2 | Box 3 (7d) → Box 3 (3-4d) |
| **GOOD** | Move to next box | Next box interval | Box 3 (7d) → Box 4 (14d) |
| **EASY** | Skip 1 box or 4x interval | 4x next interval | Box 3 (7d) → Box 5 (30d) or Box 4 (56d) |

---

## 3. Core Algorithm Implementation

### 3.1 Calculate Next Review

```java
@Service
public class SRSServiceImpl implements ISRSService {

    @Autowired
    private ForgottenCardActionStrategyFactory forgottenActionFactory;

    @Transactional
    public CardBoxPosition calculateNextReview(UUID cardId, UUID userId, ReviewRating rating) {
        CardBoxPosition cardPosition = cardBoxPositionRepository
            .findByCardIdAndUserId(cardId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Card position not found"));

        SrsSettings settings = srsSettingsRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));

        int previousBox = cardPosition.getCurrentBox();
        int newBox;
        int newInterval;
        LocalDate newDueDate;

        switch (rating) {
            case AGAIN -> {
                // Apply forgotten card action strategy
                ForgottenCardActionStrategy strategy = forgottenActionFactory
                    .getStrategy(settings.getForgottenCardAction());

                cardPosition = strategy.handleForgottenCard(cardPosition, settings);
                newBox = cardPosition.getCurrentBox();
                newInterval = cardPosition.getIntervalDays();
                newDueDate = cardPosition.getDueDate();
            }

            case HARD -> {
                // Stay in current box, reduce interval
                newBox = previousBox;
                newInterval = Math.max(1, cardPosition.getIntervalDays() / 2);
                newDueDate = LocalDate.now().plusDays(newInterval);

                cardPosition.setCurrentBox(newBox);
                cardPosition.setIntervalDays(newInterval);
                cardPosition.setDueDate(newDueDate);
            }

            case GOOD -> {
                // Move to next box
                newBox = Math.min(TOTAL_BOXES, previousBox + 1);
                newInterval = SrsConstants.BOX_INTERVALS.get(newBox);
                newDueDate = LocalDate.now().plusDays(newInterval);

                cardPosition.setCurrentBox(newBox);
                cardPosition.setIntervalDays(newInterval);
                cardPosition.setDueDate(newDueDate);
            }

            case EASY -> {
                // Skip 1 box or 4x interval (choose better option)
                int skipBox = Math.min(TOTAL_BOXES, previousBox + 2); // Skip 1 box
                int skipInterval = SrsConstants.BOX_INTERVALS.get(skipBox);

                int multipliedInterval = SrsConstants.BOX_INTERVALS.get(Math.min(TOTAL_BOXES, previousBox + 1)) * 4;

                // Choose longer interval
                if (multipliedInterval > skipInterval) {
                    newBox = Math.min(TOTAL_BOXES, previousBox + 1);
                    newInterval = multipliedInterval;
                } else {
                    newBox = skipBox;
                    newInterval = skipInterval;
                }

                newDueDate = LocalDate.now().plusDays(newInterval);

                cardPosition.setCurrentBox(newBox);
                cardPosition.setIntervalDays(newInterval);
                cardPosition.setDueDate(newDueDate);
            }
        }

        // Update metadata
        cardPosition.setLastReviewedAt(Instant.now());
        cardPosition.setReviewCount(cardPosition.getReviewCount() + 1);

        // Save to database
        return cardBoxPositionRepository.save(cardPosition);
    }
}
```

### 3.2 Forgotten Card Action Strategies

#### Option 1: Move to Box 1 (Default)
```java
@Component
public class MoveToBox1Strategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        cardPosition.setCurrentBox(1);
        cardPosition.setIntervalDays(1);
        cardPosition.setDueDate(LocalDate.now().plusDays(1));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }
}
```

#### Option 2: Move Down N Boxes
```java
@Component
public class MoveDownNBoxesStrategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        int moveDown = settings.getMoveDownBoxes(); // User configurable: 1, 2, or 3
        int newBox = Math.max(1, cardPosition.getCurrentBox() - moveDown);

        cardPosition.setCurrentBox(newBox);
        cardPosition.setIntervalDays(SrsConstants.BOX_INTERVALS.get(newBox));
        cardPosition.setDueDate(LocalDate.now().plusDays(cardPosition.getIntervalDays()));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }
}
```

#### Option 3: Stay in Box
```java
@Component
public class StayInBoxStrategy implements ForgottenCardActionStrategy {
    @Override
    public CardBoxPosition handleForgottenCard(CardBoxPosition cardPosition, SrsSettings settings) {
        // Stay in current box, but reduce interval
        int currentInterval = cardPosition.getIntervalDays();
        int reducedInterval = Math.max(1, currentInterval / 2);

        cardPosition.setIntervalDays(reducedInterval);
        cardPosition.setDueDate(LocalDate.now().plusDays(reducedInterval));
        cardPosition.setLapseCount(cardPosition.getLapseCount() + 1);
        return cardPosition;
    }
}
```

---

## 4. Review Session Flow

### 4.1 Get Due Cards Query

**Most Critical Query for Performance!**

```java
@Repository
public interface CardBoxPositionRepository extends JpaRepository<CardBoxPosition, UUID> {

    @Query("""
        SELECT cbp, c, d
        FROM CardBoxPosition cbp
        JOIN FETCH cbp.card c
        JOIN FETCH c.deck d
        WHERE cbp.userId = :userId
          AND cbp.dueDate <= :currentDate
          AND c.deletedAt IS NULL
          AND d.deletedAt IS NULL
        ORDER BY cbp.dueDate ASC, cbp.currentBox ASC
        """)
    List<CardBoxPosition> findDueCards(
        @Param("userId") UUID userId,
        @Param("currentDate") LocalDate currentDate,
        Pageable pageable
    );

    // Folder scope variant
    @Query("""
        SELECT cbp, c, d
        FROM CardBoxPosition cbp
        JOIN FETCH cbp.card c
        JOIN FETCH c.deck d
        JOIN d.folder f
        WHERE cbp.userId = :userId
          AND cbp.dueDate <= :currentDate
          AND (f.id = :folderId OR f.path LIKE :pathPattern)
          AND c.deletedAt IS NULL
          AND d.deletedAt IS NULL
        ORDER BY cbp.dueDate ASC, cbp.currentBox ASC
        """)
    List<CardBoxPosition> findDueCardsByFolder(
        @Param("userId") UUID userId,
        @Param("currentDate") LocalDate currentDate,
        @Param("folderId") UUID folderId,
        @Param("pathPattern") String pathPattern,
        Pageable pageable
    );
}
```

**Performance Optimization**:
- **Index**: `CREATE INDEX idx_card_box_user_due ON card_box_position(user_id, due_date, current_box);`
- **JOIN FETCH**: Prevent N+1 queries
- **Limit**: Max 200 cards per request (Pageable)
- **Soft delete check**: Filter deleted cards/decks

### 4.2 Review Order Application

```java
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewOrderStrategyFactory reviewOrderStrategyFactory;

    public List<CardResponse> getDueCards(UUID userId, ReviewRequest request) {
        // Get user's review order setting
        SrsSettings settings = srsSettingsRepository.findByUserId(userId).orElseThrow();

        // Fetch due cards from database (pre-sorted by due_date, current_box)
        Pageable pageable = PageRequest.of(0, 200);
        List<CardBoxPosition> dueCards;

        if (request.getScope() == ReviewScope.FOLDER) {
            Folder folder = folderRepository.findById(request.getScopeId()).orElseThrow();
            String pathPattern = folder.getPath() + "/%";
            dueCards = cardBoxPositionRepository.findDueCardsByFolder(
                userId, LocalDate.now(), request.getScopeId(), pathPattern, pageable
            );
        } else if (request.getScope() == ReviewScope.DECK) {
            // Filter by deck
            dueCards = cardBoxPositionRepository.findDueCardsByDeck(
                userId, LocalDate.now(), request.getScopeId(), pageable
            );
        } else {
            // All due cards
            dueCards = cardBoxPositionRepository.findDueCards(userId, LocalDate.now(), pageable);
        }

        // Apply review order strategy (re-sort if needed)
        ReviewOrderStrategy strategy = reviewOrderStrategyFactory.getStrategy(settings.getReviewOrder());
        List<CardBoxPosition> orderedCards = strategy.applyOrder(dueCards);

        // Map to response DTOs
        return orderedCards.stream()
            .map(cardMapper::toReviewResponse)
            .toList();
    }
}
```

### 4.3 Submit Review

```java
@Transactional
public ReviewResponse submitReview(UUID userId, ReviewSubmitRequest request) {
    // Calculate next review using SRS algorithm
    CardBoxPosition updatedPosition = srsService.calculateNextReview(
        request.getCardId(),
        userId,
        request.getRating()
    );

    // Log review for analytics
    ReviewLog log = new ReviewLog();
    log.setCardId(request.getCardId());
    log.setUserId(userId);
    log.setRating(request.getRating());
    log.setPreviousBox(updatedPosition.getCurrentBox() - (request.getRating() == ReviewRating.GOOD ? 1 : 0));
    log.setNewBox(updatedPosition.getCurrentBox());
    log.setIntervalDays(updatedPosition.getIntervalDays());
    log.setReviewedAt(Instant.now());

    reviewLogRepository.save(log);

    // Publish domain event for async updates
    eventPublisher.publishEvent(new CardReviewedEvent(
        request.getCardId(),
        userId,
        updatedPosition.getCard().getDeck().getId(),
        request.getRating(),
        log.getPreviousBox(),
        log.getNewBox(),
        Instant.now()
    ));

    return reviewMapper.toResponse(updatedPosition);
}
```

---

## 5. Daily Limits

### 5.1 New Cards Limit

```java
public List<Card> getNewCards(UUID userId) {
    SrsSettings settings = srsSettingsRepository.findByUserId(userId).orElseThrow();

    int dailyLimit = settings.getNewCardsPerDay(); // Default: 20
    int alreadyReviewed = countNewCardsToday(userId);

    int remaining = Math.max(0, dailyLimit - alreadyReviewed);

    if (remaining == 0) {
        return Collections.emptyList();
    }

    // Fetch new cards (review_count = 0)
    Pageable pageable = PageRequest.of(0, remaining);
    return cardBoxPositionRepository.findNewCards(userId, pageable);
}

private int countNewCardsToday(UUID userId) {
    LocalDate today = LocalDate.now();
    return reviewLogRepository.countNewCardsReviewedToday(userId, today);
}
```

### 5.2 Max Reviews Limit

```java
public boolean canReviewMore(UUID userId) {
    SrsSettings settings = srsSettingsRepository.findByUserId(userId).orElseThrow();

    int maxReviews = settings.getMaxReviewsPerDay(); // Default: 200
    int reviewedToday = reviewLogRepository.countReviewsToday(userId, LocalDate.now());

    return reviewedToday < maxReviews;
}
```

---

## 6. Study Modes

### 6.1 Standard SRS Mode

**Behavior**:
- Fetch cards with `due_date <= today`
- Apply review order (ascending/descending/random)
- Rating affects SRS schedule
- Updates box positions and due dates

**Query**:
```sql
SELECT * FROM card_box_position
WHERE user_id = :userId
  AND due_date <= CURRENT_DATE
ORDER BY due_date ASC, current_box ASC
LIMIT 200;
```

### 6.2 Cram Mode

**Behavior**:
- Fetch ALL cards in deck/folder (ignore due_date)
- Shuffle randomly
- Rating does NOT affect SRS schedule (read-only)
- Good for last-minute review before exam

**Implementation**:
```java
public List<CardResponse> getCramCards(UUID deckId) {
    List<Card> allCards = cardRepository.findByDeckId(deckId);

    // Shuffle randomly
    Collections.shuffle(allCards);

    return allCards.stream()
        .map(cardMapper::toResponse)
        .toList();
}

@Transactional
public void submitCramReview(UUID userId, ReviewSubmitRequest request) {
    // Log review for stats (optional)
    reviewLogRepository.save(createCramLog(request));

    // DO NOT update card_box_position or due_date
    // Cram mode is read-only for SRS schedule
}
```

### 6.3 Random Mode

**Behavior**:
- User selects number of cards to review (e.g., 20)
- Randomly pick from deck/folder
- Rating affects SRS schedule (like standard mode)

**Implementation**:
```java
public List<CardResponse> getRandomCards(UUID deckId, int count) {
    List<CardBoxPosition> allCards = cardBoxPositionRepository.findByDeckId(deckId);

    // Shuffle and take first N cards
    Collections.shuffle(allCards);
    List<CardBoxPosition> selected = allCards.stream()
        .limit(count)
        .toList();

    return selected.stream()
        .map(cardMapper::toReviewResponse)
        .toList();
}
```

---

## 7. Statistics Calculation

### 7.1 Box Distribution

```java
public Map<Integer, Integer> getBoxDistribution(UUID userId) {
    List<Object[]> results = cardBoxPositionRepository
        .countCardsByBox(userId);

    Map<Integer, Integer> distribution = new HashMap<>();
    for (int i = 1; i <= 7; i++) {
        distribution.put(i, 0);
    }

    for (Object[] row : results) {
        Integer box = (Integer) row[0];
        Long count = (Long) row[1];
        distribution.put(box, count.intValue());
    }

    return distribution;
}
```

**SQL Query**:
```sql
SELECT current_box, COUNT(*)
FROM card_box_position
WHERE user_id = :userId
GROUP BY current_box
ORDER BY current_box;
```

### 7.2 Streak Calculation

```java
public int calculateStreak(UUID userId) {
    UserStats stats = userStatsRepository.findByUserId(userId).orElseThrow();

    LocalDate lastStudy = stats.getLastStudyDate();
    LocalDate today = LocalDate.now();

    if (lastStudy == null) {
        return 0;
    }

    // Check if streak is broken
    long daysSinceLastStudy = ChronoUnit.DAYS.between(lastStudy, today);

    if (daysSinceLastStudy > 1) {
        // Streak broken
        return 0;
    } else if (daysSinceLastStudy == 1) {
        // Studied yesterday, increment streak
        return stats.getStreakDays() + 1;
    } else {
        // Studied today already
        return stats.getStreakDays();
    }
}
```

---

## 8. Performance Considerations

### 8.1 Critical Indexes

```sql
-- Most important index for due cards query
CREATE INDEX idx_card_box_user_due ON card_box_position(user_id, due_date, current_box);

-- Index for box distribution query
CREATE INDEX idx_card_box_user_box ON card_box_position(user_id, current_box);

-- Index for review logs (today's stats)
CREATE INDEX idx_review_logs_user_date ON review_logs(user_id, reviewed_at DESC);
```

### 8.2 Query Optimization

**Before Optimization** (N+1 problem):
```java
// BAD: N+1 queries
List<CardBoxPosition> positions = cardBoxPositionRepository.findDueCards(userId);
for (CardBoxPosition pos : positions) {
    Card card = pos.getCard(); // Lazy load → N queries
    Deck deck = card.getDeck(); // Lazy load → N queries
}
```

**After Optimization** (JOIN FETCH):
```java
// GOOD: Single query with JOINs
@Query("""
    SELECT cbp
    FROM CardBoxPosition cbp
    JOIN FETCH cbp.card c
    JOIN FETCH c.deck d
    WHERE cbp.userId = :userId AND cbp.dueDate <= :currentDate
    """)
List<CardBoxPosition> findDueCards(...);
```

### 8.3 Batch Operations

**Import Cards**:
```java
@Transactional
public void importCards(List<Card> cards) {
    // Batch insert: 1000 cards per transaction
    int batchSize = 1000;
    for (int i = 0; i < cards.size(); i += batchSize) {
        int end = Math.min(i + batchSize, cards.size());
        List<Card> batch = cards.subList(i, end);

        cardRepository.saveAll(batch);
        cardRepository.flush(); // Force write to DB

        entityManager.clear(); // Clear persistence context to avoid memory issues
    }
}
```

---

## 9. Algorithm Validation Tests

### 9.1 Test Cases

```java
@Test
public void testGoodRating_MovesToNextBox() {
    CardBoxPosition card = createCardInBox(2); // Box 2, interval 3 days

    CardBoxPosition updated = srsService.calculateNextReview(
        card.getCard().getId(),
        userId,
        ReviewRating.GOOD
    );

    assertEquals(3, updated.getCurrentBox()); // Box 3
    assertEquals(7, updated.getIntervalDays()); // 7 days interval
    assertEquals(LocalDate.now().plusDays(7), updated.getDueDate());
}

@Test
public void testAgainRating_ResetsToBox1() {
    CardBoxPosition card = createCardInBox(5); // Box 5

    // Configure forgotten action: MOVE_TO_BOX_1
    SrsSettings settings = createSettings(ForgottenCardAction.MOVE_TO_BOX_1);

    CardBoxPosition updated = srsService.calculateNextReview(
        card.getCard().getId(),
        userId,
        ReviewRating.AGAIN
    );

    assertEquals(1, updated.getCurrentBox()); // Reset to Box 1
    assertEquals(1, updated.getIntervalDays()); // 1 day interval
    assertEquals(1, updated.getLapseCount()); // Lapse count incremented
}

@Test
public void testEasyRating_SkipsOneBox() {
    CardBoxPosition card = createCardInBox(3); // Box 3, interval 7 days

    CardBoxPosition updated = srsService.calculateNextReview(
        card.getCard().getId(),
        userId,
        ReviewRating.EASY
    );

    assertEquals(5, updated.getCurrentBox()); // Skip to Box 5 (or use 4x interval)
    assertTrue(updated.getIntervalDays() >= 30); // At least 30 days
}
```

---

## Summary

**SRS Algorithm Design**:
- ✅ **7 fixed boxes** với intervals từ 1 → 120 days
- ✅ **4 rating levels** (Again, Hard, Good, Easy)
- ✅ **3 forgotten card strategies** (configurable)
- ✅ **3 study modes** (SRS, Cram, Random)
- ✅ **Daily limits** (new cards, max reviews)
- ✅ **Performance optimized** (indexes, JOIN FETCH, batch operations)

**Key Features**:
- Simple, predictable intervals
- User-configurable behaviors
- Optimized for performance
- Well-tested algorithm

---

**Version**: 1.0
**Last Updated**: 2025-01-10
**Status**: Design Complete, Ready for Implementation
