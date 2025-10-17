# SRS Algorithm Implementation - RepeatWise MVP

## Document Information
- **Version**: 2.0
- **Last Updated**: 2025-01-12
- **Status**: Detail Design - Ready for Implementation

## Related Documents
- Architecture Design: `docs/03-design/architecture/srs-algorithm-design.md`
- Requirements: `repeatwise-mvp-spec.md` section 2.4 SRS

---

## 1. Overview

### 1.1 System Description

RepeatWise implements a **7-box Leitner System** with fixed intervals and configurable user strategies.

**Algorithm Type**: Box-Based Spaced Repetition (Leitner System)
**Core Configuration:**
- **Total Boxes**: 7 (fixed, not configurable)
- **Fixed Intervals**: [1, 3, 7, 14, 30, 60, 120] days
- **Rating Options**: 4 levels (Again, Hard, Good, Easy)
- **Configurable Strategies**: Review order, forgotten card actions
- **Daily Limits**: New cards/day, max reviews/day
- **Study Modes**: Standard SRS, Cram, Random

**Key Principles:**
- Simple, predictable intervals
- User control over strategies
- Performance-optimized queries
- Maintainable and extensible design

### 1.2 Constants and Configuration

```
// Core constants
TOTAL_BOXES = 7

BOX_INTERVALS = {
  1: 1,      // New cards or forgotten cards
  2: 3,      // Early learning
  3: 7,      // Transitioning to long-term memory
  4: 14,     // Mid-term retention
  5: 30,     // Maturing cards
  6: 60,     // Mature cards
  7: 120     // Well-known cards
}

// Ease factor configuration (for future use)
DEFAULT_EASE_FACTOR = 2.5
MIN_EASE_FACTOR = 1.3
MAX_EASE_FACTOR = 2.5

// Rating multipliers
HARD_MULTIPLIER = 0.75        // Reduce interval by 25%
EASY_MULTIPLIER = 4.0         // 4x interval for EASY
AGAIN_STAY_MULTIPLIER = 0.5   // Reduce interval by 50% (STAY_IN_BOX)

// Ease factor adjustments (for future use)
EASE_BONUS_GOOD = 0.1
EASE_BONUS_EASY = 0.2
EASE_PENALTY_HARD = 0.15
EASE_PENALTY_AGAIN = 0.2

// Fuzz range (for natural variation)
FUZZ_MIN = 0.95  // -5% fuzz
FUZZ_MAX = 1.05  // +5% fuzz
```

### 1.3 Rating System

```
ENUM ReviewRating {
  AGAIN,  // Forgot the card, cannot remember
  HARD,   // Remembered with difficulty
  GOOD,   // Remembered well (default expected response)
  EASY    // Remembered very easily
}
```

---

## 2. Core Algorithm

### 2.1 Main Rating Handler

```
FUNCTION handleRating(card, cardBoxPosition, rating, userSettings):
  /**
   * Main entry point for processing a review rating
   *
   * @param card: Card entity
   * @param cardBoxPosition: CardBoxPosition entity (SRS state)
   * @param rating: ReviewRating enum (AGAIN/HARD/GOOD/EASY)
   * @param userSettings: SrsSettings entity (user preferences)
   * @return Updated CardBoxPosition
   */

  // Store old state for logging
  oldBox = cardBoxPosition.currentBox
  oldInterval = cardBoxPosition.intervalDays

  // Process rating based on type
  CASE rating OF
    WHEN AGAIN:
      CALL handleAgainRating(cardBoxPosition, userSettings)

    WHEN HARD:
      CALL handleHardRating(cardBoxPosition)

    WHEN GOOD:
      CALL handleGoodRating(cardBoxPosition)

    WHEN EASY:
      CALL handleEasyRating(cardBoxPosition)
  END CASE

  // Update review metadata
  cardBoxPosition.lastReviewedAt = CURRENT_TIMESTAMP()
  cardBoxPosition.reviewCount = cardBoxPosition.reviewCount + 1

  // Save updated position to database
  cardBoxPositionRepository.save(cardBoxPosition)

  // Create review log entry
  reviewLog = CREATE ReviewLog {
    cardId: card.id,
    userId: userSettings.userId,
    rating: rating,
    previousBox: oldBox,
    newBox: cardBoxPosition.currentBox,
    intervalDays: cardBoxPosition.intervalDays,
    reviewedAt: CURRENT_TIMESTAMP()
  }
  reviewLogRepository.save(reviewLog)

  // Publish event for async stat updates (non-blocking)
  PUBLISH_EVENT CardReviewedEvent(
    cardId: card.id,
    userId: userSettings.userId,
    deckId: card.deckId,
    rating: rating,
    previousBox: oldBox,
    newBox: cardBoxPosition.currentBox,
    timestamp: CURRENT_TIMESTAMP()
  )

  RETURN cardBoxPosition
END FUNCTION
```

### 2.2 Again Rating (Forgotten Card)

When user selects "AGAIN" (forgot the card), apply forgotten card strategy from user settings.

```
FUNCTION handleAgainRating(position, settings):
  /**
   * Handle "AGAIN" rating - card was forgotten
   * Apply user's configured forgotten card action strategy
   *
   * @param position: CardBoxPosition entity to update
   * @param settings: User's SRS settings
   */

  // ===== 1. Increment lapse counter =====
  position.lapseCount = position.lapseCount + 1

  // ===== 2. Apply forgotten card strategy =====
  SWITCH settings.forgottenCardAction:

    // ----- Strategy 1: Move to Box 1 (Default) -----
    CASE "MOVE_TO_BOX_1":
      position.currentBox = 1
      position.intervalDays = 1
      position.easeFactor = DEFAULT_EASE_FACTOR  // Reset ease to 2.5
      position.dueDate = TODAY() + 1 DAY

    // ----- Strategy 2: Move Down N Boxes -----
    CASE "MOVE_DOWN_N_BOXES":
      moveDown = settings.moveDownBoxes  // User configurable: 1, 2, or 3
      newBox = position.currentBox - moveDown
      position.currentBox = MAX(1, newBox)  // Don't go below Box 1

      // Get base interval for new box
      position.intervalDays = BOX_INTERVALS[position.currentBox]

      // Reduce ease factor (penalize forgotten cards)
      position.easeFactor = position.easeFactor - EASE_PENALTY_AGAIN
      position.easeFactor = MAX(MIN_EASE_FACTOR, position.easeFactor)

      position.dueDate = TODAY() + position.intervalDays DAYS

    // ----- Strategy 3: Stay in Box -----
    CASE "STAY_IN_BOX":
      // Don't change box, but reduce interval significantly
      currentInterval = position.intervalDays
      reducedInterval = ROUND(currentInterval * AGAIN_STAY_MULTIPLIER)
      position.intervalDays = MAX(1, reducedInterval)  // At least 1 day

      // Reduce ease factor (penalize forgotten cards)
      position.easeFactor = position.easeFactor - EASE_PENALTY_AGAIN
      position.easeFactor = MAX(MIN_EASE_FACTOR, position.easeFactor)

      position.dueDate = TODAY() + position.intervalDays DAYS

    DEFAULT:
      THROW ValidationException("Invalid forgotten card action")
  END SWITCH
END FUNCTION
```

**Examples:**

```
// Example 1: MOVE_TO_BOX_1 (Default)
Before: Box 5, interval 30 days, ease 2.3
After:  Box 1, interval 1 day, ease 2.5 (reset)

// Example 2: MOVE_DOWN_N_BOXES (moveDown=2)
Before: Box 5, interval 30 days, ease 2.3
After:  Box 3, interval 7 days, ease 2.1

// Example 3: STAY_IN_BOX
Before: Box 5, interval 30 days, ease 2.3
After:  Box 5, interval 15 days, ease 2.1
```

### 2.3 Hard Rating

User remembered the card, but with difficulty.

```
FUNCTION handleHardRating(position):
  /**
   * Handle "HARD" rating - card remembered with difficulty
   * Stay in same box but reduce interval (retry sooner)
   */

  // ===== 1. Stay in current box =====
  // Box remains the same

  // ===== 2. Reduce interval by 25% =====
  currentInterval = position.intervalDays
  reducedInterval = ROUND(currentInterval * HARD_MULTIPLIER)
  position.intervalDays = MAX(1, reducedInterval)  // At least 1 day

  // ===== 3. Slight decrease in ease factor =====
  position.easeFactor = position.easeFactor - EASE_PENALTY_HARD
  position.easeFactor = MAX(MIN_EASE_FACTOR, position.easeFactor)

  // ===== 4. Calculate due date =====
  position.dueDate = TODAY() + position.intervalDays DAYS
END FUNCTION
```

**Examples:**

```
// Example 1: Early box
Before: Box 2, interval 3 days, ease 2.5
After:  Box 2, interval 2 days, ease 2.35

// Example 2: Late box
Before: Box 6, interval 60 days, ease 2.2
After:  Box 6, interval 45 days, ease 2.05
```

### 2.4 Good Rating

User remembered the card well (default case).

```
FUNCTION handleGoodRating(position):
  /**
   * Handle "GOOD" rating - card remembered well
   * Progress to next box (normal advancement)
   */

  // ===== 1. Move to next box (if not at max) =====
  IF position.currentBox < TOTAL_BOXES THEN
    position.currentBox = position.currentBox + 1
  ELSE
    // Already at Box 7, stay there
    position.currentBox = TOTAL_BOXES
  END IF

  // ===== 2. Get base interval for new box =====
  baseInterval = BOX_INTERVALS[position.currentBox]

  // ===== 3. Apply ease factor =====
  calculatedInterval = baseInterval * position.easeFactor
  position.intervalDays = ROUND(calculatedInterval)

  // ===== 4. Slight increase in ease factor =====
  position.easeFactor = position.easeFactor + EASE_BONUS_GOOD
  position.easeFactor = MIN(MAX_EASE_FACTOR, position.easeFactor)

  // ===== 5. Add random fuzz (±5% for natural variation) =====
  fuzzFactor = RANDOM(FUZZ_MIN, FUZZ_MAX)
  position.intervalDays = ROUND(position.intervalDays * fuzzFactor)
  position.intervalDays = MAX(1, position.intervalDays)  // At least 1 day

  // ===== 6. Calculate due date =====
  position.dueDate = TODAY() + position.intervalDays DAYS
END FUNCTION
```

**Examples:**

```
// Example 1: Box progression
Before: Box 3, interval 7 days, ease 2.5
After:  Box 4, interval 14 * 2.5 = 35 days (+fuzz), ease 2.5 (capped)

// Example 2: Already at max box
Before: Box 7, interval 120 days, ease 2.4
After:  Box 7, interval 120 * 2.5 = 300 days (+fuzz), ease 2.5 (capped)

// Example 3: Low ease factor
Before: Box 2, interval 3 days, ease 1.5
After:  Box 3, interval 7 * 1.5 = 10.5 days (+fuzz), ease 1.6
```

**Rationale for fuzz:**
- Adds natural variation to avoid "review clumping"
- Cards reviewed on same day will have slightly different next review dates
- ±5% is small enough to not disrupt learning, but large enough to distribute reviews

### 2.5 Easy Rating

User remembered the card very easily (skip ahead).

```
FUNCTION handleEasyRating(position):
  /**
   * Handle "EASY" rating - card remembered very easily
   * Skip 1 box OR use 4x interval (whichever is longer)
   * Fast-track for well-known cards
   */

  // ===== 1. Skip one box (move +2 instead of +1) =====
  IF position.currentBox < 6 THEN
    position.currentBox = position.currentBox + 2  // Skip 1 box
  ELSE IF position.currentBox == 6 THEN
    position.currentBox = 7  // Move to max box
  ELSE
    position.currentBox = 7  // Already at max, stay there
  END IF

  // ===== 2. Get base interval for new box =====
  baseInterval = BOX_INTERVALS[position.currentBox]

  // ===== 3. Apply ease factor with Easy bonus (4x multiplier) =====
  // Easy cards get much longer intervals
  calculatedInterval = baseInterval * position.easeFactor * EASY_MULTIPLIER
  position.intervalDays = ROUND(calculatedInterval)

  // ===== 4. Increase ease factor significantly =====
  position.easeFactor = position.easeFactor + EASE_BONUS_EASY
  position.easeFactor = MIN(MAX_EASE_FACTOR, position.easeFactor)

  // ===== 5. Add random fuzz (±5%) =====
  fuzzFactor = RANDOM(FUZZ_MIN, FUZZ_MAX)
  position.intervalDays = ROUND(position.intervalDays * fuzzFactor)
  position.intervalDays = MAX(1, position.intervalDays)

  // ===== 6. Calculate due date =====
  position.dueDate = TODAY() + position.intervalDays DAYS
END FUNCTION
```

**Examples:**

```
// Example 1: Skip ahead
Before: Box 3, interval 7 days, ease 2.5
After:  Box 5, interval 30 * 2.5 * 4 = 300 days (+fuzz), ease 2.5 (capped)

// Example 2: Near max box
Before: Box 6, interval 60 days, ease 2.3
After:  Box 7, interval 120 * 2.5 * 4 = 1200 days (+fuzz), ease 2.5 (capped)

// Example 3: Early box
Before: Box 1, interval 1 day, ease 2.5
After:  Box 3, interval 7 * 2.5 * 4 = 70 days (+fuzz), ease 2.5 (capped)
```

**Rationale for 4x multiplier:**
- Easy cards should have much longer intervals than Good
- 4x multiplier balances aggressive progression without being unrealistic
- Prevents "easy spam" by capping ease factor at 2.5

### 2.6 Due Date Calculation

```
FUNCTION calculateDueDate(intervalDays):
  /**
   * Calculate next due date from interval
   * Simple addition - no randomization in MVP
   *
   * @param intervalDays: Number of days until next review
   * @return LocalDate of next review
   */

  today = CURRENT_DATE()
  dueDate = today + intervalDays days

  RETURN dueDate
END FUNCTION
```

---

## 3. Strategy Patterns

### 3.1 Review Order Strategies

User can configure the order in which due cards are presented during review sessions.

#### 3.1.1 Ascending Strategy (Box 1 → 7)

```
FUNCTION applyAscendingOrder(dueCards):
  /**
   * Review cards from Box 1 to Box 7 (hardest first)
   * Prioritizes difficult cards
   */

  // Sort by: current_box ASC, due_date ASC
  sortedCards = SORT dueCards BY (
    card.currentBox ASCENDING,
    card.dueDate ASCENDING
  )

  RETURN sortedCards
END FUNCTION
```

**Use Case:** Users who want to tackle difficult cards first when fresh

#### 3.1.2 Descending Strategy (Box 7 → 1)

```
FUNCTION applyDescendingOrder(dueCards):
  /**
   * Review cards from Box 7 to Box 1 (easiest first)
   * Prioritizes mature cards
   */

  // Sort by: current_box DESC, due_date ASC
  sortedCards = SORT dueCards BY (
    card.currentBox DESCENDING,
    card.dueDate ASCENDING
  )

  RETURN sortedCards
END FUNCTION
```

**Use Case:** Users who want to warm up with easy cards first

#### 3.1.3 Random Strategy

```
FUNCTION applyRandomOrder(dueCards):
  /**
   * Randomize card order
   * Prevents predictability
   */

  // Shuffle using Fisher-Yates algorithm
  shuffledCards = SHUFFLE(dueCards)

  RETURN shuffledCards
END FUNCTION
```

**Use Case:** Default - prevents learning the order

### 3.2 Forgotten Card Action Strategies

See sections 2.2.1, 2.2.2, 2.2.3 for detailed implementations.

**Summary:**
- **Move to Box 1**: Reset completely (default)
- **Move Down N Boxes**: Partial reset (configurable: 1, 2, or 3 boxes)
- **Stay in Box**: Most forgiving, only reduce interval

---

## 4. Study Modes

### 4.1 Spaced Repetition Mode (Standard SRS)

```
FUNCTION getDueCardsForSRS(userId, scope, scopeId, reviewOrder):
  /**
   * Get cards due for review based on SRS schedule
   *
   * @param userId: UUID of the user
   * @param scope: "ALL" | "FOLDER" | "DECK"
   * @param scopeId: UUID of folder/deck (if scope is FOLDER or DECK)
   * @param reviewOrder: User's review order preference
   * @return List of due cards
   */

  currentDate = CURRENT_DATE()
  limit = 200  // Max cards per session

  // Query due cards based on scope
  IF scope == "ALL" THEN
    dueCards = QUERY:
      SELECT cbp, c, d
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      JOIN decks d ON d.id = c.deck_id
      WHERE cbp.user_id = userId
        AND cbp.due_date <= currentDate
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
      ORDER BY cbp.due_date ASC, cbp.current_box ASC
      LIMIT limit

  ELSE IF scope == "FOLDER" THEN
    folder = GET folder WHERE folder.id = scopeId
    pathPattern = folder.path + "/%"

    dueCards = QUERY:
      SELECT cbp, c, d, f
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      JOIN decks d ON d.id = c.deck_id
      JOIN folders f ON f.id = d.folder_id
      WHERE cbp.user_id = userId
        AND cbp.due_date <= currentDate
        AND (f.id = scopeId OR f.path LIKE pathPattern)
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
        AND f.deleted_at IS NULL
      ORDER BY cbp.due_date ASC, cbp.current_box ASC
      LIMIT limit

  ELSE IF scope == "DECK" THEN
    dueCards = QUERY:
      SELECT cbp, c, d
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      JOIN decks d ON d.id = c.deck_id
      WHERE cbp.user_id = userId
        AND cbp.due_date <= currentDate
        AND c.deck_id = scopeId
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
      ORDER BY cbp.due_date ASC, cbp.current_box ASC
      LIMIT limit
  END IF

  // Apply review order strategy
  orderedCards = APPLY_REVIEW_ORDER_STRATEGY(dueCards, reviewOrder)

  RETURN orderedCards
END FUNCTION
```

**Characteristics:**
- Only shows cards with `due_date <= today`
- Applies user's review order preference
- Rating affects SRS schedule (updates box and due date)
- Respects daily limits

### 4.2 Cram Mode

```
FUNCTION getCramCards(userId, scope, scopeId):
  /**
   * Get all cards for cramming (ignores SRS schedule)
   * No effect on SRS state - read-only mode
   *
   * @param userId: UUID of the user
   * @param scope: "FOLDER" | "DECK"
   * @param scopeId: UUID of folder/deck
   * @return List of all cards (shuffled)
   */

  // Fetch ALL cards (ignore due_date)
  IF scope == "DECK" THEN
    allCards = QUERY:
      SELECT c
      FROM cards c
      WHERE c.deck_id = scopeId
        AND c.deleted_at IS NULL

  ELSE IF scope == "FOLDER" THEN
    folder = GET folder WHERE folder.id = scopeId
    pathPattern = folder.path + "/%"

    allCards = QUERY:
      SELECT c, d, f
      FROM cards c
      JOIN decks d ON d.id = c.deck_id
      JOIN folders f ON f.id = d.folder_id
      WHERE (f.id = scopeId OR f.path LIKE pathPattern)
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
        AND f.deleted_at IS NULL
  END IF

  // Shuffle randomly
  shuffledCards = SHUFFLE(allCards)

  RETURN shuffledCards
END FUNCTION

FUNCTION submitCramReview(userId, cardId, rating):
  /**
   * Record cram review (optional logging)
   * DOES NOT update SRS schedule
   */

  // Optional: Log for statistics (not required in MVP)
  reviewLog = CREATE ReviewLog {
    cardId: cardId,
    userId: userId,
    rating: rating,
    previousBox: NULL,
    newBox: NULL,
    intervalDays: NULL,
    reviewedAt: CURRENT_TIMESTAMP(),
    studyMode: "CRAM"  // Tag as cram mode
  }
  reviewLogRepository.save(reviewLog)

  // DO NOT update card_box_position
  // DO NOT update due_date
  // Cram mode is read-only for SRS
END FUNCTION
```

**Characteristics:**
- Ignores `due_date` - shows ALL cards
- Randomly shuffled order
- Rating does NOT affect SRS schedule
- Good for last-minute review before exam

### 4.3 Random Mode

```
FUNCTION getRandomCards(userId, scope, scopeId, count):
  /**
   * Get random cards for practice
   * Rating DOES affect SRS schedule (unlike cram mode)
   *
   * @param count: Number of cards to review (user specified)
   */

  // Get all available cards
  IF scope == "DECK" THEN
    allCards = QUERY:
      SELECT cbp, c
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      WHERE cbp.user_id = userId
        AND c.deck_id = scopeId
        AND c.deleted_at IS NULL

  ELSE IF scope == "FOLDER" THEN
    folder = GET folder WHERE folder.id = scopeId
    pathPattern = folder.path + "/%"

    allCards = QUERY:
      SELECT cbp, c, d, f
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      JOIN decks d ON d.id = c.deck_id
      JOIN folders f ON f.id = d.folder_id
      WHERE cbp.user_id = userId
        AND (f.id = scopeId OR f.path LIKE pathPattern)
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
        AND f.deleted_at IS NULL
  END IF

  // Shuffle and take first N cards
  shuffledCards = SHUFFLE(allCards)
  selectedCards = TAKE_FIRST(shuffledCards, count)

  RETURN selectedCards
END FUNCTION
```

**Characteristics:**
- User selects how many cards to review
- Random selection from available cards
- Rating DOES affect SRS schedule (like standard mode)

---

## 5. Daily Limits

### 5.1 New Cards Limit

```
FUNCTION canAddNewCards(userId):
  /**
   * Check if user can review more new cards today
   * New card = review_count == 0
   *
   * @return boolean
   */

  settings = GET srs_settings WHERE user_id = userId
  dailyLimit = settings.newCardsPerDay  // Default: 20

  today = CURRENT_DATE()

  // Count new cards already reviewed today
  reviewedToday = QUERY:
    SELECT COUNT(DISTINCT rl.card_id)
    FROM review_logs rl
    JOIN card_box_position cbp ON cbp.card_id = rl.card_id
    WHERE rl.user_id = userId
      AND DATE(rl.reviewed_at) = today
      AND cbp.review_count = 1  // First review

  remaining = dailyLimit - reviewedToday

  RETURN remaining > 0
END FUNCTION

FUNCTION getNewCards(userId, limit):
  /**
   * Get new cards (not yet reviewed)
   * Respects daily new cards limit
   */

  settings = GET srs_settings WHERE user_id = userId
  dailyLimit = settings.newCardsPerDay

  // Check how many new cards already reviewed today
  reviewedToday = countNewCardsReviewedToday(userId)
  remaining = MAX(0, dailyLimit - reviewedToday)

  IF remaining == 0 THEN
    RETURN []  // No more new cards today
  END IF

  // Fetch new cards (review_count = 0)
  newCards = QUERY:
    SELECT cbp, c, d
    FROM card_box_position cbp
    JOIN cards c ON c.id = cbp.card_id
    JOIN decks d ON d.id = c.deck_id
    WHERE cbp.user_id = userId
      AND cbp.review_count = 0
      AND c.deleted_at IS NULL
      AND d.deleted_at IS NULL
    ORDER BY cbp.created_at ASC  // FIFO order
    LIMIT MIN(remaining, limit)

  RETURN newCards
END FUNCTION
```

### 5.2 Max Reviews Limit

```
FUNCTION canReviewMore(userId):
  /**
   * Check if user has reached max reviews per day
   *
   * @return boolean
   */

  settings = GET srs_settings WHERE user_id = userId
  maxReviews = settings.maxReviewsPerDay  // Default: 200

  today = CURRENT_DATE()

  // Count total reviews today
  reviewedToday = QUERY:
    SELECT COUNT(*)
    FROM review_logs
    WHERE user_id = userId
      AND DATE(reviewed_at) = today

  RETURN reviewedToday < maxReviews
END FUNCTION

FUNCTION checkDailyLimitBeforeReview(userId):
  /**
   * Validate before starting review session
   * Throw exception if limit reached
   */

  IF NOT canReviewMore(userId) THEN
    THROW DailyLimitExceededException(
      "Daily review limit reached. Come back tomorrow!"
    )
  END IF
END FUNCTION
```

### 5.3 Daily Limit Override

```
FUNCTION overrideDailyLimit(userId):
  /**
   * Allow user to continue reviewing after reaching limit
   * Optional feature for motivated users
   */

  // Set temporary flag in session or user stats
  userSession.limitOverridden = TRUE

  // Log override event for analytics
  LOG "User {userId} overrode daily limit"
END FUNCTION
```

---

## 6. Performance Optimizations

### 6.1 Database Indexes

```sql
-- CRITICAL: Primary index for due cards query
CREATE INDEX idx_card_box_user_due
ON card_box_position(user_id, due_date, current_box);

-- Secondary index for box distribution stats
CREATE INDEX idx_card_box_user_box
ON card_box_position(user_id, current_box);

-- Index for single card lookup
CREATE INDEX idx_card_box_card_user
ON card_box_position(card_id, user_id);

-- Index for review logs (daily stats)
CREATE INDEX idx_review_logs_user_date
ON review_logs(user_id, reviewed_at DESC);

-- Index for card history
CREATE INDEX idx_review_logs_card
ON review_logs(card_id);
```

### 6.2 Query Optimization

#### Batch Fetch with JOIN

```
FUNCTION getDueCardsOptimized(userId):
  /**
   * Optimized query using JOIN FETCH
   * Prevents N+1 query problem
   */

  // Single query with all relationships
  dueCards = QUERY:
    SELECT
      cbp.*,
      c.id, c.front, c.back, c.created_at,
      d.id, d.name,
      f.id, f.name
    FROM card_box_position cbp
    INNER JOIN cards c ON c.id = cbp.card_id
    INNER JOIN decks d ON d.id = c.deck_id
    LEFT JOIN folders f ON f.id = d.folder_id
    WHERE cbp.user_id = userId
      AND cbp.due_date <= CURRENT_DATE()
      AND c.deleted_at IS NULL
      AND d.deleted_at IS NULL
    ORDER BY cbp.due_date ASC, cbp.current_box ASC
    LIMIT 200

  RETURN dueCards
END FUNCTION
```

**Benefits:**
- 1 query instead of 1 + N + N
- Reduces database round-trips
- Faster response time (200ms → 50ms)

### 6.3 Pagination and Limiting

```
FUNCTION getDueCardsPaginated(userId, page, pageSize):
  /**
   * Paginate results to avoid loading too many cards
   *
   * @param page: Page number (0-indexed)
   * @param pageSize: Max 200 cards per page
   */

  maxPageSize = 200
  pageSize = MIN(pageSize, maxPageSize)
  offset = page * pageSize

  dueCards = QUERY:
    SELECT cbp, c, d
    FROM card_box_position cbp
    JOIN cards c ON c.id = cbp.card_id
    JOIN decks d ON d.id = c.deck_id
    WHERE cbp.user_id = userId
      AND cbp.due_date <= CURRENT_DATE()
      AND c.deleted_at IS NULL
      AND d.deleted_at IS NULL
    ORDER BY cbp.due_date ASC, cbp.current_box ASC
    LIMIT pageSize OFFSET offset

  RETURN dueCards
END FUNCTION
```

### 6.4 Prefetch Next Batch

```
FUNCTION prefetchNextBatch(userId, currentBatchEndId):
  /**
   * Asynchronously prefetch next batch while user reviews current batch
   * Improves perceived performance
   */

  // Run in background thread
  ASYNC_EXECUTE:
    nextBatch = QUERY:
      SELECT cbp, c, d
      FROM card_box_position cbp
      JOIN cards c ON c.id = cbp.card_id
      JOIN decks d ON d.id = c.deck_id
      WHERE cbp.user_id = userId
        AND cbp.due_date <= CURRENT_DATE()
        AND cbp.id > currentBatchEndId
        AND c.deleted_at IS NULL
        AND d.deleted_at IS NULL
      ORDER BY cbp.due_date ASC, cbp.current_box ASC
      LIMIT 200

    // Store in cache for quick access
    CACHE_SET("next_batch_" + userId, nextBatch, TTL: 5 minutes)
  END ASYNC_EXECUTE
END FUNCTION
```

### 6.5 Batch Statistics Update

```
FUNCTION updateUserStatsAsync(userId):
  /**
   * Update user statistics asynchronously
   * Don't block review submission
   */

  // Run in background thread
  ASYNC_EXECUTE:
    today = CURRENT_DATE()

    // Count today's reviews
    reviewsToday = QUERY:
      SELECT COUNT(*)
      FROM review_logs
      WHERE user_id = userId
        AND DATE(reviewed_at) = today

    // Get last study date
    lastReview = QUERY:
      SELECT MAX(reviewed_at)
      FROM review_logs
      WHERE user_id = userId

    lastStudyDate = DATE(lastReview)

    // Calculate streak
    streakDays = calculateStreak(userId, lastStudyDate)

    // Update user stats table
    UPDATE user_stats
    SET
      total_cards_learned = (
        SELECT COUNT(DISTINCT card_id)
        FROM review_logs
        WHERE user_id = userId
      ),
      streak_days = streakDays,
      last_study_date = lastStudyDate,
      updated_at = CURRENT_TIMESTAMP()
    WHERE user_id = userId
  END ASYNC_EXECUTE
END FUNCTION
```

---

## 7. Statistics Calculation

### 7.1 Box Distribution

```
FUNCTION getBoxDistribution(userId):
  /**
   * Calculate how many cards in each box
   * Used for progress visualization
   *
   * @return Map<Integer, Integer> - box number to card count
   */

  results = QUERY:
    SELECT current_box, COUNT(*) as card_count
    FROM card_box_position cbp
    JOIN cards c ON c.id = cbp.card_id
    WHERE cbp.user_id = userId
      AND c.deleted_at IS NULL
    GROUP BY current_box
    ORDER BY current_box

  // Initialize all boxes with 0
  distribution = {}
  FOR box = 1 TO 7 DO
    distribution[box] = 0
  END FOR

  // Fill in actual counts
  FOR row IN results DO
    distribution[row.current_box] = row.card_count
  END FOR

  RETURN distribution
END FUNCTION
```

### 7.2 Streak Calculation

```
FUNCTION calculateStreak(userId, lastStudyDate):
  /**
   * Calculate current study streak
   *
   * @param lastStudyDate: Date of last review
   * @return Current streak in days
   */

  IF lastStudyDate IS NULL THEN
    RETURN 0
  END IF

  today = CURRENT_DATE()
  daysSinceLastStudy = DAYS_BETWEEN(lastStudyDate, today)

  // Get current streak from database
  userStats = GET user_stats WHERE user_id = userId
  currentStreak = userStats.streakDays

  IF daysSinceLastStudy > 1 THEN
    // Streak broken (missed a day)
    RETURN 0

  ELSE IF daysSinceLastStudy == 1 THEN
    // Studied yesterday, increment streak
    RETURN currentStreak + 1

  ELSE // daysSinceLastStudy == 0
    // Already studied today, keep current streak
    RETURN currentStreak
  END IF
END FUNCTION
```

### 7.3 Folder Statistics (Recursive)

```
FUNCTION getFolderStats(folderId, userId):
  /**
   * Calculate statistics for a folder (including sub-folders)
   * Uses recursive query or materialized path
   *
   * @return FolderStats object
   */

  // Get folder and its path
  folder = GET folders WHERE id = folderId
  pathPattern = folder.path + "/%"

  // Recursive query to get all cards in folder tree
  stats = QUERY:
    SELECT
      COUNT(DISTINCT c.id) as total_cards,
      COUNT(DISTINCT CASE WHEN cbp.due_date <= CURRENT_DATE() THEN c.id END) as due_cards,
      COUNT(DISTINCT CASE WHEN cbp.review_count = 0 THEN c.id END) as new_cards,
      COUNT(DISTINCT CASE WHEN cbp.current_box >= 6 THEN c.id END) as mature_cards
    FROM folders f
    INNER JOIN decks d ON d.folder_id = f.id
    INNER JOIN cards c ON c.deck_id = d.id
    INNER JOIN card_box_position cbp ON cbp.card_id = c.id
    WHERE (f.id = folderId OR f.path LIKE pathPattern)
      AND f.user_id = userId
      AND cbp.user_id = userId
      AND f.deleted_at IS NULL
      AND d.deleted_at IS NULL
      AND c.deleted_at IS NULL

  RETURN {
    totalCards: stats.total_cards,
    dueCards: stats.due_cards,
    newCards: stats.new_cards,
    matureCards: stats.mature_cards
  }
END FUNCTION
```

### 7.4 Accuracy Rate

```
FUNCTION calculateAccuracyRate(userId, deckId, timeRange):
  /**
   * Calculate review accuracy rate
   * Accuracy = (GOOD + EASY) / Total Reviews
   *
   * @param timeRange: "LAST_7_DAYS" | "LAST_30_DAYS" | "ALL_TIME"
   * @return Percentage (0-100)
   */

  startDate = CALCULATE_START_DATE(timeRange)

  results = QUERY:
    SELECT
      COUNT(CASE WHEN rating IN ('GOOD', 'EASY') THEN 1 END) as correct,
      COUNT(*) as total
    FROM review_logs rl
    JOIN cards c ON c.id = rl.card_id
    WHERE rl.user_id = userId
      AND (deckId IS NULL OR c.deck_id = deckId)
      AND rl.reviewed_at >= startDate

  IF results.total == 0 THEN
    RETURN 0
  END IF

  accuracy = (results.correct / results.total) * 100
  RETURN ROUND(accuracy, 2)
END FUNCTION
```

---

## 8. Edge Cases and Validation

### 8.1 New Card Initialization

```
FUNCTION initializeNewCard(cardId, userId):
  /**
   * Initialize SRS state for a newly created card
   * Called when card is first created
   */

  cardBoxPosition = CREATE CardBoxPosition {
    cardId: cardId,
    userId: userId,
    currentBox: 1,
    intervalDays: 1,
    dueDate: CURRENT_DATE() + 1 day,
    easeFactor: 2.5,  // Default ease factor (not used in MVP)
    reviewCount: 0,
    lapseCount: 0,
    lastReviewedAt: NULL,
    createdAt: CURRENT_TIMESTAMP(),
    updatedAt: CURRENT_TIMESTAMP()
  }

  cardBoxPositionRepository.save(cardBoxPosition)
END FUNCTION
```

### 8.2 Box 7 Edge Case

```
FUNCTION handleBox7Advancement(cardBoxPosition):
  /**
   * Handle progression from Box 7 (highest box)
   * Stay in Box 7 but increase interval
   */

  IF cardBoxPosition.currentBox == 7 THEN
    currentInterval = cardBoxPosition.intervalDays

    // Increase interval by 50% (cap at 180 days)
    newInterval = MIN(180, FLOOR(currentInterval * 1.5))

    cardBoxPosition.intervalDays = newInterval
    cardBoxPosition.dueDate = CURRENT_DATE() + newInterval days
    // currentBox stays at 7
  END IF
END FUNCTION
```

### 8.3 Zero Due Cards

```
FUNCTION handleNoDueCards(userId):
  /**
   * Handle case when no cards are due
   * Show appropriate message to user
   */

  dueCount = COUNT_DUE_CARDS(userId)

  IF dueCount == 0 THEN
    // Check if there are new cards available
    newCardsAvailable = canAddNewCards(userId)

    IF newCardsAvailable THEN
      RETURN {
        message: "No due cards. Start learning new cards?",
        action: "SHOW_NEW_CARDS"
      }
    ELSE
      RETURN {
        message: "All done for today! Come back tomorrow.",
        action: "SHOW_STATS"
      }
    END IF
  END IF
END FUNCTION
```

### 8.4 Concurrent Review Prevention

```
FUNCTION preventConcurrentReview(cardId, userId):
  /**
   * Prevent same card being reviewed in multiple sessions
   * Use optimistic locking
   */

  // Check if card was updated recently (within last 5 seconds)
  cardBoxPosition = GET card_box_position
    WHERE card_id = cardId AND user_id = userId

  timeSinceUpdate = CURRENT_TIMESTAMP() - cardBoxPosition.updatedAt

  IF timeSinceUpdate < 5 seconds THEN
    THROW ConcurrentReviewException(
      "This card was just reviewed. Please refresh your session."
    )
  END IF
END FUNCTION
```

---

## 9. Complete Review Session Flow

```
FUNCTION startReviewSession(userId, scope, scopeId, mode):
  /**
   * Complete flow for starting a review session
   *
   * @param mode: "SRS" | "CRAM" | "RANDOM"
   * @return ReviewSession object
   */

  // 1. Validate user can review
  IF mode == "SRS" THEN
    checkDailyLimitBeforeReview(userId)
  END IF

  // 2. Get user settings
  settings = GET srs_settings WHERE user_id = userId

  // 3. Fetch cards based on mode
  IF mode == "SRS" THEN
    cards = getDueCardsForSRS(userId, scope, scopeId, settings.reviewOrder)
  ELSE IF mode == "CRAM" THEN
    cards = getCramCards(userId, scope, scopeId)
  ELSE IF mode == "RANDOM" THEN
    cards = getRandomCards(userId, scope, scopeId, count: 20)
  END IF

  // 4. Check if cards available
  IF cards IS EMPTY THEN
    RETURN handleNoDueCards(userId)
  END IF

  // 5. Create session
  session = CREATE ReviewSession {
    sessionId: GENERATE_UUID(),
    userId: userId,
    mode: mode,
    cards: cards,
    currentIndex: 0,
    totalCards: LENGTH(cards),
    startedAt: CURRENT_TIMESTAMP(),
    reviewedCards: []
  }

  // 6. Prefetch next batch (async)
  IF mode == "SRS" AND LENGTH(cards) == 200 THEN
    lastCardId = cards[199].id
    prefetchNextBatch(userId, lastCardId)
  END IF

  RETURN session
END FUNCTION

FUNCTION submitReview(sessionId, cardId, rating):
  /**
   * Submit a review for a card
   */

  // 1. Get session and validate
  session = GET_SESSION(sessionId)
  IF session IS NULL THEN
    THROW SessionNotFoundException()
  END IF

  // 2. Get card and position
  card = GET cards WHERE id = cardId
  cardBoxPosition = GET card_box_position
    WHERE card_id = cardId AND user_id = session.userId

  // 3. Prevent concurrent review
  preventConcurrentReview(cardId, session.userId)

  // 4. Get user settings
  settings = GET srs_settings WHERE user_id = session.userId

  // 5. Process rating (main algorithm)
  IF session.mode == "SRS" OR session.mode == "RANDOM" THEN
    updatedPosition = handleRating(card, cardBoxPosition, rating, settings)
  ELSE IF session.mode == "CRAM" THEN
    submitCramReview(session.userId, cardId, rating)
  END IF

  // 6. Update session
  session.reviewedCards.push({
    cardId: cardId,
    rating: rating,
    timestamp: CURRENT_TIMESTAMP()
  })
  session.currentIndex = session.currentIndex + 1

  // 7. Check if session complete
  IF session.currentIndex >= session.totalCards THEN
    session.completedAt = CURRENT_TIMESTAMP()

    // Update stats (async)
    updateUserStatsAsync(session.userId)
  END IF

  RETURN {
    success: TRUE,
    nextCard: GET_NEXT_CARD(session),
    progress: {
      current: session.currentIndex,
      total: session.totalCards
    }
  }
END FUNCTION
```

---

## 10. Testing and Validation

### 10.1 Algorithm Test Cases

```
TEST testGoodRating_MovesToNextBox():
  // Setup
  userId = "user-123"
  card = CREATE_CARD()
  position = CREATE_POSITION(card, currentBox: 2, interval: 3)
  settings = CREATE_SETTINGS(userId)

  // Execute
  result = handleGoodRating(position)

  // Assert
  ASSERT result.currentBox == 3
  ASSERT result.intervalDays == 7
  ASSERT result.dueDate == CURRENT_DATE() + 7 days
END TEST

TEST testAgainRating_MoveToBox1():
  // Setup
  userId = "user-123"
  card = CREATE_CARD()
  position = CREATE_POSITION(card, currentBox: 5, interval: 30)
  settings = CREATE_SETTINGS(userId, forgottenAction: "MOVE_TO_BOX_1")

  // Execute
  result = handleRating(card, position, AGAIN, settings)

  // Assert
  ASSERT result.currentBox == 1
  ASSERT result.intervalDays == 1
  ASSERT result.lapseCount == 1
  ASSERT result.dueDate == CURRENT_DATE() + 1 day
END TEST

TEST testEasyRating_SkipsBox():
  // Setup
  position = CREATE_POSITION(card, currentBox: 3, interval: 7)

  // Execute
  result = handleEasyRating(position)

  // Assert
  // Should skip from Box 3 to Box 5
  // OR use 4x interval (14 * 4 = 56 days)
  ASSERT result.currentBox == 5
  ASSERT result.intervalDays == 30
  ASSERT result.dueDate == CURRENT_DATE() + 30 days
END TEST

TEST testHardRating_StaysInBox():
  // Setup
  position = CREATE_POSITION(card, currentBox: 4, interval: 14)

  // Execute
  result = handleHardRating(position)

  // Assert
  ASSERT result.currentBox == 4  // No change
  ASSERT result.intervalDays == 7  // 14 / 2
  ASSERT result.dueDate == CURRENT_DATE() + 7 days
END TEST

TEST testBox7_StaysInBox7():
  // Setup
  position = CREATE_POSITION(card, currentBox: 7, interval: 120)

  // Execute
  result = handleGoodRating(position)

  // Assert
  ASSERT result.currentBox == 7  // Cannot exceed Box 7
  ASSERT result.intervalDays == 120
END TEST
```

### 10.2 Daily Limits Test Cases

```
TEST testNewCardsLimit_Respected():
  // Setup
  userId = "user-123"
  settings = CREATE_SETTINGS(userId, newCardsPerDay: 20)

  // Review 20 new cards
  FOR i = 1 TO 20 DO
    card = CREATE_NEW_CARD()
    submitReview(card, GOOD)
  END FOR

  // Try to get more new cards
  moreCards = getNewCards(userId, limit: 10)

  // Assert
  ASSERT LENGTH(moreCards) == 0
END TEST

TEST testMaxReviewsLimit_Respected():
  // Setup
  userId = "user-123"
  settings = CREATE_SETTINGS(userId, maxReviewsPerDay: 200)

  // Review 200 cards
  FOR i = 1 TO 200 DO
    card = CREATE_CARD()
    submitReview(card, GOOD)
  END FOR

  // Try to start new session
  canReview = canReviewMore(userId)

  // Assert
  ASSERT canReview == FALSE
END TEST
```

### 10.3 Performance Test Cases

```
TEST testDueCardsQuery_Performance():
  // Setup: 10,000 cards with various due dates
  userId = "user-123"
  CREATE_TEST_DATA(userId, cardCount: 10000)

  // Execute
  startTime = CURRENT_TIME()
  dueCards = getDueCardsForSRS(userId, "ALL", NULL, "RANDOM")
  endTime = CURRENT_TIME()

  // Assert
  executionTime = endTime - startTime
  ASSERT executionTime < 200 milliseconds
  ASSERT LENGTH(dueCards) <= 200  // Limit respected
END TEST

TEST testBoxDistribution_Performance():
  // Setup: 10,000 cards
  userId = "user-123"
  CREATE_TEST_DATA(userId, cardCount: 10000)

  // Execute
  startTime = CURRENT_TIME()
  distribution = getBoxDistribution(userId)
  endTime = CURRENT_TIME()

  // Assert
  executionTime = endTime - startTime
  ASSERT executionTime < 100 milliseconds
  ASSERT LENGTH(distribution) == 7
END TEST
```

---

## 11. Summary

### 11.1 Key Components

**Core Algorithm:**
- ✅ 7-box system with fixed intervals
- ✅ 4 rating levels (AGAIN, HARD, GOOD, EASY)
- ✅ Configurable forgotten card strategies
- ✅ Predictable progression rules

**Strategies:**
- ✅ 3 review order options (Ascending, Descending, Random)
- ✅ 3 forgotten card actions (Move to Box 1, Move Down N, Stay in Box)

**Study Modes:**
- ✅ Standard SRS (affects schedule)
- ✅ Cram mode (read-only)
- ✅ Random mode (affects schedule)

**Performance:**
- ✅ Optimized database indexes
- ✅ Batch fetching with JOIN
- ✅ Query limiting (max 200 cards)
- ✅ Async stats updates

### 11.2 Implementation Checklist

Backend:
- [ ] Implement core algorithm functions (section 2)
- [ ] Implement strategy patterns (section 3)
- [ ] Implement study modes (section 4)
- [ ] Implement daily limits (section 5)
- [ ] Create database indexes (section 6.1)
- [ ] Optimize queries (section 6.2-6.5)
- [ ] Implement statistics (section 7)
- [ ] Handle edge cases (section 8)
- [ ] Write unit tests (section 10)

Frontend:
- [ ] Review session UI
- [ ] Rating buttons (4 options)
- [ ] Progress tracking
- [ ] Statistics dashboard
- [ ] Settings page (review order, forgotten action, daily limits)

Database:
- [ ] Create indexes for performance
- [ ] Set up audit logging
- [ ] Configure backup strategy

### 11.3 Performance Targets

- Due cards query: < 200ms
- Review submission: < 100ms
- Statistics calculation: < 300ms
- Support 10,000+ cards per user
- Support 200+ reviews per session

---

**Document Complete**
**Ready for Implementation**
