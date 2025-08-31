# Learning Cycle Sequence Diagrams

## Tổng quan

Tài liệu này mô tả các luồng sequence cho quá trình học tập theo chu kỳ SRS (Spaced Repetition System) trong hệ thống RepeatWise, bao gồm bắt đầu chu kỳ, thực hiện review và hoàn thành chu kỳ.

## 1. Start Learning Cycle Sequence

### 1.1 Successful Cycle Start

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{setId}/start
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: startCycle(setId, userId)
    
    CycleService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>CycleService: Set entity
    
    CycleService->>CycleService: validateSetOwnership(set, userId)
    CycleService->>CycleService: validateSetStatus(set)
    CycleService->>CycleService: checkExistingActiveCycle(setId, userId)
    
    CycleService->>CycleService: createCycleEntity(setId, userId)
    CycleService->>CycleRepository: save(cycle)
    CycleRepository->>Database: INSERT INTO cycles (set_id, user_id, status, current_review, total_reviews, created_at)
    Database-->>CycleRepository: Cycle created with ID
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService-->>CycleController: Cycle entity
    CycleController-->>APIGateway: 201 Created + Cycle data
    APIGateway-->>MobileApp: 201 Created + Cycle data
```

### 1.2 Start Cycle with Existing Active Cycle

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant SetRepository as Set Repository
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{setId}/start
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: startCycle(setId, userId)
    
    CycleService->>SetRepository: findById(setId)
    SetRepository->>Database: SELECT * FROM sets WHERE id = ?
    Database-->>SetRepository: Set data
    SetRepository-->>CycleService: Set entity
    
    CycleService->>CycleService: validateSetOwnership(set, userId)
    CycleService->>CycleService: validateSetStatus(set)
    CycleService->>CycleService: checkExistingActiveCycle(setId, userId)
    Note over CycleService: Active cycle found
    
    CycleService-->>CycleController: BusinessRuleError("Active cycle already exists")
    CycleController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 2. Perform Review Sequence

### 2.1 Successful Review with Score

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant ReviewRepository as Review Repository
    participant ReminderService as Reminder Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{cycleId}/review
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {score: 85, reviewDate: "2024-01-15T10:00:00Z"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: performReview(cycleId, score, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: validateCycleState(cycle)
    CycleService->>CycleService: validateScore(score)
    
    CycleService->>CycleService: calculateNextReviewDate(score, cycle.currentReview)
    CycleService->>CycleService: updateCycleProgress(cycle)
    
    CycleService->>ReviewRepository: saveReview(cycleId, score, reviewDate)
    ReviewRepository->>Database: INSERT INTO reviews (cycle_id, score, review_date, created_at)
    Database-->>ReviewRepository: Review saved
    ReviewRepository-->>CycleService: Review entity
    
    CycleService->>CycleRepository: update(cycle)
    CycleRepository->>Database: UPDATE cycles SET current_review = ?, next_review_date = ?, updated_at = ?
    Database-->>CycleRepository: Cycle updated
    CycleRepository-->>CycleService: Updated cycle
    
    CycleService->>ReminderService: scheduleNextReminder(cycle)
    ReminderService-->>CycleService: Reminder scheduled
    
    CycleService-->>CycleController: ReviewResult(cycle, review)
    CycleController-->>APIGateway: 200 OK + Review data
    APIGateway-->>MobileApp: 200 OK + Review data
```

### 2.2 Review with Invalid Score

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{cycleId}/review
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {score: 150, reviewDate: "2024-01-15T10:00:00Z"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: performReview(cycleId, score, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: validateCycleState(cycle)
    CycleService->>CycleService: validateScore(score)
    Note over CycleService: Score validation fails (score > 100)
    
    CycleService-->>CycleController: ValidationError("Score must be between 0 and 100")
    CycleController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 3. Complete Cycle Sequence

### 3.1 Successful Cycle Completion

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant StatisticsService as Statistics Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{cycleId}/complete
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: completeCycle(cycleId, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: validateCycleCompletion(cycle)
    CycleService->>CycleService: calculateCycleStatistics(cycleId)
    
    CycleService->>CycleService: updateCycleStatus(cycle, COMPLETED)
    CycleService->>CycleRepository: update(cycle)
    CycleRepository->>Database: UPDATE cycles SET status = 'COMPLETED', completed_at = ?, updated_at = ?
    Database-->>CycleRepository: Cycle updated
    CycleRepository-->>CycleService: Updated cycle
    
    CycleService->>StatisticsService: updateUserStatistics(userId, cycle)
    StatisticsService-->>CycleService: Statistics updated
    
    CycleService-->>CycleController: CycleCompletionResult(cycle, statistics)
    CycleController-->>APIGateway: 200 OK + Completion data
    APIGateway-->>MobileApp: 200 OK + Completion data
```

### 3.2 Complete Incomplete Cycle

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{cycleId}/complete
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: completeCycle(cycleId, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: validateCycleCompletion(cycle)
    Note over CycleService: Cycle not ready for completion (reviews < 5)
    
    CycleService-->>CycleController: BusinessRuleError("Cycle not ready for completion")
    CycleController-->>APIGateway: 400 Bad Request + Error message
    APIGateway-->>MobileApp: 400 Bad Request + Error message
```

## 4. Skip Review Sequence

### 4.1 Skip Review

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant ReminderService as Reminder Service
    participant Database as PostgreSQL

    MobileApp->>APIGateway: POST /api/cycles/{cycleId}/skip
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    Note over MobileApp,APIGateway: {reason: "busy", skipDate: "2024-01-15T10:00:00Z"}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: skipReview(cycleId, reason, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: validateCycleState(cycle)
    CycleService->>CycleService: calculateSkipPenalty(cycle)
    
    CycleService->>CycleService: updateCycleProgress(cycle)
    CycleService->>CycleRepository: update(cycle)
    CycleRepository->>Database: UPDATE cycles SET skip_count = ?, updated_at = ?
    Database-->>CycleRepository: Cycle updated
    CycleRepository-->>CycleService: Updated cycle
    
    CycleService->>ReminderService: rescheduleReminder(cycle)
    ReminderService-->>CycleService: Reminder rescheduled
    
    CycleService-->>CycleController: SkipResult(cycle)
    CycleController-->>APIGateway: 200 OK + Skip data
    APIGateway-->>MobileApp: 200 OK + Skip data
```

## 5. Get Cycle Status Sequence

### 5.1 Get Active Cycle Status

```mermaid
sequenceDiagram
    participant MobileApp as Mobile App
    participant APIGateway as API Gateway
    participant CycleController as Cycle Controller
    participant CycleService as Cycle Service
    participant CycleRepository as Cycle Repository
    participant Database as PostgreSQL

    MobileApp->>APIGateway: GET /api/cycles/{cycleId}/status
    Note over MobileApp,APIGateway: Authorization: Bearer {accessToken}
    
    APIGateway->>APIGateway: Validate JWT token
    APIGateway->>APIGateway: Extract user ID from token
    APIGateway->>CycleController: Forward request
    
    CycleController->>CycleService: getCycleStatus(cycleId, userId)
    
    CycleService->>CycleRepository: findById(cycleId)
    CycleRepository->>Database: SELECT * FROM cycles WHERE id = ?
    Database-->>CycleRepository: Cycle data
    CycleRepository-->>CycleService: Cycle entity
    
    CycleService->>CycleService: validateCycleOwnership(cycle, userId)
    CycleService->>CycleService: calculateCycleProgress(cycle)
    CycleService->>CycleService: getNextReviewInfo(cycle)
    
    CycleService-->>CycleController: CycleStatus(cycle, progress, nextReview)
    CycleController-->>APIGateway: 200 OK + Status data
    APIGateway-->>MobileApp: 200 OK + Status data
```

## 6. SRS Algorithm Calculation

### 6.1 Calculate Next Review Date

```mermaid
sequenceDiagram
    participant CycleService as Cycle Service
    participant SRSService as SRS Service
    participant Database as PostgreSQL

    CycleService->>SRSService: calculateNextReviewDate(score, currentReview)
    
    SRSService->>SRSService: getSRSIntervals(currentReview)
    SRSService->>SRSService: calculateDelay(score, baseInterval)
    
    alt Score >= 80 (Good)
        SRSService->>SRSService: nextInterval = baseInterval * 1.5
    else Score >= 60 (Fair)
        SRSService->>SRSService: nextInterval = baseInterval * 1.0
    else Score < 60 (Poor)
        SRSService->>SRSService: nextInterval = baseInterval * 0.5
    end
    
    SRSService->>SRSService: applyOverloadPrevention(nextInterval)
    SRSService->>SRSService: calculateNextReviewDate(currentDate, nextInterval)
    
    SRSService-->>CycleService: Next review date
```

## Ghi chú kỹ thuật

### 1. SRS Algorithm
- **Base intervals**: 1, 3, 7, 14, 30 days
- **Score-based multipliers**: 
  - 80-100%: 1.5x (tăng interval)
  - 60-79%: 1.0x (giữ nguyên)
  - 0-59%: 0.5x (giảm interval)
- **Overload prevention**: Giới hạn 3 set/ngày

### 2. Business Rules
- Mỗi set chỉ có 1 active cycle tại một thời điểm
- Cycle cần 5 reviews để hoàn thành
- Score phải từ 0-100%
- Không thể complete cycle chưa đủ reviews

### 3. Validation
- Cycle ownership validation
- Cycle state validation
- Score range validation
- Review timing validation

### 4. Performance
- SRS calculation được cache
- Review history được index
- Cycle status được pre-calculate
- Reminder scheduling được optimize
