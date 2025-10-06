# Review Session Management Module - Detail Design

## 1. Module Overview

### 1.1 Objectives
Review Session Management Module xử lý tất cả các hoạt động liên quan đến chu kỳ học tập và ôn tập bao gồm:
- Bắt đầu chu kỳ học tập mới
- Thực hiện các phiên ôn tập
- Quản lý lịch trình ôn tập theo thuật toán SRS
- Hoàn thành và bỏ qua phiên ôn tập
- Tạm dừng và tiếp tục học tập

### 1.2 Scope
- **In Scope**: Review sessions, SRS algorithm, learning cycles, progress tracking
- **Out of Scope**: Learning set management (handled by Learning Set Module), Statistics (handled by Statistics Module)

### 1.3 Dependencies
- **Database**: review_sessions table, learning_cycles table, review_items table
- **External Services**: Notification service (for reminders)
- **Algorithms**: Spaced Repetition System (SRS) algorithm

## 2. API Contracts

### 2.1 Learning Cycle Management

#### POST /api/v1/learning-cycles
**Request:**
```json
{
  "setId": "uuid-here",
  "preferredSessionDuration": 30,
  "dailyGoal": 20
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "message": "Chu kỳ học tập đã được bắt đầu",
  "data": {
    "cycleId": "uuid-here",
    "setId": "uuid-here",
    "status": "active",
    "currentCycle": 1,
    "totalCycles": 5,
    "preferredSessionDuration": 30,
    "dailyGoal": 20,
    "startedAt": "2024-12-19T10:00:00Z",
    "nextReviewAt": "2024-12-19T11:00:00Z"
  }
}
```

#### GET /api/v1/learning-cycles/active
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "cycleId": "uuid-here",
      "setId": "uuid-here",
      "setTitle": "Từ vựng tiếng Anh cơ bản",
      "status": "active",
      "currentCycle": 1,
      "totalCycles": 5,
      "progress": {
        "completedSessions": 0,
        "totalSessions": 5,
        "completionPercentage": 0
      },
      "nextReviewAt": "2024-12-19T11:00:00Z",
      "startedAt": "2024-12-19T10:00:00Z"
    }
  ]
}
```

### 2.2 Review Session Management

#### POST /api/v1/review-sessions
**Request:**
```json
{
  "cycleId": "uuid-here",
  "sessionType": "scheduled"
}
```

**Response (Success - 201):**
```json
{
  "success": true,
  "data": {
    "sessionId": "uuid-here",
    "cycleId": "uuid-here",
    "sessionType": "scheduled",
    "status": "in_progress",
    "items": [
      {
        "itemId": "uuid-here",
        "front": "Hello",
        "back": "Xin chào",
        "type": "text",
        "difficulty": "easy",
        "previousScore": null,
        "reviewCount": 0
      }
    ],
    "currentItemIndex": 0,
    "totalItems": 1,
    "startedAt": "2024-12-19T10:00:00Z"
  }
}
```

#### GET /api/v1/review-sessions/{sessionId}
**Response:**
```json
{
  "success": true,
  "data": {
    "sessionId": "uuid-here",
    "cycleId": "uuid-here",
    "sessionType": "scheduled",
    "status": "in_progress",
    "currentItem": {
      "itemId": "uuid-here",
      "front": "Hello",
      "back": "Xin chào",
      "type": "text",
      "difficulty": "easy",
      "previousScore": null,
      "reviewCount": 0
    },
    "progress": {
      "currentItemIndex": 0,
      "totalItems": 1,
      "completedItems": 0,
      "correctAnswers": 0,
      "incorrectAnswers": 0
    },
    "startedAt": "2024-12-19T10:00:00Z",
    "estimatedCompletionAt": "2024-12-19T10:05:00Z"
  }
}
```

#### POST /api/v1/review-sessions/{sessionId}/answer
**Request:**
```json
{
  "itemId": "uuid-here",
  "userAnswer": "Xin chào",
  "responseTime": 3.5,
  "difficulty": "easy"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "isCorrect": true,
    "score": 1.0,
    "feedback": "Chính xác!",
    "nextItem": {
      "itemId": "uuid-here-2",
      "front": "Goodbye",
      "back": "Tạm biệt",
      "type": "text",
      "difficulty": "easy",
      "previousScore": null,
      "reviewCount": 0
    },
    "sessionProgress": {
      "currentItemIndex": 1,
      "totalItems": 2,
      "completedItems": 1,
      "correctAnswers": 1,
      "incorrectAnswers": 0
    }
  }
}
```

#### POST /api/v1/review-sessions/{sessionId}/complete
**Request:**
```json
{
  "completedAt": "2024-12-19T10:05:00Z"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Phiên ôn tập đã hoàn thành",
  "data": {
    "sessionId": "uuid-here",
    "summary": {
      "totalItems": 2,
      "correctAnswers": 2,
      "incorrectAnswers": 0,
      "accuracy": 100.0,
      "averageResponseTime": 3.2,
      "sessionDuration": 300
    },
    "nextReviewAt": "2024-12-20T10:00:00Z",
    "cycleProgress": {
      "currentCycle": 1,
      "totalCycles": 5,
      "completedSessions": 1,
      "completionPercentage": 20
    }
  }
}
```

#### POST /api/v1/review-sessions/{sessionId}/skip
**Request:**
```json
{
  "reason": "not_ready",
  "skippedAt": "2024-12-19T10:02:00Z"
}
```

### 2.3 Learning Cycle Control

#### PUT /api/v1/learning-cycles/{cycleId}/pause
**Request:**
```json
{
  "reason": "busy_schedule"
}
```

#### PUT /api/v1/learning-cycles/{cycleId}/resume
**Request:**
```json
{
  "resumeAt": "2024-12-20T09:00:00Z"
}
```

#### PUT /api/v1/learning-cycles/{cycleId}/reschedule
**Request:**
```json
{
  "newSchedule": {
    "preferredTime": "08:00",
    "timezone": "Asia/Ho_Chi_Minh",
    "frequency": "daily"
  }
}
```

## 3. Data Models

### 3.1 Learning Cycle Entity
```java
@Entity
@Table(name = "learning_cycles")
public class LearningCycle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID cycleId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private LearningSet learningSet;
    
    @Enumerated(EnumType.STRING)
    private CycleStatus status;
    
    @Column(nullable = false)
    private Integer currentCycle;
    
    @Column(nullable = false)
    private Integer totalCycles;
    
    @Column(nullable = false)
    private Integer preferredSessionDuration;
    
    @Column(nullable = false)
    private Integer dailyGoal;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime pausedAt;
    
    private LocalDateTime resumedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime nextReviewAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.2 Review Session Entity
```java
@Entity
@Table(name = "review_sessions")
public class ReviewSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sessionId;
    
    @ManyToOne
    @JoinColumn(name = "cycle_id", nullable = false)
    private LearningCycle learningCycle;
    
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;
    
    @Enumerated(EnumType.STRING)
    private SessionStatus status;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime skippedAt;
    
    private String skipReason;
    
    private Integer totalItems;
    
    private Integer correctAnswers;
    
    private Integer incorrectAnswers;
    
    private Double accuracy;
    
    private Double averageResponseTime;
    
    private Integer sessionDuration;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 3.3 Review Item Entity
```java
@Entity
@Table(name = "review_items")
public class ReviewItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewItemId;
    
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private ReviewSession reviewSession;
    
    @ManyToOne
    @JoinColumn(name = "set_item_id", nullable = false)
    private SetItem setItem;
    
    @Column(nullable = false)
    private Integer order;
    
    private String userAnswer;
    
    private Double responseTime;
    
    private Double score;
    
    private Boolean isCorrect;
    
    @Enumerated(EnumType.STRING)
    private ItemDifficulty difficulty;
    
    private Integer reviewCount;
    
    private LocalDateTime reviewedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
```

### 3.4 Item Progress Entity
```java
@Entity
@Table(name = "item_progress")
public class ItemProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID progressId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "set_item_id", nullable = false)
    private SetItem setItem;
    
    @Column(nullable = false)
    private Integer reviewCount;
    
    @Column(nullable = false)
    private Double averageScore;
    
    @Enumerated(EnumType.STRING)
    private ItemStatus status;
    
    private LocalDateTime lastReviewedAt;
    
    private LocalDateTime nextReviewAt;
    
    private Integer intervalDays;
    
    private Double easeFactor;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

## 4. Business Logic

### 4.1 Start Learning Cycle Logic
```pseudocode
FUNCTION startLearningCycle(userId, startRequest):
    // Validate input
    IF NOT validateStartCycleRequest(startRequest):
        RETURN validationError
    
    // Check if user already has active cycle for this set
    existingCycle = learningCycleRepository.findActiveByUserIdAndSetId(userId, startRequest.setId)
    IF existingCycle IS NOT NULL:
        RETURN cycleAlreadyActiveError
    
    // Get learning set
    learningSet = learningSetRepository.findByIdAndOwnerId(startRequest.setId, userId)
    IF learningSet IS NULL:
        RETURN setNotFoundError
    
    // Create learning cycle
    learningCycle = new LearningCycle()
    learningCycle.user = userRepository.findById(userId)
    learningCycle.learningSet = learningSet
    learningCycle.status = ACTIVE
    learningCycle.currentCycle = 1
    learningCycle.totalCycles = 5
    learningCycle.preferredSessionDuration = startRequest.preferredSessionDuration
    learningCycle.dailyGoal = startRequest.dailyGoal
    learningCycle.startedAt = now()
    learningCycle.nextReviewAt = calculateNextReviewTime(startRequest.preferredSessionDuration)
    learningCycle.createdAt = now()
    learningCycle.updatedAt = now()
    
    // Save learning cycle
    savedCycle = learningCycleRepository.save(learningCycle)
    
    // Initialize item progress for all items in set
    FOR EACH item IN learningSet.items:
        itemProgress = new ItemProgress()
        itemProgress.user = learningCycle.user
        itemProgress.setItem = item
        itemProgress.reviewCount = 0
        itemProgress.averageScore = 0.0
        itemProgress.status = NEW
        itemProgress.intervalDays = 1
        itemProgress.easeFactor = 2.5
        itemProgress.createdAt = now()
        itemProgress.updatedAt = now()
        
        itemProgressRepository.save(itemProgress)
    
    RETURN successResponse(mapToCycleDto(savedCycle))
```

### 4.2 Create Review Session Logic
```pseudocode
FUNCTION createReviewSession(userId, createRequest):
    // Find active learning cycle
    learningCycle = learningCycleRepository.findByIdAndUserId(createRequest.cycleId, userId)
    IF learningCycle IS NULL OR learningCycle.status != ACTIVE:
        RETURN cycleNotFoundError
    
    // Check if there's already an active session
    activeSession = reviewSessionRepository.findActiveByCycleId(createRequest.cycleId)
    IF activeSession IS NOT NULL:
        RETURN sessionAlreadyActiveError
    
    // Get items for review based on SRS algorithm
    reviewItems = getItemsForReview(learningCycle.user, learningCycle.learningSet)
    IF reviewItems.isEmpty():
        RETURN noItemsForReviewError
    
    // Create review session
    reviewSession = new ReviewSession()
    reviewSession.learningCycle = learningCycle
    reviewSession.sessionType = createRequest.sessionType
    reviewSession.status = IN_PROGRESS
    reviewSession.startedAt = now()
    reviewSession.totalItems = reviewItems.size()
    reviewSession.correctAnswers = 0
    reviewSession.incorrectAnswers = 0
    reviewSession.createdAt = now()
    reviewSession.updatedAt = now()
    
    // Save review session
    savedSession = reviewSessionRepository.save(reviewSession)
    
    // Create review items
    FOR EACH item IN reviewItems:
        reviewItem = new ReviewItem()
        reviewItem.reviewSession = savedSession
        reviewItem.setItem = item.setItem
        reviewItem.order = item.order
        reviewItem.reviewCount = item.reviewCount
        reviewItem.difficulty = item.difficulty
        reviewItem.createdAt = now()
        
        reviewItemRepository.save(reviewItem)
    
    RETURN successResponse(mapToSessionDto(savedSession, reviewItems))
```

### 4.3 Process Answer Logic
```pseudocode
FUNCTION processAnswer(userId, sessionId, answerRequest):
    // Find active review session
    reviewSession = reviewSessionRepository.findByIdAndUserId(sessionId, userId)
    IF reviewSession IS NULL OR reviewSession.status != IN_PROGRESS:
        RETURN sessionNotFoundError
    
    // Find review item
    reviewItem = reviewItemRepository.findBySessionIdAndItemId(sessionId, answerRequest.itemId)
    IF reviewItem IS NULL:
        RETURN itemNotFoundError
    
    // Calculate score based on answer
    score = calculateScore(answerRequest.userAnswer, reviewItem.setItem.back, answerRequest.difficulty)
    isCorrect = score >= 0.5
    
    // Update review item
    reviewItem.userAnswer = answerRequest.userAnswer
    reviewItem.responseTime = answerRequest.responseTime
    reviewItem.score = score
    reviewItem.isCorrect = isCorrect
    reviewItem.reviewedAt = now()
    
    reviewItemRepository.save(reviewItem)
    
    // Update session statistics
    IF isCorrect:
        reviewSession.correctAnswers++
    ELSE:
        reviewSession.incorrectAnswers++
    
    reviewSessionRepository.save(reviewSession)
    
    // Update item progress using SRS algorithm
    updateItemProgress(reviewItem.setItem, reviewSession.learningCycle.user, score, answerRequest.difficulty)
    
    // Get next item
    nextItem = getNextReviewItem(sessionId, reviewItem.order)
    
    RETURN successResponse({
        isCorrect: isCorrect,
        score: score,
        feedback: generateFeedback(isCorrect, score),
        nextItem: nextItem,
        sessionProgress: calculateSessionProgress(reviewSession)
    })
```

### 4.4 SRS Algorithm Implementation
```pseudocode
FUNCTION updateItemProgress(setItem, user, score, difficulty):
    // Get current progress
    itemProgress = itemProgressRepository.findByUserIdAndSetItemId(user.getUserId(), setItem.getItemId())
    
    // Update review count
    itemProgress.reviewCount++
    
    // Calculate new average score
    itemProgress.averageScore = (itemProgress.averageScore * (itemProgress.reviewCount - 1) + score) / itemProgress.reviewCount
    
    // Update status based on performance
    IF itemProgress.averageScore >= 0.8 AND itemProgress.reviewCount >= 3:
        itemProgress.status = MASTERED
    ELSE IF itemProgress.averageScore >= 0.6:
        itemProgress.status = LEARNING
    ELSE:
        itemProgress.status = NEW
    
    // Calculate next review interval using SM-2 algorithm
    IF score >= 0.5:
        // Correct answer - increase interval
        IF itemProgress.reviewCount == 1:
            itemProgress.intervalDays = 1
        ELSE IF itemProgress.reviewCount == 2:
            itemProgress.intervalDays = 6
        ELSE:
            itemProgress.intervalDays = Math.round(itemProgress.intervalDays * itemProgress.easeFactor)
        
        // Update ease factor
        itemProgress.easeFactor = itemProgress.easeFactor + (0.1 - (5 - score) * (0.08 + (5 - score) * 0.02))
        itemProgress.easeFactor = Math.max(1.3, itemProgress.easeFactor)
    ELSE:
        // Incorrect answer - reset interval
        itemProgress.intervalDays = 1
        itemProgress.easeFactor = Math.max(1.3, itemProgress.easeFactor - 0.2)
    
    // Set next review date
    itemProgress.nextReviewAt = now() + itemProgress.intervalDays days
    itemProgress.lastReviewedAt = now()
    itemProgress.updatedAt = now()
    
    itemProgressRepository.save(itemProgress)
```

## 5. Validation Rules

### 5.1 Learning Cycle Validation
- **SetId**: Required, must exist and belong to user
- **PreferredSessionDuration**: Required, 5-120 minutes
- **DailyGoal**: Required, 1-100 items per day
- **User**: Must not have active cycle for same set

### 5.2 Review Session Validation
- **CycleId**: Required, must be active learning cycle
- **SessionType**: Required, must be valid enum value
- **User**: Must own the learning cycle

### 5.3 Answer Validation
- **ItemId**: Required, must be in current session
- **UserAnswer**: Required, max 2000 characters
- **ResponseTime**: Required, 0.1-300 seconds
- **Difficulty**: Required, must be valid enum value

## 6. Error Handling

### 6.1 Error Codes
- `REVIEW_001`: Learning cycle not found
- `REVIEW_002`: Cycle already active for set
- `REVIEW_003`: No items available for review
- `REVIEW_004`: Review session not found
- `REVIEW_005`: Session already active
- `REVIEW_006`: Invalid session status
- `REVIEW_007`: Item not found in session
- `REVIEW_008`: Cycle not active

## 7. Security Considerations

### 7.1 Data Isolation
- Users can only access their own learning cycles and sessions
- Validate ownership before any operations
- Prevent cross-user data access

### 7.2 Session Security
- Validate session ownership for all operations
- Implement session timeout (30 minutes)
- Log all session activities for audit

## 8. Observability

### 8.1 Logging
```java
// Log session start
log.info("Review session started", 
    "sessionId", session.getSessionId(), 
    "userId", session.getLearningCycle().getUser().getUserId(),
    "itemCount", session.getTotalItems());

// Log session completion
log.info("Review session completed", 
    "sessionId", session.getSessionId(), 
    "accuracy", session.getAccuracy(),
    "duration", session.getSessionDuration());
```

### 8.2 Metrics
- Active learning cycles
- Review sessions per day
- Average session accuracy
- Average response times
- SRS algorithm effectiveness

## 9. Testing Strategy

### 9.1 Unit Tests
- SRS algorithm calculations
- Score calculation logic
- Validation rules
- Business logic functions

### 9.2 Integration Tests
- API endpoint testing
- Database operations
- Session lifecycle
- Progress tracking

### 9.3 Performance Tests
- Large session handling
- Concurrent sessions
- SRS calculation performance
- Database query optimization

## 10. Dependencies

### 10.1 Internal Dependencies
- `LearningCycleRepository`: Cycle management
- `ReviewSessionRepository`: Session management
- `ReviewItemRepository`: Item tracking
- `ItemProgressRepository`: Progress tracking
- `LearningSetService`: Set validation

### 10.2 External Dependencies
- Database (PostgreSQL)
- Notification Service (for reminders)
- Time Service (for scheduling)

### 10.3 Configuration
```yaml
review-session:
  limits:
    max-session-duration: 120
    max-items-per-session: 50
    max-daily-goal: 100
  srs:
    initial-ease-factor: 2.5
    min-ease-factor: 1.3
    max-ease-factor: 3.0
  session:
    timeout-minutes: 30
    auto-save-interval: 60
```

---

**Document Version**: 1.0  
**Last Updated**: 2024-12-19  
**Next Review**: 2024-12-26  
**Owner**: Backend Team  
**Stakeholders**: Development Team, QA Team, Algorithm Team
